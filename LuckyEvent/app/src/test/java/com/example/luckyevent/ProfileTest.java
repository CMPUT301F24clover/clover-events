package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests Profile class
 */
@RunWith(JUnit4.class)
public class ProfileTest {
    private String userName = "Neighbourhood Detective";
    private String firstName = "Nancy";
    private String lastName = "Drew";
    private String userId = "1234";

    private Profile profile() {
        return new Profile(userName, firstName, lastName, userId);
    }

    @Test
    public void testGetSetUserId() {
        Profile profile = profile();
        assertEquals(userId, profile.getUserId());

        String newUserId = "5678";
        profile.setUserId(newUserId);
        assertEquals(newUserId, profile.getUserId());
    }

    @Test
    public void testGetSetUserName() {
        Profile profile = profile();
        assertEquals(userName, profile.getUserName());

        String newUserName = "Incognito Mode";
        profile.setUserName(newUserName);
        assertEquals(newUserName, profile.getUserName());
    }

    @Test
    public void testGetSetNameAttributes() {
        Profile profile = profile();
        assertEquals(firstName, profile.getFirstName());
        assertEquals(lastName, profile.getLastName());
        assertEquals(String.format("%s %s", firstName, lastName), profile.getFullName());

        String newFirstName = "Joe";
        String newLastName = "Hardy";
        profile.setFirstName(newFirstName);
        profile.setLastName(newLastName);
        assertEquals(newFirstName, profile.getFirstName());
        assertEquals(newLastName, profile.getLastName());
        assertEquals(String.format("%s %s", newFirstName, newLastName), profile.getFullName());
    }
}
