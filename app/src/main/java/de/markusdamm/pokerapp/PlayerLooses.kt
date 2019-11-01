package de.markusdamm.pokerapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.utils.DateFormats
import de.markusdamm.pokerapp.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PlayerLooses : AppCompatActivity() {

    private val playerSet = HashSet<Player>()
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var sLoser: Spinner? = null
    private var sWinner: Spinner? = null
    private var eveningId: Int = 0
    private var number: Int = 0
    private var etTime: EditText? = null

    private val playerList: List<String>
        get() {
            return playerSet.asSequence().map { it.name }.toList()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_looses)

        val intent = intent
        eveningId = intent.getIntExtra("eveId", -1)
        number = intent.getIntExtra("place", -1)
        this.title = "Platz $number"
        if (number < 0) {
            throw NullPointerException()
        }

        fillPlayerSet()
        sLoser = findViewById<View>(R.id.sLooser) as Spinner
        sWinner = findViewById<View>(R.id.sWinner) as Spinner
        etTime = findViewById<View>(R.id.etTime) as EditText

        val date = Date()
        val df = SimpleDateFormat("dd.MM.yyyy HH:mm")
        etTime!!.setText(df.format(date))

        val loserList = playerList
        val listenAdapterLoser = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, loserList)
        sLoser!!.adapter = listenAdapterLoser

        val winnerList = playerList
        val listenAdapterWinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, winnerList)
        sWinner!!.adapter = listenAdapterWinner
    }


    private fun fillPlayerSet() {
        playerSet.clear()
        val cursor = database.rawQuery(
                "SELECT p2.id, p2.name, p2.gender " +
                        "FROM places p1 " +
                        "INNER JOIN players p2 ON p1.loser = p2.id " +
                        "WHERE p1.nr = -1 AND p1.evening = $eveningId;", null)
        while (cursor.moveToNext()) {
            val name = cursor.getString(1)
            val id = cursor.getInt(0)
            val gender = cursor.getInt(2)
            val player = Player(name, id, gender)
            playerSet.add(player)
        }
        cursor.close()
    }


    fun saveLosing(view: View) {
        addLoosingToDB()
    }

    private fun getPlayerByName(name: String): Player? {
        return Utils.getPlayerByName(playerSet, name)
    }

    private fun addLoosingToDB() {
        val df = DateFormats.getDataBaseFormat()
        val winner = getPlayerByName(sWinner!!.selectedItem as String)
        val loser = getPlayerByName(sLoser!!.selectedItem as String)

        val sdT = DateFormats.getGermanDayAndTime()
        var d = Date()
        val dateString = etTime!!.text.toString()
        try {
            d = sdT.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(this, "Datum falsch erkannt; $dateString", Toast.LENGTH_LONG).show()
        }

        if (loser == winner) {
            Toast.makeText(this, "Man kann nicht gegen sich selber verlieren", Toast.LENGTH_LONG).show()
        } else {
            var sqlState = "UPDATE places " +
                    "SET winner = " + winner!!.id + ", time = '" + df.format(d) + "', nr = " + number +
                    " WHERE loser = " + loser!!.id + " AND evening = " + eveningId + ";"

            database.execSQL(sqlState)
            if (number == 2) {
                sqlState = "UPDATE places " +
                        "SET time = '" + df.format(d) + "', nr = 1 " +
                        " WHERE loser = " + winner.id + " AND evening = " + eveningId + ";"
                database.execSQL(sqlState)
            }
            Toast.makeText(this, "Ausscheiden von " + loser.name + " an " + winner.name + " hinzugefügt", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
