package com.example.luckyevent;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a list of entrants that want to sign up for an event.
 *
 * Associated user stories:
 * US 01.01.01 As an entrant, I want to join the waiting list for a specific event
 * US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
 * US 02.03.01 As an organizer, I want to OPTIONALLY limit the number of entrants who can join my waiting list
 * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply
 */
public class WaitingList {
    private List<Entrant> entrants;
    private int limit;

    public WaitingList() {
        this.entrants = new ArrayList<Entrant>();
        this.limit = -1;  // no limit to the number of entrants
    }

    public WaitingList(int limit) {
        this.entrants = new ArrayList<Entrant>();
        this.limit = limit;
    }

    public List<Entrant> getEntrants() {
        return entrants;
    }

    public void addEntrant(Entrant entrant) {
        if ( (limit == -1) || (entrants.size() < limit) ) {
            entrants.add(entrant);
        } //else
    }

    public void removeEntrant(Entrant entrant) {
        entrants.remove(entrant);
    }
}
