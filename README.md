# LicenseControl
Code challenge to develop a Licence Control system

The purpose is to limit the number of thread, operating on different machines, that can execute a piece of code for which the software license limits the number of parallel usages of the software. We assume that each execution takes a long time.

##Building

1. Right now, it in IntelliJ, but not from the command line. I don't know why. There's a problem in the maven file that doesn't show up in IntelliJ. It may build inside other IDEs, but I'm just guessing. My efforts to fix the build problem have failed. But in IntelliJ, I can build and run it, and it runs fine.
2. You need to skip test in building. I'm not sure why, because I don't have any real tests, but that's what you have to do.

## Running
Two jars are provided. BigBrother.jar is the central authority. It should be launched first:

    cd exp.miguel.BigBrother
    mvn spring-boot:run

Alternatively, you may change the BigBrother packaging in maven to `war` and launch it in a server. By default, the license allows 5 instances to run at once, but this may be changed dynamically using the LicenceClient tool.

The second is LicenseClient.jar. This application creates test thread that request a license. Each instance may request multiple licenses, some of which are designed to throw an exception instead of returning normally. 
 Launch this to start thread requesting license authority. Options may be specified on a command line or in a properties file. The properties file is `licenseClient.properties`. Default values in the properties file are:
                                                                                                                           
    threadCount=30
    crashCount=10
    timeToSolve=10

You may override these values on the command line. Command line arguments are as follows: 

    [-n <threadCount>] [-t <timeToSolve>] [-c <crashCount>]
    -limit <licenceLimit>
 
 To override the properties from the command line, syntax is as follows:

    mvn spring-boot:run -Dspring-boot.run.arguments=--n,-30,--c,-10,--t,-10
    
This corresponds to these command line arguments: (Each command line argument is separated by a comma and preceded by a dash.)

    -n 30 -c 10 -t 10

Or you may use this just to change the default value (5) of the licence limit number:

    mvn spring-boot:run -Dspring-boot.run.arguments=--limit,-7

This corresponds to these command line arguments:

    -limit 7    
    
Options may be entered in any order and all have default values that take effect if it doesn't find the properties file. Options are as follows:

  * `threadCount` is the number of licenses to request, each on its on thread. Default to 1, or 10 from the properties file.
  * `timeToSolve` is the number of seconds to take solving the simulated problem. Defaults to 5, or 10 from the properties file.
  * `crashCount` is the number of additional threads to crash before solving. Defaults to 0, or 10 from the properties file. The total number of threads launched will be equal to `threadCount + crashCount`.
  * `licenseLimit` is number of licenses to authorize at any point in time. This may be changed on the fly.
  * All of the default values may also be overridden by adding a `licenseClient.properties` file. 
  
**Example Options** `-n 30 -t 12 -c 10`

This launches 40 threads, of which 30 will complete normally and 10 will crash (throw an exception). Each thread will take 12 seconds to finish. At first, the normal threads and the crashing threads will be intermixed until one group runs out of threads. So this command

`-n 8, -t 12, -c 3` will produce threads which will be launched in this sequence:

    Normal
    Crash
    Normal
    Crash
    Normal
    Crash
    Normal
    Normal
    Normal
    Normal
    Normal


  You may launch as many tests as you want simultaneously.
  
  You may also change the limit while other tests are running. So after watching the bigBrother output, you'll be able to raise the limit and watch the number of active thread start to increase until it reaches the new limit. 

  If change the limit, and you are lowering the limit, you should make this change before the legal limit is actually lowered. So if 3 of your 5 licenses expire on midnight of August 1, you should lower the limit on July 31. This is because all licenses that are currently in use will be allowed to continue running until they complete, even if the number running is now beyond the newly set limit. You can see this in action by lowering the limit from another test.
  

## How it works:
### Client side:

1. Each execution thread notifies a central authority that it needs a license to execute the restricted method. This method immediately returns an instruction to run or wait.
2. If waiting, the execution thread periodically requests permission again until it succeeds.
3. Once permission is granted, each execution thread periodically sends a "still-alive" message every few seconds.
4. When an execution thread completes, it notifies the central authority that it has finished. 
5. The central authority watches the "still-alive" messages to know which threads have crashed and removes them from its list of active threads.
### Server side
1. The server maintains two hash maps, each of which maps an id to a time. The hash maps are collections of waiting IDs and running IDs. The the id was alive at the time in the hash map entry's value.
2. When a request comes in, The server generates id to return to the client, unless the client included an id. 
3. When a new ID is submitted, it only gets added to the list of running IDs if there's room for it and for all the waiting IDs. Otherwise it gets added to the waiting list. Whenever a request comes in that includes an id, it looks to see if that id is in an "open slot." For example, if the license limit is 5, but only three are running, there are two "open slots" at the start of the wait list. If the request is one of those two, it gets removed from the wait list and added to the run list, and the request returns permission to launch the licenced code.
4. When a keep-alive task comes in, it will have an id. If this id is not in the runners list (because the task has completed), it gets re-added with a revised time, to keep it current.
5. A side thread, called the grave digger, periodically scans both lists for dead items, which are items with a time that't too old, which means less the 3 times the keep-alive time.

### Server Configuration
The server looks for an optional properties file called BigBrother.properties. This contains the licenseLimit property, which specifies the number of licenses to run at any given time. If I have time, I'll place the property in a database, but it can be changed on the fly as is, using the client tool. It defaults to 5 licenses.

## Caveats
1) OptimizerSolver.toTask merely prints an exception of a task fails, when it should probably log it or throw it. I didn't do that because the output window was too cluttered. But it runs in its own thread so it would do no good to simply propogate the exception.

2) In the generated class io.swagger.client.ApiClient, I had to change the private `basePath` String from this:   

       private String basePath = "https://virtserver.swaggerhub.com/SwingGuy1024/license/1.0.0";
to this:

         private String basePath = "http://localhost:8080";
  I did this because the URL was wrong, and I don't know what the correct URL was. The original URL will access Swagger's documentation instead of the underlying REST APIs. Now that I use `localhost`, I don't know if you will be able to connect to it from another machine. You may need to replace it with an ip address or find some other approach. I wasn't able to test this from home.