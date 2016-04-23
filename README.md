#Jannel - Java Kannel library#

The jannel project implements a client for the bearer-box server using the protocol specified by the Kannel
project. It is implemented using the Netty IO framework for robustness and performance. The design is based on 
the well-known cloudhopper-smpp library.

The project is still in beta stages and the API is subject to change.
Will provide releases on the central maven repository and try also to provide snapshots using travis.

[![Build Status](https://travis-ci.org/spapageo/jannel.svg?branch=master)](https://travis-ci.org/spapageo/jannel) [![Coverage Status](https://coveralls.io/repos/github/spapageo/jannel/badge.svg?branch=master)](https://coveralls.io/github/spapageo/jannel?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.spapageo/jannel/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.spapageo/jannel)


##Usage##

Include the dependency of your pom file:

```xml
<dependency>
    <groupId>com.github.spapageo</groupId>
    <artifactId>jannel</artifactId>
    <version>0.1.BETA</version>
</dependency>
```

Using the library works as follows:

```java
ClientSessionConfiguration config = new ClientSessionConfiguration("awesome_box");
config.setHost("localhost");
config.setPort(12000)

JannelClient jannelClient = new JannelClient(2);

ClientSession session = jannelClient.identify(config, mySessionHandler);

Sms sms = new Sms();
sms.setSender("me");
sms.setReceiver("you");
sms.setType(SmsType.MOBILE_TERMINATED_PUSH);
sms.setMsgData("Hello there");

WindowsFuture<UUID, Sms, Ack> future = session.sendSms(sms, 5000, false);

future.await();

Ack response = future.getResponse();
```
