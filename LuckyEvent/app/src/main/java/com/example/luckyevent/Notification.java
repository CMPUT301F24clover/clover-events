package com.example.luckyevent;

/**
 * A class representing a user notification.
 *
 * @author Mmelve
 * @version 2
 * @since 1
 */
public class Notification {
    private String title;
    private String content;

    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
