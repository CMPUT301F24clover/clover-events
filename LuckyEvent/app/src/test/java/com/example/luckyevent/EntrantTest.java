package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests Entrant class
 */
@RunWith(JUnit4.class)
public class EntrantTest {
    private final String id = "1234";
    private String name;
    Entrant entrant = new Entrant(id, name);

    @Test
    public void testGetUserId() {
        assertEquals(id, entrant.getEntrantId());
    }

    @Test
    public void testNameFields() {
        name = "John Doe";
        entrant.setName(name);
        assertEquals(name, entrant.getName());
    }
}
