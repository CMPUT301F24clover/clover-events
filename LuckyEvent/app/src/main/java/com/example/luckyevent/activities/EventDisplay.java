package com.example.luckyevent.activities;

public class EventDisplay {
    private String eventName;
    private String description;
    private String date;
    private String time;
    private String orgID;
    private String dueDate;
    private String status;

    public EventDisplay(String eventName, String description, String date, String time, String orgID, String dueDate, String status){
        this.eventName = eventName;
        this.description = description;
        this.date = date;
        this.time = time;
        this.orgID = orgID;
        this.dueDate = dueDate;
        this.status = status;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
