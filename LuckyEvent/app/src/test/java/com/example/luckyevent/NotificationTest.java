package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import com.example.luckyevent.entrant.displayNotifsScreen.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests Notification class
 */
@RunWith(JUnit4.class)
public class NotificationTest {
    private final String notifId = "1234";
    private String title = "Sunny Event";
    private String content = "Enjoy a day in the sun!";

    private Notification notification() {
        return new Notification(notifId, title, content);
    }

    @Test
    public void testGetNotifId() {
        Notification notif = notification();
        assertEquals(notifId, notif.getNotifId());
    }

    @Test
    public void testGetSetTitle() {
        Notification notif = notification();
        assertEquals(title, notif.getTitle());

        String newTitle = "Basking on the beach";
        notif.setTitle(newTitle);
        assertEquals(newTitle, notif.getTitle());
    }

    @Test
    public void testGetSetContent() {
        Notification notif = notification();
        assertEquals(content, notif.getContent());

        String newContent = "Don't forget to bring sunscreen!";
        notif.setContent(newContent);
        assertEquals(newContent, notif.getContent());
    }
}
