package exp.miguel.license.broker;

/*
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/16/18
 * <p>Time: 5:38 AM
 *
 * @author Miguel Mu\u00f1oz
 */
/*
 * Used to implement features described in Effective Java, 3rd Edition
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * Class to safely wrap the troublesome Properties class, with improved support for saving properties to files.
 * Consequently, this class can't be created without specifying a File path.
 * <p>
 * Eventually, this should be changed to PropertyStore, with more flexible constructors to allow storing in other
 * kinds of storage.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public final class PropertyFile {
	private final String filePath;
	private final Properties properties = new Properties();

	/**
	 * Create a new Properties file at the specified path
	 *
	 * @param path path to the file, relative to {@code System.getProperty("user.dir")}
	 */
	public PropertyFile(String path) {
		filePath = path;
		doLoad();
	}

	public final void load() {
		doLoad();
	}

	private void doLoad() {
		File file = getFile();
		//noinspection OverlyBroadCatchBlock
		try {
			if (!file.exists()) {
				createPath(file, false);
			}
			loadProperties(file);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("OverlyBroadThrowsClause")
	private void loadProperties(File file) throws IOException {
		try (FileReader reader = new FileReader(file)) {
			properties.load(reader);
		}
	}

	private File getFile() {
		//noinspection AccessOfSystemProperties
		return new File(System.getProperty("user.dir"), filePath);
	}

	public final void saveFile() {
		doSave();
	}

	private void doSave() {
		File file = getFile();
		try {
			Writer writer = new FileWriter(file);
			properties.store(writer, "");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static boolean createPath(File file, boolean asDir) throws IOException {
		File parentFile = file.getParentFile();
		boolean success = true;
		if (!parentFile.exists()) {
			success = createPath(parentFile, true);
		}
		if (success) {
			if (asDir) {
				success = file.mkdir();
			} else {
				success = file.createNewFile();
			}
		}
		return success;
	}

	/**
	 * May return null
	 *
	 * @param propertyName the key
	 * @param value        the value
	 * @return the previous value, or null of no such value was set.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public final String setProperty(String propertyName, String value) {
		return (String) properties.setProperty(propertyName, value);
	}

	public final String setPropertyAndSave(String propertyName, String value) {
		String priorValue = setProperty(propertyName, value);
		saveFile();
		return priorValue;
	}

	public final void setPropertiesAndSave(Map<String, String> propertyMap) {
		for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue());
		}
		saveFile();
	}

	/**
	 * May return null
	 *
	 * @param propertyName key
	 * @return value, which may be null if the key is not present
	 */
	public final String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

	public final String getProperty(String propertyName, String defaultValue) {
		return properties.getProperty(propertyName, defaultValue);
	}
}
