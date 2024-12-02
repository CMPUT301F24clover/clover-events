package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import com.example.luckyevent.entrant.UserSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UserSessionTest {
    private String userId;
    private String firstName;

    @Test
    public void testUserIdField() {
        userId = "7VbMRJwnQNUzcs2PUkvBhkjGrtC3";
        UserSession.getInstance().setUserId(userId);
        assertEquals(userId, UserSession.getInstance().getUserId());
    }

    @Test
    public void testFirstNameField() {
        firstName = "Jerry";
        UserSession.getInstance().setFirstName(firstName);
        assertEquals(firstName, UserSession.getInstance().getFirstName());
    }




}
