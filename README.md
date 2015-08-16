Niobe Legion Framework
======================

A versalite server/client framework written in Java Programming Language.

Key features:
-------------
- Communication via XML over network (inspired by XMPP/Jabber), uses StAX API
- Uses SSLv3 for transparent communication encryption
- Simple user / group management
- Uses SASL (implementation like SCRAM-SHA1) for user authentification
- Uses Hibernate for entity management and persistence
- JavaFx 8 Client (currently in development)
- Simple hierachical group rights (currently in development)
- Extendable with plugins (planned, partly implemented)

TODO:
-----
- Communication protocol specification and documentation
- Plugin API implementation and documentation
- General documentation (javadoc)

Planned:
-------
- Server administration via Client

Requirements:
-------------
Build:

- Oracle JDK 1.8 Update 40 or later
- Maven 3.X or later
- [MetroProgressIndicator 1.0](https://github.com/fireandfuel/MetroProgressIndicator)
- (optional) your favourite Java IDE

Deploy it: Use usual maven way.

Run:

- Oracle JRE 1.8 Update 40 or later (not tested on OpenJRE)

How to run it:
--------------
Server: set up your server.ini, start it with: java -jar niobe-legion-server-[Version].jar

Client: set up your client.ini, start it with: java -jar niobe-legion-client-[Version].jar
(or simply double click on the file)

License: LGPL v3.0
