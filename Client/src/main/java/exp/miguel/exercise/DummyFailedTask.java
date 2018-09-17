package exp.miguel.exercise;

import exp.miguel.exercise.DummySuccessfulTask;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 2:47 AM
 *
 * @author Miguel Mu\u00f1oz
 */
class DummyFailedTask extends DummySuccessfulTask {
	DummyFailedTask(int timeSeconds) {
		super(timeSeconds, false);
	}
}
