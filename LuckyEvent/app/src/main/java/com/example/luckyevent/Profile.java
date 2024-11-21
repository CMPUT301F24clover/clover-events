package com.example.luckyevent;

public class Profile {
    private String userName;
    private String firstName;
    private String lastName;
    private String userId;

    public Profile(String userName, String firstName, String lastName, String userId){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
