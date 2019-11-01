package de.markusdamm.pokerapp

import junit.framework.Assert
import junit.framework.TestCase

import java.util.ArrayList
import java.util.Date

import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.utils.Utils

class UtilsTest// dient zur Identifikation
(name: String) : TestCase(name) {
    fun testSimpleAdd() {
        val playerList = ArrayList<Player>()
        val p1 = Player("a", 1, 0)
        p1.id = 1
        val p2 = Player("b", 0, 0)
        p2.id = 2
        val p3 = Player("c", 0, 1)
        p3.id = 3
        playerList.add(p1)
        playerList.add(p2)
        playerList.add(p3)
        val expected = Utils.getPlayerById(playerList, 3)
        Assert.assertTrue(expected == p3)
    }
}