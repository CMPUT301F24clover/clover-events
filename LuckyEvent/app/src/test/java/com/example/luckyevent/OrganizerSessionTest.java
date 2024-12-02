package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import com.example.luckyevent.organizer.displayHomePage.OrganizerSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OrganizerSessionTest {
    private String userId;
    private String facilityCode;
    private String firstName;
    private String lastName;
    private String userName;
    private String facilityName;
    private String role;
    private boolean hasEventProfile;


    @Test
    public void testUserIdField() {
        userId = "7VbMRJwnQNUzcs2PUkvBhkjGrtC3";
        OrganizerSession.getInstance().setUserId(userId);
        assertEquals(userId, OrganizerSession.getInstance().getUserId());
    }

    @Test
    public void testFacilityCodeField() {
        facilityCode = "Aj6S92C";
        OrganizerSession.getInstance().setFacilityCode(facilityCode);
        assertEquals(facilityCode, OrganizerSession.getInstance().getFacilityCode());
    }

    @Test
    public void testFirstNameField() {
        firstName = "Jerry";
        OrganizerSession.getInstance().setFirstName(firstName);
        assertEquals(firstName, OrganizerSession.getInstance().getFirstName());
    }

    @Test
    public void testLastNameField() {
        lastName = "Doe";
        OrganizerSession.getInstance().setLastName(lastName);
        assertEquals(lastName, OrganizerSession.getInstance().getLastName());
    }

    @Test
    public void testUserNameField() {
        userName = "JohnDoe";
        OrganizerSession.getInstance().setUserName(userName);
        assertEquals(userName, OrganizerSession.getInstance().getUserName());
    }

    @Test
    public void testFacilityNameField() {
        facilityName = "Doe.inc";
        OrganizerSession.getInstance().setFacilityName(facilityName);
        assertEquals(facilityName, OrganizerSession.getInstance().getFacilityName());
    }

    @Test
    public void testRoleField() {
        role = "organizer";
        OrganizerSession.getInstance().setRole(role);
        assertEquals(role, OrganizerSession.getInstance().getRole());
    }

    @Test
    public void testHasEventProfileField() {
        hasEventProfile = true;
        OrganizerSession.getInstance().setHasEventProfile(hasEventProfile);
        assertEquals(hasEventProfile, OrganizerSession.getInstance().getHasEventProfile());
    }






}
