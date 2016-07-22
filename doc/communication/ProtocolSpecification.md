Niobe Legion Protocol Specification 1.0 Early Draft 1
===============================================

Abstract
--------
The Niobe Legion Protocol [NLP] is an application protocol based on the Extensible Markup Language
[[XML]](https://www.w3.org/TR/xml/), which is described in this document.

The NLP is influenced by the Extensible Messaging and Presence Protocol [[XMPP]](https://tools.ietf.org/html/rfc6120).

Copyright Notice
----------------
Copyright (c) 2016 by fireandfuel (fireandfuel[at]hotmail[dot]de)

This document is released as part of the documentation of the Niobe Legion Framework: You can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

Status of this document
-----------------------
This document is an early draft based on the Niobe Legion Framework reference implementation.

Goal
----
The NLP is designed for a simple expendable low latency and platform independent network communication by the
Niobe Legion server client framework. Its core features are user authentication and bidirectional application data
transmission.

Function summary
----------------
This sections gives a brief overview how the NLP works. Please prefer the following sections for a more
detailed description of the NLP.

The data in the NLP is transmitted as small chunks, called XML stanzas. Each XML stanza have a XML namespace and XML
name, optionally followed by XML attributes. Some XML stanza can have a child XML stanza or text node.

The process of connection over the NLP is:

1. Open a Transmission Control Protocol [[TCP]](https://tools.ietf.org/html/rfc793) connection to a port over the
Internet Protocol Version 4 [[IPv4]](https://tools.ietf.org/html/rfc791) or Version 6
[[IPv6]](https://tools.ietf.org/html/rfc2460) address to a server, mostly resolved using a fully qualified domain name
[FQDN].
2. Open a XML stream over TCP connection
3. Exchange implementation information (like client/server identification, features and authentication mechanisms)
4. (optional) Negotiate a Transport Layer Security [[TLS1.2]](https://tools.ietf.org/html/rfc5246) for stream encryption
5. (optional) Establish a [[GZIP]](https://tools.ietf.org/html/rfc1952) data compression for the stream
6. Authenticate over Simple Authentication and Security Layer [[SASL]](https://tools.ietf.org/html/rfc2222) mechanism
7. Exchange some XML stanzas
8. (optional) De-Authenticate
9. Close XML stream
10. Close TCP connection

Terminology
-----------
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and
"OPTIONAL" in this document are to be interpreted as described in [[RFC 2119]](https://tools.ietf.org/html/rfc2119).

This document uses the following abbreviation for better readability:

C: = a client

S: = a server

XML streams
-----------
XML streams are unidirectional.

XML namespaces
--------------
It is required to use XML namespaces for the XML streams. Namespaces can be declared at the initial XML stream stanza
or at the first usage of the namespace's element. The default namespace is "legion".

Server socket initialization
----------------------------
The server must create a server socket for accepting client connections over TCP with a particular port.
It is recommended to use a different port for the XML stream communication to accept multiple client connections.
The server may limit the amount of connections to prevent (distributed) denial of service attacks.

Connection initialization
-----------------
The client connects to the server socket over TCP. It sends the XML declaration and initial XML stream stanza over the
TCP connection.
C: <?xml version="1.0" encoding="UTF-8"?>
C: <legion:stream xmlns:legion="https://github.com/fireandfuel/Niobe-Legion">

The server must answer the XML stream stanza with:
S: <?xml version="1.0" encoding="UTF-8"?>
S: <legion:stream xmlns:legion="https://github.com/fireandfuel/Niobe-Legion">

When the client received the server's answer to the client's initial XML stream stanza, the XML stream connection is
established.

Exchange implementation information
-----------------------------------
After the XML stream connection is established, the client should send its client's identification, available features
and authentication mechanisms. For example a client that identifies itself as "legion_client" Version "1.0" with the
feature "starttls" and authentication mechanism "SCRAM-KECCAK-512".
```
C: <legion:client name="legion_client" version="1.0">
C: <legion:features>
C: <legion:feature>starttls</legion_feature>
C: </legion:features>
C: <legion:mechanisms>
C: <legion:mechanism>SCRAM-KECCAK-512</legion:mechanism>
C: </legion:mechanisms>
C: </legion:client>
```

If the server accepts the client it sends an accept XML stanza, followed by its own identification, available features
and authentication mechanisms.
```
S: <legion:accept>
S: <legion:client name="legion_client" version="1.0"/>
S: </legion:accept>
S: <legion:server name="legion_server" version="1.0">
S: <legion:features>
S: <legion:feature>starttls</legion_feature>
S: </legion:features>
S: <legion:mechanisms>
S: <legion:mechanism>SCRAM-KECCAK-512</legion:mechanism>
S: </legion:mechanisms>
S: </legion:server>
```

If the server rejects the client implementation (e.g. caused by missing client's identification, blacklisted
implementation/version, no supported authentication), it sends a reject XML stanza. The reject XML stanza may contains
a human readable reason.
```
S: <legion:decline type="legion:client">Client is blacklisted on server</legion:decline>
```

When the client receives the accept XML stanza from the server and accepts the server's implementation information, 
it will send a accept XML back. The implementation information exchange is successful and the client can proceed with
TLS negotiation, stream compression or user authentication.
```
C: <legion:accept>
C: <legion:client name="legion_server" version="1.0"/>
C: </legion:accept>
```

If the client rejects the server implementation (e.g. caused by missing server's identification, blacklisted
implementation/version, no supported authentication), it sends a reject XML stanza. The reject XML stanza may contains
a human readable reason.
```
C: <legion:decline type="legion:server">Server is blacklisted on client</legion:decline>
```

TLS 1.2 negotiation (optional)
--------------------------
The NLP is designed to support encryption over Transport Layer Security [[TLS1.2]](https://tools.ietf.org/html/rfc5246).
It is required for a TLS1.2 connection that the server and client supports it and both propagate the feature "starttls" 
in the implementation information. Also required is that the server have a valid [[X.509]]() certificate. The client
implementation should show the certificate information when the user requests for it and asks the user to accept the 
certificate if it is unknown to the client.

The client initiates the TLS 1.2 negotiation when the implementation information exchange is successful and the
mentioned requirements are fulfilled.
```
C: <legion:starttls/>
```

The server answers when the TLS 1.2 connection can be established with
```
S: <legion:proceedtls/>
```
After the server sends the message it switches the socket to accept incoming TLS 1.2 connections.
Consequently the client switches starts a TLS 1.2 handshake when it received the server's proceedtls XML stanza.

// TODO