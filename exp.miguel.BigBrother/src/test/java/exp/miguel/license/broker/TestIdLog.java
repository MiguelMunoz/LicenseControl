package exp.miguel.license.broker;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import io.swagger.model.RequestDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/16/18
 * <p>Time: 11:09 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class TestIdLog {

	private static final Logger log = LoggerFactory.getLogger(TestIdLog.class);
	private static final long TO_MILLIS = 1000L;
	private static final String OKAY = "Okay";
	private static final int MAX = 20;

	public static void main(String[] args) {
		new TestIdLog().testLog();
	}
//	@Test
	private void testLog() {
		Deque<String> waiting = new LinkedList<>();
		Deque<String> running = new LinkedList<>();
		IdLog idLog = IdLog.instance;
		for (int ii = 0; ii< MAX; ++ii) {
			RequestDetail detail = idLog.getLicense();
			String authority = detail.getAuthority();
			final String id = detail.getId();
			if (OKAY.equals(authority)) {
				running.add(id);
			} else {
				waiting.add(id);
				log.debug("Added {} to Waiting: {}", id, waiting);
			}
			cleanRunners(running);
			cleanWaiters(waiting);
			waitSeconds(1);
		}

		while (!running.isEmpty() || !waiting.isEmpty()) {
			if (!running.isEmpty()) {
				idLog.complete(running.pop());
			}
			waitSeconds(1);
			cleanRunners(running);
			cleanWaiters(waiting);
			Iterator<String> itr = waiting.descendingIterator();
			for (int ii=0; ii<10; ++ii) {
				if (itr.hasNext()) {
					RequestDetail detail = idLog.getLicense(itr.next());
					if (OKAY.equals(detail.getAuthority())) {
						log.debug("test launched {}", detail.getId());
						itr.remove();
						running.add(detail.getId());
					}
				}
			}
		}
	}
	
	private void cleanRunners(Deque<String> list) {
		IdLog idLog = IdLog.instance;
		for (int i = 0; i< MAX; ++i) {
			String id = String.valueOf(i);
			if (!idLog.testOnlyIsRunning(String.valueOf(id))) {
				list.remove(id);
			}
		}
	}

	private void cleanWaiters(Deque<String> list) {
		IdLog idLog = IdLog.instance;
		for (int i = 0; i < MAX; ++i) {
			String id = String.valueOf(i);
			if (!idLog.testOnlyIsWaiting(String.valueOf(id))) {
				list.remove(id);
			}
		}
	}

	private void waitSeconds(int seconds) {
		try {
			Thread.sleep(seconds* TO_MILLIS);
		} catch (InterruptedException ignored) { }
	}
}
