package com.example.luckyevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.luckyevent.organizer.conductLottery.Lottery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

/**
 * Tests Lottery class
 */
@RunWith(JUnit4.class)
public class LotteryTest {
    private int sampleSize = 2;

    private ArrayList<String> entrantsList() {
        ArrayList<String> entrants = new ArrayList<>();
        entrants.add("A");
        entrants.add("B");
        entrants.add("C");
        entrants.add("D");
        return entrants;
    }

    @Test
    public void testInitialization() {
        ArrayList<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, sampleSize);

        assertEquals(entrants, lottery.getEntrants());
        assertTrue(lottery.getWinners().isEmpty());
        assertEquals(sampleSize, lottery.getSampleSize());
    }

    @Test
    public void testSelectWinnersNotEveryoneWins() {
        ArrayList<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, sampleSize);
        lottery.selectWinners();

        assertEquals(sampleSize, lottery.getWinners().size());
        assertEquals(entrants.size() - sampleSize, lottery.getEntrants().size());
    }

    @Test
    public void testSelectWinnersEveryoneWins() {
        ArrayList<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, entrants.size() + 1);
        lottery.selectWinners();

        assertEquals(entrants.size(), lottery.getWinners().size());
        assertTrue(lottery.getEntrants().isEmpty());
    }

    @Test
    public void testShuffling() {
        ArrayList<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, entrants.size());
        lottery.selectWinners();

        assertNotEquals(entrants, lottery.getWinners());
    }
}
