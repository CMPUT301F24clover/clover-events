package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NotificationTest {
    private String title;
    private String content;
    Notification notification = new Notification(title,content);

    @Test
    public void testTitleField() {
        title = "Sunny Event";
        notification.setTitle(title);
        assertEquals(title, notification.getTitle());
    }

    @Test
    public void testContentField() {
        content = "Join us for some fun";
        notification.setContent(content);
        assertEquals(content, notification.getContent());
    }
}
