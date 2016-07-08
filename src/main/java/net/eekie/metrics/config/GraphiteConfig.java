package net.eekie.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * This configuration will only be enabled when the graphite.enabled property is set to true. It will configure and
 * register graphite reporter for the dropwizard metrics. If you run the project with docker-compose, the property will
 * automatically be set (see docker-compose.yml) It will also pull an image with Graphite + Grafana dashboard to allow
 * the demo app sent metrics to a real Graphite server.
 */
@Configuration
@EnableConfigurationProperties(GraphiteProperties.class)
@ConditionalOnExpression("${graphite.enabled:true}")
public class GraphiteConfig {

    private final Logger logger = LoggerFactory.getLogger(GraphiteConfig.class);

    @Autowired
    GraphiteProperties graphiteProperties;

    @Bean
    public GraphiteReporter graphiteReporter(MetricRegistry metricRegistry) {
        logger.info("registering graphite reporter");
        final GraphiteReporter reporter = GraphiteReporter
                .forRegistry(metricRegistry)
                .build(graphite());
        reporter.start(1, TimeUnit.SECONDS);
        return reporter;
    }

    @Bean
    GraphiteSender graphite() {
        return new Graphite(new InetSocketAddress(graphiteProperties.getServerHost(), graphiteProperties.getServerPort()));
    }

}

@ConfigurationProperties(prefix = "graphite", ignoreUnknownFields = false)
class GraphiteProperties {
    private boolean enabled = true;
    private String serverHost = "localhost";
    private int serverPort = 2003;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
