package net.eekie.metrics.zoo;

public interface ZooListener {

    void onAnimalAdded(Animal animal);

    void onAnimalRemoved(Animal animal);

}