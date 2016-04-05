FROM java:openjdk-8-jdk-alpine

RUN mkdir metrics

ADD *.jar /metrics/app.jar
RUN sh -c 'touch /app.jar'
VOLUME /tmp

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "$JAVA_OPTS", "-jar", "/metrics/app.jar"]

EXPOSE 9875

