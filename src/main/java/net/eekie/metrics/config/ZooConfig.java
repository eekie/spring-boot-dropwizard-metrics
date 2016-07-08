package net.eekie.metrics.config;

import com.codahale.metrics.Counter;
import net.eekie.metrics.zoo.Animal;
import net.eekie.metrics.zoo.ZooListener;
import net.eekie.metrics.zoo.ObservableZoo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZooConfig {

    public final Logger logger = LoggerFactory.getLogger(ZooConfig.class);

    @Autowired
    private MetricsConfig metricsConfig;

    @Bean
    public ObservableZoo zooWithAnimalCountMetricListener() {
        ObservableZoo zoo = new ObservableZoo();
        zoo.registerListener(dropwizardMetricAnimalCountZooListener());
        return zoo;
    }

    @Bean
    public ZooListener dropwizardMetricAnimalCountZooListener() {

        final Counter animals = metricsConfig.getMetricRegistry().counter("animal-count");

        return new ZooListener() {

            @Override
            public void onAnimalAdded(Animal animal) {
                animals.inc();
                logger.debug("metric captured for animal added '{}', current size: {}", animal.getId(), animals.getCount());
            }

            @Override
            public void onAnimalRemoved(Animal animal) {
                animals.dec();
                logger.debug("metric captured for animal removed '{}', current size: {}", animal.getId(), animals.getCount());
                if (animals.getCount() == 0) {
                    logger.error("woohoo, simulate an error", new RuntimeException("testing runtime exception"));
                }
            }

        };

    }

}
