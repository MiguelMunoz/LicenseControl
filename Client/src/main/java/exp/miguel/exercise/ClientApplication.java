package exp.miguel.exercise;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import exp.miguel.license.client.LicenseException;
import exp.miguel.license.client.MessageFacade;
import exp.miguel.license.client.LicenseTask;
import exp.miguel.license.client.OptimizerSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.jms.annotation.EnableJms;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/18
 * <p>Time: 4:03 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SpringBootApplication
@ComponentScan(basePackages = {"exp.miguel.license.client", "exp.miguel.license"})
//@EnableJms
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
		StringBuilder argString = new StringBuilder();
		for (String s: args) {
			//noinspection MagicCharacter
			argString.append(s).append(' ');
		}
		log.info("Command line arguments: {}", argString);
		System.err.printf("Command line arguments: %s%n", argString); // NON-NLS
		// First check if we're lowering the limit.
		
		if ((args.length == 2) && "-limit".equals(args[0])) {
			int newLimit = Integer.valueOf(args[1]);
			try {
				OptimizerSolver.setNewLimit(newLimit);
			} catch (LicenseException e) {
				//noinspection UseOfSystemOutOrSystemErr
				System.err.printf("Error setting a new limit: %s%n", e.getLocalizedMessage()); // NON-NLS
				e.printStackTrace();
			}
		}
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

		List<LicenseTask> futureList = new LinkedList<>();
		while ((count > 0) || (crashes > 0)) {
			if (count > 0) {
				futureList.add(new DummySuccessfulTask(timeSeconds));
				count--;
			}
			if (crashes > 0) {
				futureList.add(new DummyFailedTask(timeSeconds));
				crashes--;
			}
		}

		int threadNumber = 0;
		for (LicenseTask task : futureList) {
			// 2 Notes
			// 1. The spec didn't specify what to return if the solver throws an exception, except to preserve the integrity 
			//    of the license terms. So my solve() method will throw an Exception if something goes wrong. This may be
			//    easily changed.
			// 2. The method takes a parameter. This isn't necessary for an actual solver, but I needed it to test it with 
			//    two different possible tasks, one of which throws an exception and the other of which finishes normally.
			//    This also has the advantage that it's more easily adaptable to other licensed tasks.
			new Thread(() -> doTask(task), String.format("Solver Thread %d", threadNumber++)).start(); //NON-NLS
			try {
				Thread.sleep(950);
			} catch (InterruptedException ignored) {}
		}
	}

	private void doTask(LicenseTask task) {
		try {
			OptimizerSolver solver = new OptimizerSolver(task);
			String solution = solver.solve();
			log.info(solution);
//			System.err.printf("Solution: %s%n", solution); // NON-NLS
		} catch (LicenseException e) {
			e.printStackTrace();
		}
	}
}
