package net.eekie.metrics.zoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObservableZoo extends ObservableSubject<ZooListener> {

    private List<Animal> animals = new ArrayList<>();

    public List<Animal> getAnimals() {
        return Collections.unmodifiableList(animals);
    }

    public void addAnimal (Animal animal) {
        // Add the animal to the list of animals
        this.animals.add(animal);
        // Notify the list of registered listeners
        this.notifyListeners(listener -> listener.onAnimalAdded(animal));
    }

    public void removeAnimal (Animal animal) {
        // Remove the animal from the list of animals
        this.animals.remove(animal);
        // Notify the list of registered listeners
        this.notifyListeners(listener -> listener.onAnimalRemoved(animal));
    }

}
