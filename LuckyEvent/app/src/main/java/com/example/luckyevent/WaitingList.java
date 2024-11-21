package com.example.luckyevent;

/**
 * A class representing an event's waiting list.
 *
 * @author Mmelve
 * @version 1
 * @since 1
 */
public class WaitingList {
    private String eventName;
    private String eventDateTime;
    private String eventDesc;
    private String waitingListStatus;

    public WaitingList(String eventName, String eventDateTime, String eventDesc, String waitingListStatus) {
        this.eventName = eventName;
        this.eventDateTime = eventDateTime;
        this.eventDesc = eventDesc;
        this.waitingListStatus = waitingListStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getWaitingListStatus() {
        return waitingListStatus;
    }

    public void setWaitingListStatus(String waitingListStatus) {
        this.waitingListStatus = waitingListStatus;
    }
}
