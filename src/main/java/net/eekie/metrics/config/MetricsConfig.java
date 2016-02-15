package net.eekie.metrics.config;

import com.codahale.metrics.*;
import com.codahale.metrics.jvm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class MetricsConfig {

    private final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);
    private MetricRegistry metricRegistry = new MetricRegistry();
    private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
    private static final String PROP_METRIC_REG_JVM_THREAD = "jvm.thread";
    private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";

    @Value("${metrics.consolereporting.interval:15}")
    private Long consoleReportingInterval;

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
        metricRegistry.register(PROP_METRIC_REG_JVM_THREAD, new ThreadStatesGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        final JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
        reporter.start();
    }

}