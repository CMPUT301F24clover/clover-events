package com.example.luckyevent;

public class Event {
    private String name;
    private String dateTime;
    private String desc;

    public Event(String name, String dateTime, String desc) {
        this.dateTime = dateTime;
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String date) {
        this.dateTime = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
