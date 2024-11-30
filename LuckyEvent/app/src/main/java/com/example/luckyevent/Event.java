package com.example.luckyevent;

/**
 * A class representing an event.
 *
 * @author Mmelve
 * @version 2
 * @since 1
 */
public class Event implements Comparable<Event> {
    private final String eventId;
    private String name;
    private String dateTime;
    private String desc;
    private long createdAt;

    public Event(String eventId, String name, String dateTime, String desc) {
        this.eventId = eventId;
        this.name = name;
        this.dateTime = dateTime;
        this.desc = desc;
    }

    public Event(String eventId, String name, long createdAt, String dateTime, String desc) {
        this.eventId = eventId;
        this.name = name;
        this.createdAt = createdAt;
        this.dateTime = dateTime;
        this.desc = desc;
    }

    public String getEventId() {
        return eventId;
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

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public int compareTo(Event other) {
        // Sort in descending order (most recent first)
        return Long.compare(other.createdAt, this.createdAt);
    }
}
