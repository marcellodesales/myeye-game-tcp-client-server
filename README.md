# Myeye Game Server

A simple game server using TCP protocols.

* Implementation by Marcello de Sales (marcello.desales@gmail.com).

# Building 

I have developed the server-side considering the requirements. I have used Gradle as the build system (http://www.gradle.org) if you need to rebuild the jar or run the JUnit test cases from the console. However, the jars must be provided in this zip under "build/libs" in this revision.

"gradle clean" - cleans the binary (before running test cases)
"gradle jar" - generates jar
"gradle test" - run the test cases
"gradle dependencies" - verifies dependencies
"gradle runServer" - runs the server
"gradle eclise" - generates the Eclipse artifacts (.project and .classpath).
...

Copy the dependency to Google Guava 
cp /home/marcello/.gradle/caches/artifacts-4/com.google.guava/guava/c12498cf18507aa6433a94eb7d3e77d5/jars/guava-10.0.1.jar build/libs/

# Design

Given that, an immutable POJO was designed to hold the data of messages. Since the data types can be mapped directly to the Java primitive types, it was simple to produce the "marshaling" of a given instance with the types using the Java NIO API.

- Version (1 byte) -> Java Byte (8 bit integer)
- Message Type (2 byte integer) -> Java Short (16 bit integer)
- User ID (4 byte integer) -> Java Integer (32 bit integer)
- Payload (variable length ASCII string) (unbound-length String)

The POJO was named GameMessage and is composed by those properties. In order to simplify data input, I have wrapped Message Type and Version into 2 other Enum types: MessageType and MessageVersion. Although the semantics might be incorrect, I considered Message Type the type a messages during the game (INITIALIZE_GAME, PAUSE_GAME...) that carries a default payload, whereas a customized message (ACTION) that could be used for carrying custom payload ASCII messages. However, protocols can be developed with those as well as usual.

The class GameMessageService is a class that provides the Factory methods for new instances of the related class, as well as the methods to marshal and unmarshal messages. This class is Unit-tested to assure that the marshal/unmarshal will work propertly.

The Server-side was implemented using a multi-threaded approach with a Thread-pool of size 50. This can handle a good number of requests of messages to be logged. Each client request is handled by an instance of the class GameMessageRequestHandler, which is responsible for receiving the bytes of binary message transmitted by the client, unmarshal the message instance and send the received message to the GameMessageLogger. All client request handlers will concurrently add new GameMessage instances to the Blocking Queue of this single thread.

There would be a lot of changes to handle specific corner cases, specifically related to the size of the payload (as I don't have knowledge of that, I'm using 4K).

The Client-side was implemented to exercise the server-side. A state-machine was created to add some knowledge about what messages can be transmitted to the server (again, this is my own assumption :D). The same GameMessageService is used here to marshal the message instance. Other adaptations of the client would depend on specific requirements.

# Running the Client

To Run the solution
- First unzip the contents of the zip file to "DIR".

*************************************** Running the server:
1. cd to "DIR"
2. Start the server

marcello@oahu:/development/workspaces/open-source/myeye$ ./startServer 
MYEYE Game Server running, waiting connections on 192.168.190.138:2020
Developed by Marcello de Sales (marcello.desales@gmail.com)

*************************************** Running The client
1. cd to "DIR"
2. Start the client

marcello@oahu:/development/workspaces/open-source/myeye$ ./startClient 
MYEYE Game Client running, will connect to 192.168.190.138:2020
Developed by Marcello de Sales (marcello.desales@gmail.com)

##### Game Session Information ####
Your User ID is '1401523655'

### Game versions ###
ONE(1)
TWO(2)
-> What version would you like to play?

The values displayed on the options are needed to be entered by the user. Note that there's NO validation on those!!! :) Here's a Client session...

### Game versions ###
ONE(1)
TWO(2)
-> What version would you like to play? 1

##### Sending Message #####
Sending GameMessage{Message Version=ONE(1), Message Type=INITIALIZE_GAME(8), UserID=1401523655, Payload=INITIALIZE_GAME at Thu Dec 01 18:46:23 PST 2011}
BINARY: [1, 0, 8, 83, -119, -115, -57, 73, 78, 73, 84, 73, 65, 76, 73, 90, 69, 95, 71, 65, 77, 69, 32, 97, 116, 32, 84, 104, 117, 32, 68, 101, 99, 32, 48, 49, 32, 49, 56, 58, 52, 54, 58, 50, 51, 32, 80, 83, 84, 32, 50, 48, 49, 49]
Sent!


##### Game Options ####
ACTION(16)
PAUSE_GAME(64)
TERMINATE_GAME(128)
-> What do you want to do? 128

##### Sending Message #####
Sending GameMessage{Message Version=ONE(1), Message Type=TERMINATE_GAME(128), UserID=1401523655, Payload=TERMINATE_GAME at Thu Dec 01 18:46:39 PST 2011}
BINARY: [1, 0, -128, 83, -119, -115, -57, 84, 69, 82, 77, 73, 78, 65, 84, 69, 95, 71, 65, 77, 69, 32, 97, 116, 32, 84, 104, 117, 32, 68, 101, 99, 32, 48, 49, 32, 49, 56, 58, 52, 54, 58, 51, 57, 32, 80, 83, 84, 32, 50, 48, 49, 49]
Sent!


Game terminated! Good bye!

# Running the Server

===================== SERVER SIDE ============================================

marcello@oahu:/development/workspaces/open-source/myeye$ ./startServer 
MYEYE Game Server running, waiting connections on 192.168.190.138:2020
Developed by Marcello de Sales (marcello.desales@gmail.com)

Thread Pool [ 50 , 50 ]
The Largest Pool size: 2
# of active threads: 1
# of maximum pool size:50
Handling client request Client [/192.168.190.138:59028]
Logging message from Logger Thread
Logged request of Client [/192.168.190.138:59028] in 55 ms
GameMessage{Message Version=TWO(2), Message Type=INITIALIZE_GAME(8), UserID=1533448286, Payload=INITIALIZE_GAME at Thu Dec 01 18:41:04 PST 2011}

Thread Pool [ 50 , 50 ]
The Largest Pool size: 3
# of active threads: 1
# of maximum pool size:50
Handling client request Client [/192.168.190.138:59029]
Logging message from Logger Thread
Logged request of Client [/192.168.190.138:59029] in 44 ms
GameMessage{Message Version=TWO(2), Message Type=ACTION(16), UserID=1533448286, Payload=Super crazy}

Thread Pool [ 50 , 50 ]
The Largest Pool size: 4
# of active threads: 1
# of maximum pool size:50
Handling client request Client [/192.168.190.138:59030]
Logging message from Logger Thread
Logged request of Client [/192.168.190.138:59030] in 32 ms
GameMessage{Message Version=TWO(2), Message Type=PAUSE_GAME(64), UserID=1533448286, Payload=PAUSE_GAME at Thu Dec 01 18:41:23 PST 2011}

Thread Pool [ 50 , 50 ]
The Largest Pool size: 5
# of active threads: 1
# of maximum pool size:50
Handling client request Client [/192.168.190.138:59031]
Logging message from Logger Thread
Logged request of Client [/192.168.190.138:59031] in 50 ms
GameMessage{Message Version=TWO(2), Message Type=TERMINATE_GAME(128), UserID=1533448286, Payload=TERMINATE_GAME at Thu Dec 01 18:41:28 PST 2011}

^Cmarcello@oahu:/development/workspaces/open-source/myeye$ ^C
marcello@oahu:/development/workspaces/open-source/myeye$ ./startServer 
MYEYE Game Server running, waiting connections on 192.168.190.138:2020
Developed by Marcello de Sales (marcello.desales@gmail.com)

Thread Pool [ 50 , 50 ]
The Largest Pool size: 2
# of active threads: 1
# of maximum pool size:50
Handling client request Client [/192.168.190.138:59072]
Logging message from Logger Thread
Logged request of Client [/192.168.190.138:59072] in 66 ms
GameMessage{Message Version=ONE(1), Message Type=INITIALIZE_GAME(8), UserID=1401523655, Payload=INITIALIZE_GAME at Thu Dec 01 18:46:23 PST 2011}

Thread Pool [ 50 , 50 ]
The Largest Pool size: 3
# of active threads: 1
# of maximum pool size:50
Handling client request Client [/192.168.190.138:59073]
Logging message from Logger Thread
Logged request of Client [/192.168.190.138:59073] in 54 ms
GameMessage{Message Version=ONE(1), Message Type=TERMINATE_GAME(128), UserID=1401523655, Payload=TERMINATE_GAME at Thu Dec 01 18:46:39 PST 2011}
