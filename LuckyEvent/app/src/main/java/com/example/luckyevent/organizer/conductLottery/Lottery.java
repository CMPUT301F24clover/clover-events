package com.example.luckyevent.organizer.conductLottery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A class representing the lottery process of drawing winners from a given waiting list.
 *
 * @author Mmelve
 * @version 2
 * @since 1
 */
public class Lottery implements Serializable {
    private ArrayList<String> entrants;
    private ArrayList<String> winners;
    private int sampleSize;

    public Lottery(ArrayList<String> entrants, int sampleSize) {
        this.entrants = entrants;
        this.winners = new ArrayList<>();
        this.sampleSize = sampleSize;
    }

    public ArrayList<String> getEntrants() {
        return entrants;
    }

    public ArrayList<String> getWinners() {
        return winners;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Sets the winners list to be the entrants from position 0 to position sampleSize of the
     * shuffled list of entrants. If sampleSize is greater than the size of the entrants list,
     * then all the entrants are winners.
     */
    public void selectWinners() {
        int size = entrants.size();

        if (sampleSize <= size) {
            ArrayList<String> shuffledList = getShuffledList();
            winners = new ArrayList<>(shuffledList.subList(0, sampleSize));
            entrants = new ArrayList<>(shuffledList.subList(sampleSize, size));
        } else {
            winners = new ArrayList<>(entrants);
            entrants = new ArrayList<>();
        }
    }

    /**
     * Implements the Fisher-Yates' shuffle algorithm to randomly shuffle the entrants list.
     * @return A shuffled list of entrants.
     */
    private ArrayList<String> getShuffledList() {
        ArrayList<String> shuffledList = new ArrayList<>(entrants);
        Random r = new Random();
        int size = shuffledList.size();

        for (int i = size - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            Collections.swap(shuffledList, i, j);
        }

        return shuffledList;
    }

}
