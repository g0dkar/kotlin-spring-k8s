FROM adoptopenjdk:11-jre-hotspot
MAINTAINER Rafael M. Lins <rafael.lins777@gmail.com>

# Updates the image
COPY docker-data/install_updates /install_updates
RUN /install_updates

# Custom java.security (no cache, non-blocking and safe SecureRandom)
COPY docker-data/security ${JAVA_HOME}/conf/security

# Port we will expose
EXPOSE 8080/tcp

# Copy the fat jar into the image
COPY target/app.jar /app.jar

# Entry point
ENTRYPOINT ["java", "-Duser.timezone=UTC", \
            "-server", "-jar", "/app.jar"]
