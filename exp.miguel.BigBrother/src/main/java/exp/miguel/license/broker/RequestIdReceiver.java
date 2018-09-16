package exp.miguel.license.broker;

import javax.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.stereotype.Component;

import static exp.miguel.license.BigBrother.LAUNCH_TOPIC;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/18
 * <p>Time: 3:39 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class RequestIdReceiver {
	private final IdLog idLog;

	@Autowired
	public RequestIdReceiver(IdLog idLog) {
		this.idLog = idLog;
	}
	private static final Logger log = LoggerFactory.getLogger(RequestIdReceiver.class);
	
	@JmsListener(destination = LAUNCH_TOPIC, containerFactory = "myFactory")
	public void receiveMessage(String id) {
		log.info("Received <{}>", id);
		log.info("idLog: {}", idLog);
	}

//	@JmsListener(destination = LAUNCH_TOPIC)
//	public void processMessage(String content) {
//		log.info("Received {} on topic {}", content, LAUNCH_TOPIC);
//	}

	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
	                                                DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all boot's default to this factory, including the message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some of Boot's default if necessary.
		log.info("Installing myFactory: JmsListenerContainerFactory");
		return factory;
	}


}
