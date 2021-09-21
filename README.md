# Chat in localnet

This is java chat application. 
The functionality is primitive simple: everybody connected to the only chat-room for conversation. 
Text messages are supported.

It`s built based on client-server architecture. 

## Server

For starting:

	>  java -jar .\out\artifacts\ChatServer_jar\ChatServer.jar 2222

where 2222 - any avalible port number

For starting in **Docker**:

	> docker build -t upitis/chatsrv .

you can edit Dockerfile for port changing, 2222 by default
	
	> docker run --rm -it -P upitis/chatsr

## Client

For starting:

	> java -jar .\out\artifacts\Chat_jar\ChatClient.jar