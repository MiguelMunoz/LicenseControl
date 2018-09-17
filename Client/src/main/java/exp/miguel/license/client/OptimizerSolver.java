package exp.miguel.license.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import io.swagger.client.model.Constants;
import io.swagger.client.model.RequestDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static exp.miguel.license.LicenseConstants.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/16/18
 * <p>Time: 12:31 AM
 *
 * @author Miguel Mu\u00f1oz
 */

@SuppressWarnings({"HardCodedStringLiteral", "UseOfSystemOutOrSystemErr"})
public class OptimizerSolver {
	private static final Logger log = LoggerFactory.getLogger(OptimizerSolver.class);
	private static final long TO_MILLIS = 1000L;

	private static MessageFacade messageFacade = new LicenseConnection();
	private LicenseTask task;
	private final Constants constants;
	private volatile boolean running = true;
	private Thread keepAliveThread;

	public OptimizerSolver(LicenseTask task) throws LicenseException {
		this.task = task;
		constants = messageFacade.requestConstants();
		log.debug("Constants: {}", constants);
		log.debug("Keep Alive: ", constants.getKeepAliveTimeMillis());
//		System.err.printf("Constants: %s%nKeepAlive: %d%n", constants, constants.getKeepAliveTimeMillis()); // NON-NLS
	}

	/**
	 * Solves the problem, after obtaining a license from the license broker. 
	 * @return The solution, or null if the problem failed to complete.
	 */
	public String solve() throws LicenseException {
		RequestDetail reply = messageFacade.requestLicense(null);
		log.info("requestLicense() reply: {}", reply);
		if (OKAY.equals(reply.getAuthority())) {
			return start(reply.getId());
		}
		return doSolve(reply);
	}

	private String doSolve(RequestDetail reply) throws LicenseException {
		long keepAlive = constants.getKeepAliveTimeMillis();
		log.error("Keep Alive = {}", keepAlive);
//		System.err.printf("Keep-alive: %s%n", keepAlive); // NON-NLS
//		if (keepAlive == 0) {
//			keepAlive = 7000L;
//		}
		while (true) {
			long startTime = System.currentTimeMillis();
			try {
				Thread.sleep(getWait(keepAlive, startTime));
				reply = messageFacade.requestLicense(reply.getId());
				if (OKAY.equals(reply.getAuthority())) {
					return start(reply.getId());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private String start(String id) throws LicenseException {
		launchKeepAliveThread(id);

		String result = null;
		try {
			task.run();
			result = task.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			System.out.printf("Start Exception: %s%n", e.getLocalizedMessage());
			e.printStackTrace(System.out);
			throw new LicenseException(e);
		} catch (Throwable t) {
			running = false;
			keepAliveThread.interrupt();
			//noinspection ProhibitedExceptionThrown
			throw t;
		}
		running = false;
		keepAliveThread.interrupt();
		return result;
	}

	private static AtomicInteger keepAliveLaunchCount = new AtomicInteger(0);
	private static AtomicInteger getKeepAliveFinishCount = new AtomicInteger(0);
	private void launchKeepAliveThread(final String id) {
		Runnable runner = () -> {
			keepAliveLaunchCount.incrementAndGet();
			long keepAlive = constants.getKeepAliveTimeMillis();
			long startTime = System.currentTimeMillis();
			while (running) {
				long wait = getWait(keepAlive, startTime);
				System.out.printf("id %s sleeping for %d milliseconds%n", id, wait);// NON-NLS
				try {
					Thread.sleep(wait);
					messageFacade.submitStillAlive(id);
				} 
				// We can ignore any exceptions sending the keep alive signal, because we have already have a licence.
				// The exceptions can only be because the server went down, but we don't worry about that anymore.
				catch (LicenseException ex) {
					System.out.printf("Wait.exception: %s%n", ex.getLocalizedMessage());
					ex.printStackTrace(System.out);
				} // running will be true here.
				catch (InterruptedException e) {
					// It's tempting to put "running = false" right here, but we might not be inside the sleep call when
					// this thread gets interrupted. Setting it to false before interrupting guarantees the loop will terminate.
					Thread.currentThread().interrupt();
				}
			}
			try {
				System.out.printf("OS.Completed %s%n", id);
				messageFacade.submitCompleted(id);
				// We can ignore this exception for the same reason.
			} catch (LicenseException ignored) { }
			getKeepAliveFinishCount.incrementAndGet();
			System.out.printf("KeepAlive Threads: Launched %d, Completed %d%n", keepAliveLaunchCount.get(), getKeepAliveFinishCount.get());
		};
		keepAliveThread = new Thread(runner);
//		keepAliveThread.setDaemon(true);
		keepAliveThread.start();
	}

	private long getWait(final long keepAlive, final long startTime) {
		long now = System.currentTimeMillis();
		long duration = now - startTime;
		//noinspection TooBroadScope
		return keepAlive - (duration % keepAlive);
	}

	public static void setNewLimit(int limit) throws LicenseException {
		messageFacade.setLimit(limit);
	}
}
