package com.example.luckyevent;

public class UserSession {
    private static UserSession instance;
    //Additional values can be added
    private String userId;
    private String firstName;
    private String lastName;
    private String userName;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    //The userId can be accessed by any activity like this:
    //String userId = UserSession.getInstance().getUserId();

}
