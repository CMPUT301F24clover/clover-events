package com.example.luckyevent;

/**
 * A class used to store the details of the currently signed in organizer
 *
 * @author Tola
 * @version 1
 * @since 1
 */
public class OrganizerSession {
    private static OrganizerSession instance;

    private String userId;
    private String facilityCode;
    private String firstName;
    private String lastName;
    private String userName;
    private String facilityName;
    private String role;
    private boolean hasEventProfile;

    private OrganizerSession() {}

    public static OrganizerSession getInstance() {
        if (instance == null) {
            instance = new OrganizerSession();
        }
        return instance;
    }

    // Facility Code
    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    // First Name
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    // Last Name
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    // User Name
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    // Organization Name
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    // Role
    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    // User ID
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    // Has Event Profile
    public void setHasEventProfile(boolean hasEventProfile) {
        this.hasEventProfile = hasEventProfile;
    }

    public boolean getHasEventProfile() {
        return hasEventProfile;
    }

    // Method to clear session data when logging out
    public void clearSession() {
        userId = null;
        facilityCode = null;
        firstName = null;
        lastName = null;
        userName = null;
        facilityName = null;
        role = null;
        hasEventProfile = false;
    }
}