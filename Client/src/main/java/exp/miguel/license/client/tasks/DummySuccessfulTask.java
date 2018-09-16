package exp.miguel.license.client.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 2:30 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class DummySuccessfulTask implements LicenseTask, Callable<String> {

	private static final long TO_MILLIS = 1000L;
	private static final String OKAY = "Okay";
	private long milliSeconds;

	private FutureTask<String> theFuture;
	private final String result;
	private final String id;

	public DummySuccessfulTask(long id, int timeSeconds) {
		this(id, timeSeconds, OKAY);
	}

	public DummySuccessfulTask(long id, int timeSeconds, String data) {
		this.id = String.valueOf(id);
		milliSeconds = timeSeconds * TO_MILLIS;
		result = data;
		theFuture = new FutureTask<>(this);
	}

	public String getId() {
		return id;
	}

	@Override
	public String call() {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException ignored) { }
		return complete();
	}

	protected String complete() {
		return result;
	}

	public Future<String> getFuture() {
		return theFuture;
	}
}
