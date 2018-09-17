package exp.miguel.license.client;

/**
 * Exception meant only for wrapping other exceptions. This is why every constructor takes a cause.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/16/18
 * <p>Time: 4:41 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public class LicenseException extends Exception {
	LicenseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	LicenseException(Throwable cause) {
		super(cause);
	}
}
