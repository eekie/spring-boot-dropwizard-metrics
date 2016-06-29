package net.eekie.metrics.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import net.logstash.logback.appender.LogstashSocketAppender;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class LoggingConfiguration {

    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    @Value("${spring.application.name}")
    private String appName;

    //@Value("${server.port}")
    private String serverPort;

    //@Value("${eureka.instance.instanceId}")
    private String instanceId;

    @Value("${logging.logstash.enabled}")
    private boolean logstashEnabled;
    @Value("${logging.logstash.host}")
    private String logstashHost;
    @Value("${logging.logstash.port}")
    private int logstashPort;
    @Value("${logging.logstash.queueSize}")
    private int logStashQueueSize;

    @PostConstruct
    private void init() {
        if (logstashEnabled) {
            addLogstashAppender();
        }
    }

    public void addLogstashAppender() {
        log.info("Initializing Logstash logging");
        LogstashSocketAppender logstashAppender = new LogstashSocketAppender();
        logstashAppender.setName("LOGSTASH");
        logstashAppender.setContext(context);
        ShortenedThrowableConverter shortenedThrowableConverter = new ShortenedThrowableConverter();
        shortenedThrowableConverter.setMaxLength(2048);
        shortenedThrowableConverter.setMaxDepthPerThrowable(30);
        shortenedThrowableConverter.setRootCauseFirst(true);
        shortenedThrowableConverter.setShortenedClassNameLength(20);
        shortenedThrowableConverter.addExclude("sun\\.reflect\\..*\\.invoke.*");
        shortenedThrowableConverter.addExclude("net\\.sf\\.cglib\\.proxy\\.MethodProxy\\.invoke");
        logstashAppender.setThrowableConverter(shortenedThrowableConverter);
        String customFields = "{\"app_name\":\"" + appName + "\",\"app_port\":\"" + serverPort + "\"," +
            "\"instance_id\":\"" + instanceId + "\"}";

        // Set the Logstash appender config from JHipster properties
        logstashAppender.setSyslogHost(logstashHost);
        logstashAppender.setPort(logstashPort);
        logstashAppender.setCustomFields(customFields);
        logstashAppender.start();

        // Wrap the appender in an Async appender for performance
        AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setContext(context);
        asyncLogstashAppender.setName("ASYNC_LOGSTASH");
        asyncLogstashAppender.setQueueSize(logStashQueueSize);
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();

        context.getLogger("ROOT").addAppender(asyncLogstashAppender);
    }
}
