package de.markusdamm.pokerapp.data


class Player(val name: String, var id: Int, gender: Int) : Comparable<Player> {


    var gender: Boolean = false
    var isSelected: Boolean = false
    var isSelectable: Boolean = false

    val genderAsInt: Int
        get() = Gender.toInt(gender)

    init {
        this.isSelected = false
        this.isSelectable = true
        this.gender = Gender.toBool(gender)
    }

    fun toggleSelected() {
        isSelected = !isSelected
    }

    override fun toString(): String {
        return name
    }

    override fun compareTo(other: Player): Int {
        return this.name.compareTo(other.name)
    }
}