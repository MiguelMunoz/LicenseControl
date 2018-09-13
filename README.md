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

Two jars are provided. BigBrother.jar is the central authority. It should be launched first.
The second is LicenseClient.jar. Launch this to start thread requesting license authority. Syntax is as follows:

    java -jar LicenseClient.jar [-n threadCount] [-t timeToSolve] [-c crashCount]

Options may be entered in any order and all have default values. Options are as follows:

  * `threadCount` is the number of licenses to request, each on its on thread. Default to 1.
  * `timeToSolve` is the number of seconds to take solving the simulated problem. Defaults to 5.
  * `crashCount` is the number of threads to crash before solving. Defaults to 0.
  
  You may launch as many tests as you want simultaneously.
  
## Configuration:

Both the client and the server may be configured. Configuration is done through properties files, to be placed in the directory given by the "user.dir" System property, which is usually the directory where the application was launched.

### Client Configuration
LicenseClient looks for an optional properties file called licenseClient.properties, which contains the URL to connect to the JMS server managing the licenses. This defaults to localhost. It also lets you change the default values for threadCount, timeToSolve, and crashCount. Command line arguments take precedence over values in the properties file.  

### Server Configuration
The server looks for an optional properties file called BigBrother.properties. This contains the licenseLimit property, which specifies the number of licenses to run at any given time. If I have time, I'll place the property in a database, so it can be changed on the fly. It defaults to 5 licenses.
