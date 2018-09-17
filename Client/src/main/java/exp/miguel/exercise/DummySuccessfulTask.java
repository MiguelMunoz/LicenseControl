package exp.miguel.exercise;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import exp.miguel.license.client.LicenseTask;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 2:30 AM
 *
 * @author Miguel Mu\u00f1oz
 */
class DummySuccessfulTask extends FutureTask<String> implements LicenseTask {

	private static final long TO_MILLIS = 1000L;
	private static final String OKAY = "Okay";

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
		return () -> {
			System.out.printf("Task sleeping for %d ms%n", millis);
			try {
				Thread.sleep(millis);
			} catch (InterruptedException ignored) { }
			if (!doComplete) {
				Thread.dumpStack();
				throw new IllegalStateException("Failed"); // for tasks that fail.
			}
			return result;
		};
	}
}
