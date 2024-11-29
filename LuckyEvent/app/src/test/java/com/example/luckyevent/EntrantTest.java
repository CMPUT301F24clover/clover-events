package com.example.luckyevent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests Entrant class
 */
@RunWith(JUnit4.class)
public class EntrantTest {
    private final String entrantId = "1234";
    private String name = "Mickey Mouse";
    private String invitationStatus = "Invited";
    private String imageUrl = "image1url";

    private Entrant entrantWithoutInvitation() {
        return new Entrant(entrantId, name, imageUrl);
    }

//    private Entrant entrantWithInvitation() {
//        return new Entrant(entrantId, name, invitationStatus, imageUrl);
//    }

    @Test
    public void testGetEntrantId() {
        Entrant entrant = entrantWithoutInvitation();
        assertEquals(entrantId, entrant.getEntrantId());
    }

    @Test
    public void testGetSetName() {
        Entrant entrant = entrantWithoutInvitation();
        assertEquals(name, entrant.getName());

        String newName = "Donald Duck";
        entrant.setName(newName);
        assertEquals(newName, entrant.getName());
    }

//    @Test
//    public void testGetSetInvitationStatus() {
//        Entrant uninvitedEntrant = entrantWithoutInvitation();
//        assertEquals("n/a", uninvitedEntrant.getInvitationStatus());
//
//        Entrant invitedEntrant = entrantWithInvitation();
//        assertEquals(invitationStatus, invitedEntrant.getInvitationStatus());
//
//        String updatedInvitationStatus = "Accepted";
//        invitedEntrant.setInvitationStatus(updatedInvitationStatus);
//        assertEquals(updatedInvitationStatus, invitedEntrant.getInvitationStatus());
//    }
//
//    @Test
//    public void testGetSetProfileImageUrl() {
//        Entrant entrant = entrantWithoutInvitation();
//        assertEquals(imageUrl, entrant.getProfileImageUrl());
//
//        String newImageUrl = "image2url";
//        entrant.setProfileImageUrl(newImageUrl);
//        assertEquals(newImageUrl, entrant.getProfileImageUrl());
//    }
}
