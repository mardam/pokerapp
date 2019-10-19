package de.markusdamm.pokerapp.data

import java.util.Date

/**
 * Created by Markus Damm on 23.03.2015.
 */
class Placement(val number: Int, var player: Player) : Comparable<Placement> {
    var winner: Player? = null
    var date: Date? = null

    override fun compareTo(other: Placement): Int {
        if (this.number == other.number) {
            return 0
        }
        return if (this.number < other.number) -1 else 1
    }
}
