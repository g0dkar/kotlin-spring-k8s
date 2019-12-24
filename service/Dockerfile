FROM adoptopenjdk:11-jre-hotspot

MAINTAINER Rafael M. Lins <rafael.lins777@gmail.com>

LABEL service=kotlin-spring

# Updates the image
COPY docker-data/install_updates /install_updates
RUN /install_updates

# Custom java.security (no DNS cache, non-blocking and safe SecureRandom)
# Reference: https://tersesystems.com/blog/2015/12/17/the-right-way-to-use-securerandom/
COPY docker-data/java.security ${JAVA_HOME}/conf/security/java.security

# Port we will expose
EXPOSE 8080/tcp

# Copy the fat jar into the image
COPY target/app.jar /app.jar

# Copy the launcher script
COPY docker-data/launch_app /launch_app

# Entry point
ENTRYPOINT ["/launch_app", "/app.jar"]
