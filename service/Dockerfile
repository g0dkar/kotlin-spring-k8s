FROM openjdk:13-alpine
MAINTAINER Rafael M. Lins <rafael.lins777@gmail.com>

# Needed by the Spring Boot Server
VOLUME /tmp

# Port we will expose
EXPOSE 8080/tcp

# Copy the fat jar into the image
COPY target/app.jar app.jar

# Entry point
ENTRYPOINT ["java", "-Dspring.profiles.active=live", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=UTC", "-server", "-jar", "/app.jar"]