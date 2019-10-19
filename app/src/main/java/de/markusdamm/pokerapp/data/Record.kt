package de.markusdamm.pokerapp.data

import java.util.ArrayList

import de.markusdamm.pokerapp.utils.Utils
import kotlin.math.roundToInt

/**
 * Created by Markus Damm on 08.01.2018.
 */

class Record(private val position: Int, val evening: String, val player: String?, val value: String, private val type: String) {

    override fun toString(): String {
        return when(type) {
            "time" -> "$position: $evening ($value)"
            "minuts" -> "$position: $evening ($value  Minuten | ${Utils.formatTimeToString(Integer.parseInt(value))})"
            "percent" -> "$position: $evening (${(java.lang.Double.parseDouble(value) * 100).roundToInt() / 100.0}%)"
            "number" -> "$position: $evening ($value)"
            "time+player" -> "$position: $player ($evening | $value)"
            "minuts+player" -> "$position: $player ($evening  | $value Minuten | ${Utils.formatTimeToString(Integer.parseInt(value))})"
            "number+player" -> "$position: $player ($evening | $value)"
            else -> "Illegal type for record: " + this.type
        }
    }

    companion object {

        val possibleRecords: ArrayList<String>
            get() {
                return arrayListOf("Längster Abend", "Frühester Beginn", "Frühestes Ende", "Weiblichster Abend",
                        "Frühestes Ausscheiden", "Schnellstes Ausscheiden", "Schnellster Letzter", "Multikills",
                        "Meiste Teilnehmer", "Sieger", "Kills", "Tode", "Längste Killstreak")
            }

        fun getDBRequest(kind: String): String {
            return when (kind) {
                "Längster Abend" -> longestEvening
                "Frühester Beginn" -> earliestStart
                "Frühestes Ende" -> earliestEnd
                "Weiblichster Abend" -> mostFemaleEvening
                "Frühestes Ausscheiden" -> earliestDead
                "Schnellstes Ausscheiden" -> fastestDead
                "Meiste Teilnehmer" -> mostParticipants
                "Multikills" -> multikills
                "Schnellster Letzter" -> fastestLast
                "Sieger" -> winners
                "Kills" -> kills
                "Tode" -> deaths
                "Längste Killstreak" -> longestStreak
                else -> throw IllegalArgumentException("Illegal kind in getDBRequest for Record$kind")
            }
        }

        private val longestEvening: String
            get() = "SELECT e.name as name, " +
                    "((julianday(p.time) - julianday(e.date)) * 24 * 60) AS value " +
                    " FROM evenings as e, places as p " +
                    " WHERE e.id = p.evening AND p.nr = 1 AND e.name != 'Abend 1' " +
                    " order by value desc"

        private val earliestStart: String
            get() = "SELECT name, strftime('%H:%M',date) as value FROM evenings " + "order by value"

        private val earliestEnd: String
            get() = "SELECT e.name as name, strftime('%H:%M',p.TIME) as value " +
                    "FROM evenings as e, places as p " +
                    "WHERE e.id = p.evening and p.nr = 1 and e.name != 'Abend 1' " +
                    "ORDER BY strftime('%H:%M',DATETIME(p.TIME, '-12 hours'))"

        private val mostFemaleEvening: String
            get() = "select name, females, males, " +
                    "females * 100.0 / (males + females) as value " +
                    "from ( " +
                    "select e.name as name, " +
                    "SUM( case when pl.gender = 1 then 1 else 0 end) AS females, " +
                    "SUM( case when pl.gender = 0 then 1 else 0 end) AS males " +
                    "From evenings as e, places as p, players as pl " +
                    "where e.id = p.evening AND pl.id = p.loser " +
                    "group by e.id " +
                    ") order by value DESC"

        private val earliestDead: String
            get() = "select e.name as name, pl.name as player, \n" +
                    "strftime('%H:%M', p.time) as value\n" +
                    "from evenings as e, places as p, players as pl\n" +
                    "WHERE e.id = p.evening AND p.loser = pl.id AND e.name != 'Abend 1' AND p.time is not null \n" +
                    "ORDER BY strftime('%H:%M',DATETIME(p.TIME, '-12 hours'))"

        private val fastestDead: String
            get() = "SELECT * FROM (\n " +
                    "SELECT e.name as name, pl.name as player, \n" +
                    "((julianday(p.time) - julianday(e.date)) * 24 * 60) AS value \n" +
                    "FROM evenings as e, places as p, players as pl \n" +
                    "WHERE e.id = p.evening and pl.id = p.loser AND e.name != 'Abend 1') \n" +
                    "WHERE value IS NOT NULL \n" +
                    "ORDER BY value ASC"

        private val mostParticipants: String
            get() = "select e.name as name, count(*) as value " +
                    "from places as p, evenings as e " +
                    "where p.evening = e.id " +
                    "group by e.id " +
                    "order by value desc"

        private val multikills: String
            get() = "SELECT player, name, value\n" +
                    "FROM (\n" +
                    "SELECT pl.name as player, e.name as name, time, count(*) as value, count(DISTINCT p.winner) as winners\n" +
                    "FROM places as p, evenings as e, players as pl\n" +
                    "WHERE p.evening = e.id and e.name != 'Abend 1' and p.nr != 1 and pl.id = p.winner\n" +
                    "group by time\n" +
                    ") WHERE value > 1 and winners = 1\n" +
                    "ORDER BY value DESC, player"

        private val fastestLast: String
            get() = "SELECT pl.name as player, ((julianday(p1.time) - julianday(e1.date)) * 24 * 60) AS value, e1.name as name FROM places p1\n" +
                    "INNER JOIN (SELECT evening, count(*) as c FROM places GROUP BY evening) p2 ON p1.evening = p2.evening\n" +
                    "INNER JOIN players pl ON pl.id = p1.loser\n" +
                    "INNER JOIN evenings e1 ON e1.id = p1.evening\n" +
                    "WHERE p2.c = p1.nr AND e1.name != 'Abend 1'\n" +
                    "ORDER BY value"

        private val winners: String
            get() = "SELECT e.name as name, pl2.name as player, pl.name as value\n" +
                    "FROM evenings as e, players as pl, places as p, players as pl2\n" +
                    "WHERE e.id = p.evening and p.nr = 2 and p.loser = pl.id and pl2.id = p.winner\n" +
                    "order by e.date ASC"

        private val kills: String
            get() = "SELECT count(*) as value, p1.name as player, p2.name as name\n" +
                    "FROM places p, players p1, players p2\n" +
                    "WHERE p.nr != 1 AND p.evening != 1 AND p1.id = p.winner and p2.id = p.loser\n" +
                    "GROUP BY winner, loser\n" +
                    "ORDER BY value DESC, player"

        private val deaths: String
            get() = "SELECT count(*) as value, p1.name as name, p2.name as player\n" +
                    "FROM places p, players p1, players p2\n" +
                    "WHERE p.nr != 1 AND p.evening != 1 AND p1.id = p.winner and p2.id = p.loser\n" +
                    "GROUP BY winner, loser\n" +
                    "ORDER BY value DESC, player"

        private val longestStreak: String
            get() = "with v as (SELECT p.nr as position, e.name as evening, pl.name as player, p.time as time FROM players pl, places p, evenings e\n" +
                    "where pl.id = p.winner AND e.id = p.evening),\n" +
                    "w as (SELECT evening, player as name, (select count(*) from v as v2 where v.time > v2.time OR (v.position < v2.position AND v.time = v2.time)) as nummer FROM v\n" +
                    "ORDER BY nummer ASC),\n" +
                    "x as (select p1.evening, p1.nummer as Nummer1, p2.nummer as Nummer2, p1.name as name from w p1, w p2 where p2.name == p1.name and p1.nummer <= p2.nummer and p2.nummer - p1.nummer < 15),\n" +
                    "y as (select distinct t1.evening, t1.nummer1, t1.nummer2, t1.name from x as t1, x as t2\n" +
                    "where (t2.nummer1 > t1.nummer1 and t2.nummer1 < t1.nummer2) and t1.name != t2.name),\n" +
                    "z1 as (select * from x EXCEPT select * from y),\n" +
                    "z as (select * from z1 where nummer1 != nummer2),\n" +
                    "a as (select z1.evening, z2.nummer1, z2.nummer2, z2.name from z as z1, z as z2\n" +
                    "where z1.name = z2.name  and (z1.nummer1 != z2.nummer1 or z1.nummer2 != z2.nummer2) \n" +
                    "and ((z2.nummer1 > z1.nummer1 and z2.nummer1 < z1.nummer2)\n" +
                    "or (z2.nummer2 > z1.nummer1 and z2.nummer2 < z1.nummer2)))\n" +
                    "SELECT evening as name, name as player, nummer2-nummer1 + 1 as value FROM (select * from z EXCEPT select * from a) ORDER BY value DESC"

        fun getType(kind: String): String {
            return when(kind) {
                "Längster Abend" -> "minuts"
                "Frühester Beginn" -> "time"
                "Frühestes Ende" -> "time"
                "Weiblichster Abend" -> "percent"
                "Frühestes Ausscheiden" -> "time+player"
                "Schnellstes Ausscheiden" -> "minuts+player"
                "Meiste Teilnehmer" -> "number"
                "Multikills" -> "number+player"
                "Schnellster Letzter" -> "minuts+player"
                "Sieger" -> "number+player"
                "Kills" -> "number+player"
                "Tode" -> "number+player"
                "Längste Killstreak" -> "number+player"
                else -> throw IllegalArgumentException("Illegal kind for type in Records")
            }
        }
    }
}