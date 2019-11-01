package de.markusdamm.pokerapp.utils


import java.util.Calendar
import de.markusdamm.pokerapp.data.Player
import kotlin.math.floor

/**
 * Created by Markus Damm on 25.03.2015.
 */
object Utils {

    val date: String
        get() {
            val c = Calendar.getInstance()
            val year: Int = c.get(Calendar.YEAR)
            val month: Int = c.get(Calendar.MONTH) + 1
            val day: Int = c.get(Calendar.DAY_OF_MONTH)
            return "${addZeros(day)}.${addZeros(month)}.$year"
        }

    val time: String
        get() {
            val c = Calendar.getInstance()
            val hour: Int = c.get(Calendar.HOUR_OF_DAY)
            val minute: Int = c.get(Calendar.MINUTE)
            return "${addZeros(hour)}:${addZeros(minute)}"
        }

    private fun addZeros(i: Int): String {
        return if (i < 10) {
            "0$i"
        } else {
            i.toString()
        }
    }

    fun getPlayerById(players: Collection<Player>, id: Int): Player? {
        return players.firstOrNull { it.id == id }
    }

    fun getPlayerByName(playerList: Collection<Player>, name: String): Player? {
        return playerList.firstOrNull { it.name == name }
    }

    fun formatTimeToString(minuits: Int): String {
        val days = floor((minuits / (60 * 24)).toDouble()).toInt()
        val hours = floor(((minuits - days * 60 * 24) / 60).toDouble()).toInt()
        val hour = addZeros(hours)
        val rest = floor((minuits - days * 60 * 24 - hours * 60).toDouble()).toInt()
        val res = addZeros(rest)
        return "${days}d${hour}h${res}m"
    }
}
