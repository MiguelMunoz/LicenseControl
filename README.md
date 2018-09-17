# LicenseControl
Code challenge to develop a Licence Control system

The purpose is to limit the number of thread, operating on different machines, that can execute a piece of code for which the software license limits the number of parallel usages of the software. We assume that each execution takes a long time.

## How it works:

1. Each execution thread notifies a central authority that it needs a license to execute the restricted method
2. The execution thread waits for the central authority to grant permission.
3. Once permission is granted, each execution thread periodically sends a "still-alive" message every few seconds.
4. When an execution thread completes, it notifies the central authority that it has finished. 
5. The cental authority watches the "still-alive" messages to know which threads have crashed and removes them from its list of active threads.

## Operation:

Two jars are provided. BigBrother.jar is the central authority. It should be launched first:

    java -jar BigBrother.jar

Alternatively, you may change the BigBrother packaging in maven to `war` and launch it in a server. By default, the license allows 5 instances to run at once, but this may be changed dynamically using the LicenceClient tool.

The second is LicenseClient.jar. This application creates test thread that request a license. Each instance may request multiple licenses, some of which are designed to throw an exception instead of returning normally. 
 Launch this to start thread requesting license authority. Syntax is as follows:

    java -jar LicenseClient.jar [-n threadCount] [-t timeToSolve] [-c crashCount] [-u licenseURL]
    java -jar LicenseClient.jar -limit new-License-Limit

Options may be entered in any order and all have default values. Options are as follows:

  * `threadCount` is the number of licenses to request, each on its on thread. Default to 1.
  * `timeToSolve` is the number of seconds to take solving the simulated problem. Defaults to 5.
  * `crashCount` is the number of additional threads to crash before solving. Defaults to 0. The total number of threads launched will be equal to `threadCount + crashCount`.
  * `licenseURL` is the optional URL of the license authority. This parameter is not necessary if the license authority is running on localhost.
  * All of the default values may also be overridden by adding a `licenseClient.properties` file. 
  
  You may launch as many tests as you want simultaneously.
  
  If you use it to change the limit, and you are lowering the limit, you should make this change before the legal limit is actually lowered. So if 3 of your 5 licenses expire on midnight of August 1, you should lower the limit on July 31. This is because all licenses that are currently in use will be allowed to continue running until they complete, even if the number running is now beyond the newly set limit. 
  
## Configuration:

Both the client and the server may be configured. Configuration is done through properties files, to be placed in the directory given by the "user.dir" System property, which is usually the directory where the application was launched.

### Client Configuration
LicenseClient looks for an optional properties file called licenseClient.properties, which contains the URL to connect to the JMS server managing the licenses. This defaults to localhost. It also lets you change the default values for threadCount, timeToSolve, and crashCount. Command line arguments take precedence over values in the properties file. 
#### Default URL
By default, the URL of the license broker is `http://localhost:61616/` but you may override this on the client by setting the `licenseURL` property.

### Server Configuration
The server looks for an optional properties file called BigBrother.properties. This contains the licenseLimit property, which specifies the number of licenses to run at any given time. If I have time, I'll place the property in a database, so it can be changed on the fly. It defaults to 5 licenses.

## How it Works

### JMS Queues and Topics

#### Authority IDTopic
Assigns an ID to any license client that requests one. Returns immediately. (Might not be necessary)

#### Authority LaunchTopic
Authorizes a license client with the specified ID to start its licensed task

#### Authority KeepAliveTopic
Allows the license authority to keep track of which waiting clients are still alive and which
have died.

#### Client IDRequestTopic
Temporary Topic allows the client to receive an id from the authority. (Might not be necessary.)

