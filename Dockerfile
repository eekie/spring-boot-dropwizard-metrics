FROM java:8-jre

RUN apt-get update && apt-get install -y \
    haveged \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir metrics

ADD target/dropwizard-metrics-1.0-SNAPSHOT.jar /metrics/

CMD ["java", "$JAVA_OPTS", ""-jar", "/metrics/dropwizard-metrics-1.0-SNAPSHOT.jar"]
