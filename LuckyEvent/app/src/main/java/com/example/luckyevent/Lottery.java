package com.example.luckyevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A class representing the lottery process of drawing winners from the waiting list.
 *
 * Associated user stories:
 * US 02.05.02 As an organizer, I want to set the system to sample a specified number of attendees to register for the event
 * US 02.06.01 As an organizer, I want to view a list of all chosen entrants who are invited to apply
 */
public class Lottery {
    private List<Entrant> entrants;
    private List<Entrant> winners;
    private int sampleSize;

    public Lottery(List<Entrant> entrants, int sampleSize) {
        this.entrants = entrants;
        this.winners = new ArrayList<Entrant>();
        this.sampleSize = sampleSize;
    }

    /**
     * Note: Call setter every time waiting list updates. For example, if a winner accepts the
     * invitation, they should be removed from the waiting list.
     */
    public void setEntrants(List<Entrant> entrants) {
        this.entrants = entrants;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public List<Entrant> getWinners() {
        return winners;
    }

    public List<Entrant> getEntrants() {
        return entrants;
    }

    /**
     * Sets the winners list to be the entrants from position 0 to position sampleSize of the
     * shuffled list of entrants. If sampleSize is greater than the size of the entrants list,
     * then all the entrants are winners.
     * @return
     *    list of winners
     */
    public void selectWinners() {
        int size = entrants.size();

        if (sampleSize <= size) {
            List<Entrant> shuffledList = getShuffledList();
            winners = new ArrayList<Entrant>(shuffledList.subList(0, sampleSize));
            entrants = new ArrayList<Entrant>(shuffledList.subList(sampleSize, size));
        } else {
            winners = new ArrayList<Entrant>(entrants);
            entrants = new ArrayList<Entrant>();
        }
    }

    /**
     * Implements the Fisher-Yates' shuffle algorithm to randomly shuffle the entrants list.
     * @return
     *    shuffled list of entrants
     */
    private List<Entrant> getShuffledList() {
        List<Entrant> shuffledList = new ArrayList<Entrant>(entrants);
        Random r = new Random();
        int size = shuffledList.size();

        for (int i = size - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            Collections.swap(shuffledList, i, j);
        }

        return shuffledList;
    }

}
