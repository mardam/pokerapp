package de.markusdamm.pokerapp.data

import android.util.Pair

import java.util.ArrayList

import de.markusdamm.pokerapp.utils.Utils

/**
 * Created by Markus Damm on 30.03.2015.
 */
class PlayerStatistic(val player: Player?) : Comparable<PlayerStatistic> {

    var bestPlace: Int = 0
    var worstPlace: Int = 0
    var wins: Int = 0
    var headUps: Int = 0
    var podiums: Int = 0
    var lastPlaces: Int = 0
    var participations: Int = 0
    var minuits: Int = 0
    var beatenPlayers: Int = 0
    var participators: Int = 0
    var sumOfPlaces: Int = 0
    var multikills: Int = 0
    var mostKills: Int = 0
        private set
    var median: Double = 0.toDouble()
    var sd: Double = 0.toDouble()
    var normalizedMean: Double = 0.toDouble()
    var average: Double = 0.toDouble()
    private var killed: List<String> = ArrayList()
    var mostDeaths: Int = 0
        private set
    private var killers: List<String> = ArrayList()
    val strings: ArrayList<String> = ArrayList()

    private var value1: String? = null
    private var value2: String? = null
    private var value3: String? = null

    private val mostKilled: String
        get() {
            var ret = ""
            for (kill in killed) {
                ret = if (ret === "") {
                    kill
                } else {
                    "$ret, $kill"
                }
            }
            return ret
        }

    private val mostKillers: String
        get() {
            var ret = ""
            for (kill in killers) {
                ret = if (ret === "") {
                    kill
                } else {
                    "$ret, $kill"
                }
            }
            return ret
        }


    val statisticList: ArrayList<String>
        get() {
            return arrayListOf("$stBestPlace: $bestPlace",
                    "$stWorstPlace: $worstPlace",
                    "$stWins: $wins",
                    "$stHeadUps: $headUps",
                    "$stPodiums: $podiums",
                    "$stLastPlaces: $lastPlaces",
                    "$stParticipations: $participations",
                    "$stMinuits: $minuits  Minuten bzw. ${Utils.formatTimeToString(minuits)}",
                    "$stBeatenPlayers: $beatenPlayers",
                    "$stNumberOfOponents: $participators",
                    "$stSumOfPlaces: $sumOfPlaces",
                    "$stWorsePlayer : ${(participators - sumOfPlaces)}",
                    "$stAveragePlace: $average",
                    "$stNormalizedMean: $normalizedMean",
                    "$stMedian: $median",
                    "$stSD: $sd",
                    "$stMultikills: $multikills",
                    buildMostKillers(),
                    buildMostKilled())
        }

    init {
        fillStrings()
    }


    fun setMostKills(value: Pair<Int, List<String>>) {
        this.mostKills = value.first
        this.killers = value.second
    }

    fun setMostDeaths(value: Pair<Int, List<String>>) {
        this.mostDeaths = value.first
        this.killed = value.second
    }


    fun setValues(value1: String, value2: String, value3: String) {
        this.value1 = value1
        this.value2 = value2
        this.value3 = value3
    }

    private fun buildMostKillers(): String {
        return if (mostKills == -1) {
            "$stMostKills: niemand"
        } else "$stMostKills: $mostKills Mal: $mostKillers"

    }

    private fun buildMostKilled(): String {
        return if (mostDeaths == -1) {
            "$stMostDeaths: niemanden"
        } else "$stMostDeaths: $mostDeaths Mal von $mostKilled"

    }

    fun <T : Number> getValue(value: String): T {
        when (value) {
            stBestPlace -> return Integer.valueOf(bestPlace) as T
            stWins -> return Integer.valueOf(wins) as T
            stMinuits -> return Integer.valueOf(minuits) as T
            stBeatenPlayers -> return Integer.valueOf(beatenPlayers) as T
            stNumberOfOponents -> return Integer.valueOf(participators) as T
            stSumOfPlaces -> return Integer.valueOf(sumOfPlaces) as T
            stWorsePlayer -> return Integer.valueOf(participators - sumOfPlaces) as T
            stParticipations -> return Integer.valueOf(participations) as T
            stAveragePlace -> return java.lang.Double.valueOf(average) as T
            stMultikills -> return Integer.valueOf(multikills) as T
            stHeadUps -> return Integer.valueOf(headUps) as T
            stPodiums -> return Integer.valueOf(podiums) as T
            stWorstPlace -> return Integer.valueOf(worstPlace) as T
            stLastPlaces -> return Integer.valueOf(lastPlaces) as T
            stMostKills -> return Integer.valueOf(mostKills) as T
            stMostDeaths -> return Integer.valueOf(mostDeaths) as T
            stMedian -> return java.lang.Double.valueOf(median) as T
            stSD -> return java.lang.Double.valueOf(sd) as T
            stNormalizedMean -> return java.lang.Double.valueOf(normalizedMean) as T
            else -> return Integer.valueOf(-1) as T
        }
    }


    private fun fillStrings() {
        strings.clear()
        strings.addAll(listOf(stBestPlace, stWorstPlace, stWins, stHeadUps, stPodiums, stLastPlaces, stParticipations,
                stMinuits, stBeatenPlayers, stNumberOfOponents, stSumOfPlaces, stWorsePlayer, stAveragePlace, stMedian,
                stSD, stNormalizedMean, stMultikills))
    }

    private fun inverseToSort(): List<String> {
        return arrayListOf(stBestPlace, stSumOfPlaces, stWorstPlace, stLastPlaces, stSD, stMedian, stAveragePlace, stNormalizedMean)
    }

    fun floatingValues(): List<String> {
        return arrayListOf(stSD, stMedian, stAveragePlace, stNormalizedMean)
    }

    private fun compareNumbers(x: Double, y: Double, value: String?): Int {
        return if (inverseToSort().contains(value)) {
            x.compareTo(y)
        } else {
            y.compareTo(x)
        }
    }

    override fun compareTo(other: PlayerStatistic): Int {
        var x: Double? = this.getValue<Number>(value1!!).toDouble()
        var y: Double? = other.getValue<Number>(value1!!).toDouble()

        if (x!!.toDouble() != y!!.toDouble()) {
            return compareNumbers(x, y, value1)
        }

        x = this.getValue<Number>(value2!!).toDouble()
        y = other.getValue<Number>(value2!!).toDouble()

        if (x.toDouble() != y.toDouble()) {
            return compareNumbers(x, y, value2)
        }

        x = this.getValue<Number>(value3!!).toDouble()
        y = other.getValue<Number>(value3!!).toDouble()

        return compareNumbers(x, y, value3)
    }

    companion object {

        const val stBestPlace = "Beste Platzierung"
        const val stWorstPlace = "Schlechteste Platzierung"
        const val stWins = "Siege"
        const val stHeadUps = "Heads-Ups"
        const val stPodiums = "Podiumsplätze"
        const val stParticipations = "Anzahl an Teilnahmen"
        const val stMinuits = "Gespielte Zeit"
        const val stBeatenPlayers = "Anzahl rausgeworfener Spieler"
        const val stNumberOfOponents = "Anzahl Gegner"
        const val stSumOfPlaces = "Summe der Plätze"
        const val stWorsePlayer = "Anzahl schlechtere Spieler bei Teilnahme"
        const val stAveragePlace = "Durchschnittliche Platzierung"
        const val stMultikills = "Multikills"
        const val stLastPlaces = "Letzte Plätze"
        const val stMostKills = "Häufigste getötete Gegner"
        const val stMostDeaths = "Am häufigsten getötet von"
        const val stSD = "Standardabweichung"
        const val stMedian = "Median"
        const val stNormalizedMean = "Normalisierter Durchschnitt über Teilnehmerzahl"
    }
}
