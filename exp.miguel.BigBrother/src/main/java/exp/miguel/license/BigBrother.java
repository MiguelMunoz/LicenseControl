package exp.miguel.license;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/18
 * <p>Time: 2:54 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SpringBootApplication
@ComponentScan(basePackages = {"exp.miguel.license.broker", "exp.miguel.license"})
public class BigBrother implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(BigBrother.class);

	public static final String LAUNCH_TOPIC = "LaunchTopic";
	public static final String LICENSE_LIMIT = "licenseLimit";
	public static final String LICENSE_AUTHORITY = "LicenseAuthority";
	private static final long TIME_OUT = 60000L;
	private int licenseLimit;

	public static void main(String[] args) {
//		ConfigurableApplicationContext context = 
		SpringApplication.run(BigBrother.class, args);
	}

	@Override
	public void run(final String... args) { }
	
	public BigBrother() {
		Properties properties = new Properties();
		File file = new File(System.getProperty("user.dir"), "BigBrother.properties");
		if (file.exists()) {
			//noinspection OverlyBroadCatchBlock
			try {
				properties.load(new FileReader(file));
			} catch (IOException ignored) { }
		}
		String licenseLimitText = properties.getProperty(LICENSE_LIMIT, "5");
		licenseLimit = Integer.valueOf(licenseLimitText);
		log.warn("License Limit: {}", licenseLimit);
		log.warn("Running on Java {}", System.getProperty("java.version"));

	}

}
