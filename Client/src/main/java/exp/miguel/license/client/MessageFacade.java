package exp.miguel.license.client;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 1:09 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public interface MessageFacade {
	
//	int  requestId();
	String requestLicense(String id);
	void submitStillAlive(int id);
	void submitCompleted(int id);
}
