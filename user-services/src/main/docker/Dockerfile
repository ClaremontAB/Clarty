FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD clarty-user-services.jar clarty-user-services.jar
RUN sh -c 'touch /clarty-user-services.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -Djava.security.egd=file:/dev/./urandom -jar /clarty-user-services.jar" ]
