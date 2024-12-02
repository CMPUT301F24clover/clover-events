package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import com.example.luckyevent.shared.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests Event class
 */
@RunWith(JUnit4.class)
public class EventTest {
    private final String eventId = "1234";
    private String name = "Sunny Event";
    private String dateTime = "July 16, 2025 · 12 - 4pm";
    private String desc = "Enjoy a day in the sun!";

    private Event event() {
        return new Event(eventId, name, dateTime, desc);
    }

    @Test
    public void testGetEventId() {
        Event event = event();
        assertEquals(eventId, event.getEventId());
    }

    @Test
    public void testGetSetName() {
        Event event = event();
        assertEquals(name, event.getName());

        String newName = "Basking on the beach";
        event.setName(newName);
        assertEquals(newName, event.getName());
    }

    @Test
    public void testGetSetDateTime() {
        Event event = event();
        assertEquals(dateTime, event.getDateTime());

        String newDateTime = "July 22, 2025 · 12 - 4pm";
        event.setDateTime(newDateTime);
        assertEquals(newDateTime, event.getDateTime());
    }

    @Test
    public void testGetSetDesc() {
        Event event = event();
        assertEquals(desc, event.getDesc());

        String newDesc = "Don't forget to bring sunscreen!";
        event.setDesc(newDesc);
        assertEquals(newDesc, event.getDesc());
    }
}
