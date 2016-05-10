package net.eekie.metrics.config;

import com.codahale.metrics.*;
import com.codahale.metrics.jvm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

@Configuration
public class MetricsConfig {

    private final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);
    private MetricRegistry metricRegistry = new MetricRegistry();
    private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
    private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
    private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
    private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
    private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";

    @Value("${metrics.consolereporting.interval:15}")
    private Long consoleReportingInterval;

    @Value("${metrics.logs.enabled}")
    private boolean logStashEnabled;

    @Value("${metrics.logs.reportFrequency}")
    private int reportFrequency;

    @Bean
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Bean
    public ConsoleReporter getConsoleReporter() {
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(getMetricRegistry())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter((s, metric) -> "animal-count".equalsIgnoreCase(s))
                .build();
        reporter.start(consoleReportingInterval, TimeUnit.SECONDS);
        logger.info("Initialized Dropwizard Metrics console reporter with interval of {} seconds", consoleReportingInterval);
        return reporter;
    }

    @PostConstruct
    public void init() {
        logger.info("Registering JVM gauges");
        metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
        metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS, new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        final JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
        reporter.start();
    }

    @PostConstruct
    public void initSlf4jReporter() {
        if (logStashEnabled) {
            logger.info("Initializing Metrics Log reporting");
            final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                .outputTo(LoggerFactory.getLogger("metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
            reporter.start(reportFrequency, TimeUnit.SECONDS);
        }

    }

}
