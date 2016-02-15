package net.eekie.metrics.config.jmx;

import net.eekie.metrics.zoo.Animal;
import net.eekie.metrics.zoo.ObservableZoo;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.UUID;

@ManagedResource
public class ZooResource {

    private final ObservableZoo observableZoo;

    public ZooResource(ObservableZoo observableZoo) {
        this.observableZoo = observableZoo;
    }

    @ManagedOperation
    public void addAnimal() {
        observableZoo.addAnimal(new Animal(UUID.randomUUID().toString()));
    }

    @ManagedAttribute
    public int getAnimalCount() {
        return observableZoo.getAnimals().size();
    }

}
