package exp.miguel.license.broker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;
import exp.miguel.license.LicenseConstants;
import io.swagger.model.RequestDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/18
 * <p>Time: 3:44 PM
 *
 * @author Miguel Mu\u00f1oz
 */
//@Component
@SuppressWarnings("Singleton")
public enum IdLog {
	instance;

	private static final Logger log = LoggerFactory.getLogger(IdLog.class);

	@GuardedBy("LOCK")
	private final Map<String, Long> aliveRunners = new HashMap<>();
	@GuardedBy("LOCK")
	private final Map<String, Long> aliveWaiters = new LinkedHashMap<>();

	private final AtomicInteger licenseLimit = new AtomicInteger();
	private final AtomicInteger idSource = new AtomicInteger();
	private final Object LOCK = new Object();
	
	// for logging only:
	private final long startTime = System.currentTimeMillis();
	
//	private enum Ops = {add, }

	IdLog() {
		setLimit(LicenseLimit.getLimit());
		launchGraveDigger();
	}
	
	/* ****************
	 | set license limit
	 * ****************/

	public void setLimit(int limit) {
		licenseLimit.set(limit);
		LicenseLimit.setLimit(limit); // save the new value.
	}

	/* *************
	 | getLicense
	 * *************/

	public RequestDetail getLicense() {
		final String id = String.valueOf(idSource.incrementAndGet());
		log.debug("new licence id={}", id);
		return getLicense(id);
	}
	
	public RequestDetail getLicense(String id) {
		synchronized (LOCK) {
			final int limit = licenseLimit.get();
			int openSlots = limit - aliveRunners.size();
			if (isInOpenSlot(openSlots, id)) {
				return doLaunch(id);
			} else if (openSlots > aliveWaiters.size()) {
				return doLaunch(id);
			}
		}
		RequestDetail requestDetail = createRequestDetail(id, LicenseConstants.WAIT);
		final String idString = requestDetail.getId();
		return doWait(requestDetail, idString);
	}

	/**
	 * For example, if the RunningSet has three open slots, this will return true if the id is in one of the first
	 * three positions in the Waiting set.
	 * @param openSlots The number of open slots in the running set
	 * @param id The id to look for.
	 * @return true if the id should be added to the running set, false otherwise
	 */
	@GuardedBy("LOCK")
	private boolean isInOpenSlot(int openSlots, String id) {
		Iterator<String> iterator = aliveWaiters.keySet().iterator();
		while ((openSlots > 0) && iterator.hasNext()) {
			//noinspection EqualsReplaceableByObjectsCall
			if (iterator.next().equals(id)) {
				log.debug("Found id {} in open slot", id);
				return true;
			}
			openSlots--;
		}
		return false;
	}
	
	private RequestDetail doWait(final RequestDetail requestDetail, final String id) {
		synchronized (LOCK) {
			keepWaiterAlive(id);
		}
		return requestDetail;
	}

	@GuardedBy("LOCK")
	private RequestDetail doLaunch(String id) {
		keepRunnerAlive(id); // This puts the id onto the aliveRunners set if it's not there. 
		RequestDetail requestDetail = createRequestDetail(id, LicenseConstants.OKAY);
		log.debug("                    {} waiting threads: {}", aliveWaiters.size(), debugLimitedToString(aliveWaiters.keySet()));
		log.debug("* Launching id {} for {} running threads: {} ********", id, aliveRunners.size(), aliveRunners.keySet());
		log.debug("task {} no longer waiting", id);
		aliveWaiters.remove(id);
		log.debug("Remaining Waiters: {}", aliveWaiters.keySet());
		return requestDetail;
	}
	
	@GuardedBy("LOCK")
	private void keepRunnerAlive(final String id) {
		aliveRunners.put(id, System.currentTimeMillis());
		log.debug("Keeping id {} alive with {} runners and {} waiting", id, aliveRunners.size(), aliveWaiters.size());
	}

	private RequestDetail createRequestDetail(final String id, final String authority) {
		RequestDetail requestDetail = new RequestDetail();
		requestDetail.setAuthority(authority);
		requestDetail.setId(id);
		return requestDetail;
	}

	@GuardedBy("LOCK")
	private void keepWaiterAlive(String id) {
		aliveWaiters.put(id, System.currentTimeMillis());
		log.debug("Request id {} alive at {} for {} runners: {} and {} waiters: {}", 
				id, 
				aliveWaiters.get(id)-startTime,
				aliveRunners.size(),
				aliveRunners.keySet(),
				aliveWaiters.size(), 
				debugLimitedToString(aliveWaiters.keySet())
		);
	}

	/* ****************
	 | keepAlive
	 * ****************/
	
	public void keepAlive(String id) {
		// Sometimes this message gets processed just after the completed message let to its removal. In this case, 
		// we don't want to put it back, so we look to ensure it's still there.
		synchronized (LOCK) {
			if (aliveRunners.keySet().contains(id)) {
				keepRunnerAlive(id);
			}
		}
	}
	
	/* ****************
	 | complete
	 * ****************/

	public void complete(String id) {
		synchronized (LOCK) {
			aliveRunners.remove(id);
		}
		log.debug("....              {} waiting threads = {}... $$$$$$$$", aliveWaiters.size(), debugLimitedToString(aliveWaiters.keySet()));
		log.debug("* Completing id {}: {} running threads = {}    $$$$$$$$", id, aliveRunners.size(), aliveRunners.keySet());
	}
	
	private void launchGraveDigger() {
		Runnable runner = () -> {
			//noinspection OverlyBroadCatchBlock
			try {
				final long keepAliveLimit = 3 * LicenseLimit.getKeepAliveMilliseconds();
				final long sleepTime = keepAliveLimit/10;
				//noinspection InfiniteLoopStatement
				while (true) {
					Thread.sleep(sleepTime);
					long now = System.currentTimeMillis();
					long deadLimit = now - keepAliveLimit;
					//noinspection FieldAccessNotGuarded
					removeDeadEntries(deadLimit, aliveRunners);
					//noinspection FieldAccessNotGuarded
					removeDeadEntries(deadLimit, aliveWaiters);
				}
			} catch (Throwable e) {
				// This should never happen, but we need to know if it does!
				log.error("Serious Error: {}", e.getLocalizedMessage(), e);
			}
		};
		Thread graveDigger = new Thread(runner, "IdLog.GraveDiggerThread");
		graveDigger.setDaemon(true);
		graveDigger.start();
	}

	private void removeDeadEntries(final long deadLimit, final Map<String, Long> entries) {
		synchronized (LOCK) {
			Iterator<Map.Entry<String, Long>> iterator = entries.entrySet().iterator();
			while (iterator.hasNext()) {
				final Map.Entry<String, Long> stringLongEntry = iterator.next();
				long previousTime = stringLongEntry.getValue();
				if (previousTime < deadLimit) {
					log.debug("Found dead id {} in {}", stringLongEntry.getKey(), entries.keySet());
					iterator.remove();
					log.debug("Removed as {}", entries.keySet());
					//noinspection ObjectEquality
					log.debug("Removing Dead Task id {} from {} with remaining: {}", 
							stringLongEntry.getKey(), 
							((entries == aliveRunners) ? "runners" : "waiters"),// NON-NLS
							debugLimitedToString(entries.keySet())
					);
				}
			}
		}
	}
	
	private static <T> String debugLimitedToString(Iterable<T> iterable) {
		int limit = LicenseLimit.getLimit();
		Iterator<T> iterator = iterable.iterator();
		StringBuilder builder = new StringBuilder("[");
		if ((limit > 0) && iterator.hasNext()) {
			builder.append(iterator.next());
			limit--;
		}
		while ((limit > 0) && iterator.hasNext()) {
			builder.append(", ").append(iterator.next());
			limit--;
		}
		if (iterator.hasNext()) {
			builder.append("... ]");
		} else {
			//noinspection MagicCharacter
			builder.append(']');
		}
		return builder.toString();
	}
	
	@SuppressWarnings("FieldAccessNotGuarded")
	boolean testOnlyIsRunning(String id) {
		return aliveRunners.keySet().contains(id);
	}

	@SuppressWarnings("FieldAccessNotGuarded")
	boolean testOnlyIsWaiting(String id) {
		return aliveWaiters.keySet().contains(id);
	}
}
