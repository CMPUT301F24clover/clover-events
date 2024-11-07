package com.example.luckyevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class LotteryTest {
    private List<String> entrantsList() {
        List<String> entrants = new ArrayList<>();
        entrants.add("A");
        entrants.add("B");
        entrants.add("C");
        entrants.add("D");
        return entrants;
    }

    @Test
    public void testInitialization() {
        List<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, 2);
        assertEquals(entrants, lottery.getEntrants());
        assertTrue(lottery.getWinners().isEmpty());
    }

    @Test
    public void testSelectWinnersNotEveryoneWins() {
        List<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, 2);
        lottery.selectWinners();

        assertEquals(2, lottery.getWinners().size());
        assertEquals(2, lottery.getEntrants().size());
    }

    @Test
    public void testSelectWinnersEveryoneWins() {
        List<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, 5);
        lottery.selectWinners();

        assertEquals(4, lottery.getWinners().size());
        assertTrue(lottery.getEntrants().isEmpty());
    }

    @Test
    public void testShuffling() {
        List<String> entrants = entrantsList();
        Lottery lottery = new Lottery(entrants, 4);
        lottery.selectWinners();

        assertNotEquals(entrants, lottery.getWinners());
    }

}
