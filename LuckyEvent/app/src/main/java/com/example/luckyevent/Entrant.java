package com.example.luckyevent;

/**
 * A class representing an entrant.
 *
 * @author Mmelve
 * @version 2
 * @since 1
 */
public class Entrant {
    private final String entrantId;
    private String name;
    private String invitationStatus;
    private String profileImageUrl;

    // delete this constructor later
    public Entrant(String entrantId, String name) {
        this.entrantId = entrantId;
        this.name = name;
        invitationStatus = "n/a";
    }

    // delete this constructor later
    public Entrant(String entrantId, String name, String invitationStatus) {
        this.entrantId = entrantId;
        this.name = name;
        this.invitationStatus = invitationStatus;
    }

//    public Entrant(String entrantId, String name, String profileImageUrl) {
//        this.entrantId = entrantId;
//        this.name = name;
//        invitationStatus = "n/a";
//        this.profileImageUrl = profileImageUrl;
//    }
//
//    public Entrant(String entrantId, String name, String invitationStatus, String profileImageUrl) {
//        this.entrantId = entrantId;
//        this.name = name;
//        this.invitationStatus = invitationStatus;
//        this.profileImageUrl = profileImageUrl;
//    }

    public String getEntrantId() {
        return entrantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
