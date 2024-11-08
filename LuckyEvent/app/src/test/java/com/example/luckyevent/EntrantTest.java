package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EntrantTest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    Entrant entrant = new Entrant(firstName,lastName,email);

    @Test
    public void testFirstNameFields() {
        firstName = "John";
        entrant.setFirstName(firstName);
        assertEquals(firstName, entrant.getFirstName());
    }

    @Test
    public void testLastNameFields() {
        lastName = "Doe";
        entrant.setLastName(lastName);
        assertEquals(lastName, entrant.getLastName());
    }

    @Test
    public void testEmailFields() {
         email= "JohnDoe@gmail.com";
        entrant.setEmail(email);
        assertEquals(email, entrant.getEmail());
    }

    @Test
    public void testPhoneNumberFields() {
        phoneNumber = "(415) 867-5309";
        entrant.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, entrant.getPhoneNumber());
    }


}
