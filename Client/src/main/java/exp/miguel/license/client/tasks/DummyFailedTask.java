package exp.miguel.license.client.tasks;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 2:47 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class DummyFailedTask extends DummySuccessfulTask {
	public DummyFailedTask(long id, int timeSeconds) {
		super(id, timeSeconds);
	}

	@Override
	protected String complete() {
		throw new IllegalStateException("Failed");
	}
}
