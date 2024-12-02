package com.example.luckyevent.entrant.displayProfileScreen;

/**
 * @author Amna
 * Model class that represents a users profile, this includes the name, email, and phone number
 */
public class ProfileSetup {
    private String name;
    private String email;
    private String phoneNumber;

    /**
     * sets the name for the users profile
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the name of the user
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * sets the email for the users profile
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * gets the email users email
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the phone for the users profile
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * gets the users phone number
     * @return the users phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
