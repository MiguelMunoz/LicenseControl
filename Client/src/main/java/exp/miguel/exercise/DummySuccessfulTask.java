package exp.miguel.exercise;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import exp.miguel.license.client.LicenseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 2:30 AM
 *
 * @author Miguel Mu\u00f1oz
 */
class DummySuccessfulTask extends FutureTask<String> implements LicenseTask {
	private static final Logger log = LoggerFactory.getLogger(DummySuccessfulTask.class);
	private static final long TO_MILLIS = 1000L;
	private static final String OKAY = "Okay";
	private static final int HASH_TAIL = 8192;

//	private FutureTask<String> theFuture;
	
	DummySuccessfulTask(int timeSeconds) {
		this(timeSeconds, true);
	}

	DummySuccessfulTask(int timeSeconds, boolean complete) {
		this(timeSeconds, OKAY, complete);
	}

	private DummySuccessfulTask(int timeSeconds, String data, boolean doComplete) {
		super(makeCallable(timeSeconds * TO_MILLIS, data, doComplete));
	}

	private static Callable<String> makeCallable(long millis, String result, boolean doComplete) {
		return new Callable<String>() {
			@Override
			public String call() {
				log.debug("new task will complete/fail in %d ms", millis);
				try {
					Thread.sleep(millis);
				} catch (InterruptedException ignored) { }
				if (!doComplete) {
					log.debug("Taskk {} crashed", hashCode() % HASH_TAIL);
					System.err.printf("Taskk %S crashed%n", hashCode() % HASH_TAIL); // NON-NLS
					throw new IllegalStateException("Failed"); // for tasks that fail.
				}
				log.debug("Taskk {} complete", hashCode() % HASH_TAIL);
				System.err.printf("Taskk %s complete%n", hashCode() % HASH_TAIL); // NON-NLS
				return result;
			}
		};
	}
}
