package exp.miguel.license.client;

import java.util.Objects;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
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
@Component
public class LicenseConnection implements MessageFacade {
	private static final Logger log = LoggerFactory.getLogger(LicenseConnection.class);

	private final JmsTemplate jmsTemplate;

	// I should be using the constant defined in BigBrother, but Maven is failing to find it, in spite of the
	// dependency in the maven file.
	private static final String LAUNCH_TOPIC = "LaunchTopic";

//	private final String authorityUrl;
//	private final InitialContext initialContext;
	public LicenseConnection(JmsTemplate template) {
		jmsTemplate = template;
	}

	@Override
	public String requestLicense(String id) {
		Destination destination = new ActiveMQTopic();
		Message reply = jmsTemplate.sendAndReceive(LAUNCH_TOPIC, session -> session.createTextMessage(id));
		if (reply instanceof TextMessage) {
			try {
				return ((TextMessage)reply).getText();
			} catch (JMSException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		return Objects.toString(reply);
	}

	@Override
	public void submitStillAlive(final int id) {

	}

	@Override
	public void submitCompleted(final int id) {

	}
}
