package exp.miguel.license.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
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

	private static MessageFacade messageFacade = new LicenseConnection();
	private LicenseTask task;
	private final Constants constants;
	private final AtomicBoolean running = new AtomicBoolean(true);
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

		if (OKAY.equals(reply.getAuthority())) {
			return start(reply.getId());
		}
		return waitForLicense(reply);
	}

	private String waitForLicense(RequestDetail reply) throws LicenseException {
		long keepAlive = constants.getKeepAliveTimeMillis();
		log.info("Keep Alive = {}", keepAlive);
//		System.err.printf("Keep-alive: %s%n", keepAlive); // NON-NLS
//		if (keepAlive == 0) {
//			keepAlive = 7000L;
//		}
		while (true) {
			long startTime = System.currentTimeMillis();
			try {
				Thread.sleep(getWait(keepAlive/2, startTime));
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
			log.debug("OS.Completed {}", id);
			System.out.printf("OS.Completed %s%n", id);
			messageFacade.submitCompleted(id);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.debug("OS.Task id {} threw an exception.", id, e);
			System.err.printf("OS.Task id %s threw an exception.%n", id); // NON-NLS
			e.printStackTrace();
		} catch (ExecutionException e) {
//			e.printStackTrace(System.out);
			log.debug("OS.Task id {} threw an exception.", id, e);
			System.err.printf("OS.Task id %s threw an exception.%n", id); // NON-NLS
			e.printStackTrace();
			throw new LicenseException(e);
		} catch (RuntimeException | Error t) {
			log.debug("OS.Task id {} threw an exception.", id, t);
			System.err.printf("OS.Task id %s threw an exception.%n", id); // NON-NLS
			t.printStackTrace();
			throw new LicenseException(t);
		}
		finally {
			running.set(false);
			keepAliveThread.interrupt();
		}
		return result;
	}
	
	private static AtomicInteger dbgKeepAliveLaunchCount = new AtomicInteger(0);
	private static AtomicInteger dbgKeepAliveFinishCount = new AtomicInteger(0);
	private void launchKeepAliveThread(final String id) {
		Runnable runner = () -> {
			dbgKeepAliveLaunchCount.incrementAndGet();
			long keepAlive = constants.getKeepAliveTimeMillis();
			long startTime = System.currentTimeMillis();
			while (running.get()) {
				long wait = getWait(keepAlive, startTime);
				try {
					Thread.sleep(wait);
					messageFacade.submitStillAlive(id);
				} 
				// We can ignore any exceptions sending the keep alive signal, because we have already have a licence.
				// The exceptions can only be because the server went down, but we don't worry about that anymore.
				catch (LicenseException ex) {
					log.debug("Wait.exception: {}", ex.getLocalizedMessage());
				} // running will be true here.
				catch (InterruptedException e) {
					// It's tempting to put "running = false" right here, but we might not be inside the sleep call when
					// this thread gets interrupted. Setting it to false before interrupting guarantees the loop will terminate.
					Thread.currentThread().interrupt();
				}
			}
			dbgKeepAliveFinishCount.incrementAndGet();
			log.debug("KeepAlive Threads: Launched {}, Completed {}", dbgKeepAliveLaunchCount.get(), dbgKeepAliveFinishCount.get());
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
