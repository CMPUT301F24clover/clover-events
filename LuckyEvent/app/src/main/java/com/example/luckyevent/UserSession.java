package com.example.luckyevent;

/**
 * A class used to store the details of the currently signed in entrant
 *
 * @author Seyi
 * @version 1
 * @since 1
 */
public class UserSession {
    private static UserSession instance;
    //Additional values can be added
    private String userId;
    private String firstName;
    private String profileUri = null;
    private String lastName;
    private String userName;
    private Boolean notificationDisabled;
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

    public void setProfileUri(String profileUri){
        this.profileUri = profileUri;
    }

    public String getProfileUri (){
        return profileUri;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNotificationDisabled(boolean notificationDisabled) {
        this.notificationDisabled = notificationDisabled;
    }

    public Boolean isNotificationDisabled() {
        // Return false as default if not set
        return notificationDisabled != null ? notificationDisabled : false;
    }

    //The userId can be accessed by any activity like this:
    //String userId = UserSession.getInstance().getUserId();

}
