package de.markusdamm.pokerapp.data

/**
 * Created by Markus Damm on 30.03.2015.
 */
object Gender {
    const val FEMALE = true
    const val MALE = false
    const val FEMALE_INT = 1
    const val MALE_INT = 0
    const val BOTH_INT = 2
    const val MALE_STRING = "männlich"
    const val FEMALE_STRING = "weiblich"
    const val BOTH_STRING = "beide"

    fun getIntByString(st: String): Int {
        return when (st) {
            MALE_STRING -> MALE_INT
            FEMALE_STRING -> FEMALE_INT
            BOTH_STRING -> BOTH_INT
            else -> throw NullPointerException()
        }
    }

    fun toInt(b: Boolean): Int {
        return if (b == Gender.MALE) {
            MALE_INT
        } else FEMALE_INT
    }

    fun toBool(i: Int): Boolean {
        return if (i == Gender.MALE_INT) {
            MALE
        } else FEMALE
    }

    fun getString(b: Boolean): String {
        return if (b == MALE) {
            MALE_STRING
        } else FEMALE_STRING
    }

    fun getStringByInt(i: Int): String {
        return if (i == MALE_INT) {
            MALE_STRING
        } else {
            if (i == FEMALE_INT) {
                FEMALE_STRING
            } else {
                BOTH_STRING
            }
        }
    }
}