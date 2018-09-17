package exp.miguel.license.client;

import java.util.Objects;
//import javax.jms.Connection;
//import javax.jms.ConnectionFactory;
//import javax.jms.Destination;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//import javax.jms.Topic;
//import javax.jms.TopicConnection;
//import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.activemq.command.ActiveMQTopic;
import io.swagger.client.ApiException;
import io.swagger.client.api.DevelopersApi;
import io.swagger.client.model.Constants;
import io.swagger.client.model.RequestDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

// commented out because Maven is failing to include the dependency on the BigBrother jar file. I don't know why.
//import static exp.miguel.license.BigBrother.LAUNCH_TOPIC;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/13/18
 * <p>Time: 2:04 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("OverlyBroadCatchBlock")
@Component
public class LicenseConnection implements MessageFacade {
	private static final Logger log = LoggerFactory.getLogger(LicenseConnection.class);

//	private final JmsTemplate jmsTemplate;

	// I should be using the constant defined in BigBrother, but Maven is failing to find it, in spite of the
	// dependency in the maven file.
//	private static final String LAUNCH_TOPIC = "LaunchTopic";
	private final DevelopersApi apiInstance;

	//	private final String authorityUrl;
//	private final InitialContext initialContext;
	public LicenseConnection() {
//		jmsTemplate = template;
		apiInstance = new DevelopersApi();
	}

	@Override
	public RequestDetail requestLicense(String id) throws LicenseException {
		try {
			if ((id == null) || id.isEmpty()) {
				return apiInstance.requestLicense();
			} else {
				return apiInstance.requestLicenseAgain(id);
			}
		} catch (Throwable e) {
			throw new LicenseException(e);
		}
	}

	@Override
	public void submitStillAlive(final String id) throws LicenseException {
		try {
			apiInstance.stillAlive(id);
		} catch (Throwable e) {
			throw new LicenseException(e);
		}
	}

	@Override
	public void submitCompleted(final String id) throws LicenseException {
		try {
			apiInstance.complete(id);
		} catch (Throwable e) {
			throw new LicenseException(e);
		}
	}

	@Override
	public Constants requestConstants() throws LicenseException {
		try {
			return apiInstance.constants();
		} catch (Throwable e) {
			throw new LicenseException(e);
		}
	}

	@Override
	public void setLimit(int limit) throws LicenseException {
		try {
			apiInstance.licenseCount(limit);
		} catch (ApiException e) {
			throw new LicenseException(e);
		}
	} 
	
	
}
