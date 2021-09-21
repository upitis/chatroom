FROM java:8
MAINTAINER Upitis K.I.
COPY ["out/artifacts/ChatServer_jar/*.jar", "/opt/"]
EXPOSE 2222
ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/ChatServer.jar", "2222"]
