package com.example.luckyevent;

public class EventSetting {
    private String eventName;
    private String eventDate;
    private String eventId;
    private String eventDesc;

    public EventSetting(String eventId, String eventName, String eventDate, String eventDesc){
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDesc = eventDesc;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
}
