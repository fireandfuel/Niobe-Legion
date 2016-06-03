Niobe Legion Framework
======================

A versatile server/client framework written in Java Programming Language.

[![Build Status](https://travis-ci.org/fireandfuel/Niobe-Legion.svg?branch=master)](https://travis-ci.org/fireandfuel/Niobe-Legion)

Key features:
-------------
- Server Client Framework with communication via XML over network (inspired by XMPP), uses StAX API
- Uses TLS1.2 for transparent communication encryption
- Uses SASL (implementation like SCRAM-SHA1) for user authentication
- Uses Hibernate for entity management and persistence

TODO and in development:
------------------------
- JavaFx 8 Client (look and feel, some functions missing)
- Simple user / group management
- Simple hierarchical user group rights
- Expandable with plugins
- Communication protocol specification and documentation
- Plugin API implementation and documentation
- General documentation (javadoc)

Requirements:
-------------
Build:

- Oracle JDK 1.8 Update 40 or later
- Gradle 2.X or later
- [MetroProgressIndicator 1.0](https://github.com/fireandfuel/MetroProgressIndicator)
- (optional) your favourite Java IDE

Deploy it: Use usual gradle way.

Run:

- Oracle JRE 1.8 Update 40 or later (not tested on OpenJRE)

How to run it:
--------------
Server: set up your server.ini, start it with: java -jar niobe-legion-server-[Version].jar

Client: set up your client.ini, start it with: java -jar niobe-legion-client-[Version].jar
(or simply double click on the file)

License: LGPL v3.0

