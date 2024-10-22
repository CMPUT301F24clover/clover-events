package com.example.luckyevent;

public class Notifications {
    private String title;
    private String content;

    public Notifications(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String content) {
        this.content = content;
    }
}
