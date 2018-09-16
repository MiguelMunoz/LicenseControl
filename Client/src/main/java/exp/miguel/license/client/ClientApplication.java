package exp.miguel.license.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.JMSException;
import exp.miguel.license.client.tasks.DummyFailedTask;
import exp.miguel.license.client.tasks.DummySuccessfulTask;
import exp.miguel.license.client.tasks.LicenseTask;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/18
 * <p>Time: 4:03 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SpringBootApplication
@ComponentScan(basePackages = {"exp.miguel.license.client", "exp.miguel.license"})
@EnableJms
public class ClientApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);

	private static final String THREAD_COUNT = "threadCount";
	private static final String TIME_TO_SOLVE = "timeToSolve";
	private static final String CRASH_COUNT = "crashCount";
	private static final String LICENSE_URL = "licenseURL";
	private static int threadCount = 1;
	private static int timeToSolve = 5;
	private static int crashCount = 0;
	private static String licenseUrl = "tcp://localhost:61616/"; // NON-NLS
	private static final long ONE_WEEK = 1000L * 3600L * 24L * 7L;
	private MessageFacade messageFacade;
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(ClientApplication.class, args);
	}
	
	@Autowired
	public ClientApplication(MessageFacade messageFacade) {
		this.messageFacade = messageFacade;
	}

	@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
	@Override
	public void run(final String... args) {
		Properties properties = new Properties();
		final String userDir = System.getProperty("user.dir");
		log.info("user.dir: {}", userDir);
		File file = new File(userDir, "licenseClient.properties");
		if (file.exists()) {
			//noinspection OverlyBroadCatchBlock
			try {
				properties.load(new FileReader(file));
			} catch (FileNotFoundException ignored) { log.warn("No properties file"); } catch (IOException ioe) {
				log.error(ioe.getLocalizedMessage(), ioe);
				System.exit(-1);
			}
		}
		threadCount = getInt(properties, THREAD_COUNT, threadCount);
		timeToSolve = getInt(properties, TIME_TO_SOLVE, timeToSolve);
		crashCount = getInt(properties, CRASH_COUNT, crashCount);
		licenseUrl = properties.getProperty(LICENSE_URL, licenseUrl);
		log.warn("threadCount: {}", threadCount);
		log.warn("timeToSolve: {}", timeToSolve);
		log.warn("crashCount:  {}", crashCount);
		log.warn("Running on Java {}", System.getProperty("java.version"));

		// strip off trailing slash:
		while (licenseUrl.endsWith("/")) {
			licenseUrl = licenseUrl.substring(0, licenseUrl.length() - 1);
		}

		processArgs(args);

		new Thread(() -> launchThreads(threadCount, timeToSolve, crashCount) ).start();
	}

	private static int getInt(Properties properties, String key, int defaultValue) {
		try {
			String text = properties.getProperty(key, String.valueOf(defaultValue));
			return Integer.valueOf(text);
		} catch (NumberFormatException e) {
			// Show name of bad property
			log.error("Unable to read integer property \"{}\". {}", key, e.getLocalizedMessage());
			throw e;
		}
	}

	@SuppressWarnings({"EqualsReplaceableByObjectsCall", "HardCodedStringLiteral"})
	private static void processArgs(String[] args) {
		List<String> argList = Arrays.asList(args);
		Iterator<String> itr = argList.iterator();
		try {
			while (itr.hasNext()) {
				String arg = itr.next();
				if ("-n".equals(arg)) {
					threadCount = Integer.valueOf(itr.next());
				} else if ("-t".equals(arg)) {
					timeToSolve = Integer.valueOf(itr.next());
				} else if ("-c".equals(arg)) {
					crashCount = Integer.valueOf(itr.next());
				} else if ("-u".equals(arg)) {
					licenseUrl = itr.next();
				}
			}
		} catch (NumberFormatException | NoSuchElementException e) {
			log.error(e.getLocalizedMessage(), e);
			//noinspection UseOfSystemOutOrSystemErr
			System.err.printf("Usage: java -jar <jar-file> [-n %s] [-t %s] [-c %s] [-u %s]%n", THREAD_COUNT, TIME_TO_SOLVE, CRASH_COUNT, LICENSE_URL);
			System.exit(-1);
		}
	}

	private void launchThreads(int count, int timeSeconds, int crashes) {
		// tried 1099, 2049, 4380
		
		// found ports: Connected at port 1099
		//Connected at port 2049
		//Connected at port 4380
		//Connected at port 6942
		//Connected at port 10638
		//Connected at port 18170
		//Connected at port 34000
		//Connected at port 50188
		//Connected at port 50198
		//Connected at port 53575
		//Connected at port 60556
		//Connected at port 60557
		//Connected at port 60572
		//Connected at port 60573
		//Connected at port 63342
		//Connected at port 63830
		//Connected at port 63831
		//Connected at port 63838
		//Connected at port 63839.

		long id = System.currentTimeMillis();
//		{
//			boolean connected = false;
//			int foundPort = 0;
//			for (int port = 1024; port < 65536; ++port) {
//				try {
//					String urlText = String.format("tcp://localhost:%d", port);
////					URL url = new URL("tcp:", "localhost", port, "");
////					URL url = new URL(urlText);
////					java.net.URLConnection connection = url.openConnection();
//					ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", urlText);
//					factory.createConnection();
//					connected = true;
//					foundPort = port;
//					System.out.printf("Connected at port %d%n", port);
////					break;
////				} catch (MalformedURLException e) {
////					throw new IllegalStateException(String.format("Port: %d", port), e);
////				} catch (IOException e) { }
//				} catch (JMSException e) {
////					e.printStackTrace();
//				}
//			}
//			log.info("Connected: {} at port {}", connected, foundPort);
//
//		}

		List<LicenseTask> futureList = new LinkedList<>();
		while ((count > 0) || (crashes > 0)) {
			if (count > 0) {
				futureList.add(new DummySuccessfulTask(id, timeSeconds));
				id += ONE_WEEK;
				count--;
			}
			if (crashes > 0) {
				futureList.add(new DummyFailedTask(id, timeSeconds));
				id += ONE_WEEK;
				crashes--;
			}
		}

		for (LicenseTask task : futureList) {
			String reply = messageFacade.requestLicense(task.getId());
			log.info("requestLicense() reply: {}", reply);
//			sendTopicMessage("");
		}
	}

}
