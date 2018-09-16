# BigBrother

This Spring Boot application is supposed to launch an embedded JMS broker, which I have been unable to connect to. 

## Bug Info:
I do not specify a `spring.activemq.broker-url` property in the `application.properties` file, because the Spring Boot JMS documentation says that this property suppresses the embedded broker. Experiments have shown that this is what happens, because when I launch with this property, I get error messages telling me that it was unable to connect to a certain topic because it failed to connect to the JMS broker. Without this property I get no such message, and I get a message telling me that it launched a broker at vm://localhost. 

Launching without the property set puts logs these messages to the console:


    o.apache.activemq.broker.BrokerService   : Using Persistence Adapter: MemoryPersistenceAdapter                                                 
    o.a.a.broker.jmx.ManagementContext       : JMX consoles can connect to service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi                     
    o.apache.activemq.broker.BrokerService   : Apache ActiveMQ 5.15.6 (localhost, ID:Slytherin-52287-1537050481591-0:1) is starting                
    o.apache.activemq.broker.BrokerService   : Apache ActiveMQ 5.15.6 (localhost, ID:Slytherin-52287-1537050481591-0:1) started                    
    o.apache.activemq.broker.BrokerService   : For help or more information please see: http://activemq.apache.org                                 
    o.a.activemq.broker.TransportConnector   : Connector vm://localhost started                                                                    
    exp.miguel.license.BigBrother            : Started BigBrother in 10.439 seconds (JVM running for 28.76)

It sure looks like the broker got launched, but my client fails to connect to it. Typing `netstat -a` doesn't reveal anything running at port 61616, which is where I would expect it to be. (The Spring Boot documentation says nothing about the default URL.)

When I run my client, I get this message:
 
 `javax.jms.JMSException: Could not connect to broker URL: tcp://localhost:61616. Reason: java.net.ConnectException: Connection refused (Connection refused)`
 
 2018-09-15 15:27:59.747  WARN 50853 --- [           main] exp.miguel.license.BigBrother            : License Limit: 5
 2018-09-15 15:27:59.748  WARN 50853 --- [           main] exp.miguel.license.BigBrother            : Running on Java 10
 2018-09-15 15:28:00.866  INFO 50853 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
 2018-09-15 15:28:01.154  INFO 50853 --- [           main] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 2147483647
 2018-09-15 15:28:01.524  INFO 50853 --- [           main] o.apache.activemq.broker.BrokerService   : Using Persistence Adapter: MemoryPersistenceAdapter
 2018-09-15 15:28:01.692  INFO 50853 --- [  JMX connector] o.a.a.broker.jmx.ManagementContext       : JMX consoles can connect to service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
 2018-09-15 15:28:01.782  INFO 50853 --- [           main] o.apache.activemq.broker.BrokerService   : Apache ActiveMQ 5.15.6 (localhost, ID:Slytherin-52287-1537050481591-0:1) is starting
 2018-09-15 15:28:01.791  INFO 50853 --- [           main] o.apache.activemq.broker.BrokerService   : Apache ActiveMQ 5.15.6 (localhost, ID:Slytherin-52287-1537050481591-0:1) started
 2018-09-15 15:28:01.791  INFO 50853 --- [           main] o.apache.activemq.broker.BrokerService   : For help or more information please see: http://activemq.apache.org
 2018-09-15 15:28:01.876  INFO 50853 --- [           main] o.a.activemq.broker.TransportConnector   : Connector vm://localhost started
 2018-09-15 15:28:01.994  INFO 50853 --- [           main] exp.miguel.license.BigBrother            : Started BigBrother in 10.439 seconds (JVM running for 28.76)
