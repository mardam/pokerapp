package de.markusdamm.pokerapp.data

import java.util.ArrayList
import java.util.Date

class Evening(var location: Location?, var date: Date?, val name: String) : Comparable<Evening> {
    var isFinished: Boolean = false
    var isStarted: Boolean = false
    val placements: ArrayList<Placement> = ArrayList()
    var id: Int = 0

    val numberOfParticipants: Int
        get() = placements.size

    val players: Set<Player>
        get() {
            val players: MutableSet<Player> = mutableSetOf()
            for (pm in placements) {
                players.add(pm.player)
                pm.winner?.let {
                    players.add(it)
                }
            }
            return players
        }

    val worstPlaceForUnsetPlayer: Int
        get() {
            var ret = placements.size
            for (pm in placements) {
                if (pm.number in 1..ret) {
                    ret = pm.number - 1
                }
            }
            return ret
        }

    init {
        this.isFinished = false
        this.isStarted = false
    }


    fun enterPlacement(pm: Placement) {
        placements.add(pm)
        placements.sort()
        if (pm.number == 1) {
            isFinished = true
        }
        if (pm.number > 0) {
            isStarted = true
        }
    }

    override fun compareTo(other: Evening): Int {
        return if (this.date == null && other.date == null) {
            0
        } else if (this.date != null && other.date != null) {
            this.date!!.compareTo(other.date)
        } else if (this.date != null) {
            1
        } else {
            -1
        }
    }
}