package exp.miguel.license.broker;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/16/18
 * <p>Time: 5:41 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("UtilityClassCanBeEnum")
public final class LicenseLimit {
	;
	private static final Logger log = LoggerFactory.getLogger(LicenseLimit.class);
	private LicenseLimit() { }

	private static final String KEEP_ALIVE_TIME = "keep.alive.time";
	private static final String LICENSE_LIMIT = "license.limit";
	// Usually, this would be done in a database, but I'm not going to go through the trouble of adding database
	// integration, just to create a single table, with just two columns, holding a single record, especially since
	// I had already written the PropertyFile class on another project.
	private static PropertyFile propertiesFile 
			= new PropertyFile(String.format("%s/BigBrother", System.getProperty("user.home"))); // NON-NLS

	private static long keepAliveMillis = 6000L;

	private static final String FIVE_SECOND_DURATION = "pt5s";

	static {
		// Initialize if it doesn't exist.
		String limit = propertiesFile.getProperty(LICENSE_LIMIT, "5");
		propertiesFile.setPropertyAndSave(LICENSE_LIMIT, limit);

		log.debug("Default keep-alive millis: {} ms", keepAliveMillis);
		propertiesFile.setPropertyAndSave(KEEP_ALIVE_TIME, FIVE_SECOND_DURATION);
		String keepAlive = propertiesFile.getProperty(KEEP_ALIVE_TIME, FIVE_SECOND_DURATION);
		log.debug("Keep-Alive from properties file: {}", keepAlive);
		propertiesFile.setPropertyAndSave(KEEP_ALIVE_TIME, keepAlive);
		log.debug("Parsing {}", keepAlive);
		Duration keepAliveTime = Duration.parse(keepAlive);
		keepAliveMillis = keepAliveTime.toMillis();
		log.debug("final value: {}", keepAliveMillis);
	}
	
	public static int getLimit() {
		return Integer.valueOf(propertiesFile.getProperty(LICENSE_LIMIT));
	}
	
	@SuppressWarnings("WeakerAccess")
	public static void setLimit(int newLimit) {
		propertiesFile.setPropertyAndSave(LICENSE_LIMIT, String.valueOf(newLimit));
	}
	
	public static long getKeepAliveMilliseconds() { return keepAliveMillis; }
	public static void setKeepAliveMilliseconds(long keepAliveMilliseconds) { keepAliveMillis = keepAliveMilliseconds; }
}
