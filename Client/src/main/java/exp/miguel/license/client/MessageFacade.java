package exp.miguel.license.client;

import io.swagger.client.model.Constants;
import io.swagger.client.model.RequestDetail;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 1:09 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public interface MessageFacade {

	/**
	 * Request a license. Throws an exception if the server goes down.
	 * @param id The id if this task has already been submitted before
	 * @return a RequestDetail with an id and a license status of "Okay" or "Wait"
	 */
	RequestDetail requestLicense(String id) throws LicenseException;
	void submitStillAlive(String id) throws LicenseException;
	void submitCompleted(String id) throws LicenseException;
	Constants requestConstants() throws LicenseException;
	void setLimit(int limit) throws LicenseException;
}
