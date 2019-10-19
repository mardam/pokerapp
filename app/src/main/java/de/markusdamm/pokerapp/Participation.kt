package de.markusdamm.pokerapp

import de.markusdamm.pokerapp.data.Evening
import de.markusdamm.pokerapp.data.Player

/**
 * Created by Markus Damm on 06.01.2019.
 */

class Participation(var evening: Evening, var position: Int, private val max: Int, private val beatenBy: Player) {

    override fun toString(): String {
        var ret = "${evening.name}: $position von $max"
        beatenBy.name.let {
            ret += " (Killer: $it )"
        }
        return ret
    }
}
