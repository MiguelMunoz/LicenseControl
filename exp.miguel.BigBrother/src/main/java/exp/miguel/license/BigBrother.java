package exp.miguel.license;

import java.time.Duration;
import exp.miguel.license.broker.LicenseLimit;
import exp.miguel.license.broker.PropertyFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//import org.apache.activemq.broker.BrokerService;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.jms.config.JmsListenerEndpoint;
//import org.springframework.jms.listener.MessageListenerContainer;
//import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/18
 * <p>Time: 2:54 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {"exp.miguel.license.broker", "exp.miguel.license", "io.swagger", "io.swagger.api"})
public class BigBrother implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(BigBrother.class);

	private int licenseLimit = LicenseLimit.getLimit();

	public static void main(String[] args) {
//		ConfigurableApplicationContext context = 
		SpringApplication.run(BigBrother.class, args);
	}

	@Override
	public void run(final String... args) {
	}
	
	public BigBrother() {
		log.info("License Limit: {}", licenseLimit); // NON-NLS
		log.info("Running on Java {}", System.getProperty("java.version")); // NON-NLS
		log.info("Keep Alive = {} ms", LicenseLimit.getKeepAliveMilliseconds());
	}
	
}
