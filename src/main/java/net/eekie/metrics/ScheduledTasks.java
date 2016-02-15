package net.eekie.metrics;

import net.eekie.metrics.config.ZooConfig;
import net.eekie.metrics.zoo.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    private final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ZooConfig zooConfig;

    private final Random randomNum = new Random();

    @Scheduled(fixedRateString = "${scheduledtasks.addremoveanimals.fixedRate}")
    public void addAndRemoveAnimals() {
        removeAnimalsRandomly();
        addAnimalRandomly();
    }

    private void addAnimalRandomly() {
        if (randomNum.nextInt(2)==1) {
            logger.info("adding 1 animal");
            zooConfig.zooWithAnimalCountMetricListener().addAnimal(new Animal(UUID.randomUUID().toString()));
        }
    }

    private void removeAnimalsRandomly() {
        List<Animal> animalsToRemove = zooConfig.zooWithAnimalCountMetricListener().getAnimals().stream().filter(animal -> randomNum.nextInt(2) == 1).collect(Collectors.toList());
        if (animalsToRemove.size() > 0) {
            logger.info("removing {} animals", animalsToRemove.size());
            animalsToRemove.forEach(animal -> zooConfig.zooWithAnimalCountMetricListener().removeAnimal(animal));
        }
    }

}