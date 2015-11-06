package de.markusdamm.pokerapp;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;

import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.utils.Utils;

public class UtilsTest extends TestCase {
    public void testSimpleAdd() {
        ArrayList<Player> playerList = new ArrayList<>();
        Player p1 = new Player("a",1,0);
        p1.setID(1);
        Player p2 = new Player("b",0,0);
        p2.setID(2);
        Player p3 = new Player("c",0,1);
        p3.setID(3);
        playerList.add(p1);
        playerList.add(p2);
        playerList.add(p3);
        Player expected = Utils.getPlayerFromListById(playerList,3);
        Assert.assertTrue(expected == p3);
    }


    // dient zur Identifikation
     public UtilsTest(String name) {
     super(name);
     }
}