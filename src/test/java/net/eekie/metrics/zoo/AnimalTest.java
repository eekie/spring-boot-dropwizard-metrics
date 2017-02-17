package net.eekie.metrics.zoo;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnimalTest {

    @Test
    public void testGetId() throws Exception {
        Animal animal = new Animal("1234");
        assertTrue(animal.getId().equals("1234"));
    }

}
