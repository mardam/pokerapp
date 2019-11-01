package de.markusdamm.pokerapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.utils.ArrayAdapterPlayer
import de.markusdamm.pokerapp.utils.Utils
import java.util.*


class PlayerAdderToEvening : AppCompatActivity() {

    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private lateinit var playerLV: ListView
    private lateinit var et: EditText
    private var playerList = ArrayList<Player>()
    private val btn: Button? = null
    private var eveningID: Int = 0


    val players: ArrayList<Player>
        get() {
            val ret = ArrayList<Player>()

            val cursor = database.rawQuery("SELECT id, name, gender FROM players", null)
            while (cursor.moveToNext()) {
                val entry = cursor.getString(cursor.getColumnIndex("name"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                var player = Utils.getPlayerById(playerList, id)
                val gender = cursor.getInt(2)
                if (player == null) {
                    player = Player(entry, id, gender)
                }
                ret.add(player)
            }
            cursor.close()
            return ret
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_adder_to_evening)
        et = findViewById<EditText>(R.id.newPlayerName)
        val intent = intent
        fillList()
        eveningID = intent.getIntExtra("eveId", -1)
        markUnselectablePlayers()
        addListenerOnListViewItemSelection()
    }

    fun savePlayer(view: View) {
        val s = et.text.toString()
        if (s != "") {
            val player = Player(s, -1, 0)
            addPlayerToDB(player)
        } else {
            Toast.makeText(this, "Bitte Namen eingeben", Toast.LENGTH_LONG).show()
        }
    }

    private fun fillList() {
        playerList = players
        markUnselectablePlayers()
        playerLV = findViewById<ListView>(R.id.players)
        val listenAdapter = ArrayAdapterPlayer(this, playerList)
        playerLV.adapter = listenAdapter
    }

    private fun addPlayerToDB(player: Player) {
        val pl = Utils.getPlayerByName(playerList, player.name)
        if (pl == null) {
            database.execSQL("INSERT INTO players (name, gender) VALUES('" + player.name + "', " + player.genderAsInt + ");")
            fillList()
            Toast.makeText(this, player.name + " wurde gespeichert", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Spieler existiert schon", Toast.LENGTH_LONG).show()
        }
    }

    private fun addListenerOnListViewItemSelection() {
        playerLV.isClickable = true
        playerLV.setOnItemClickListener  { parent, _, position, _ ->
            val item = playerLV.getItemAtPosition(position) as Player
            item.toggleSelected()
            fillList()
            Toast.makeText(parent.context, position.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun addPlayersToEvening(view: View) {
        var i = 0
        playerList.asSequence()
                .filter { it.isSelected }
                .forEach { addPlayerToEvening(it); i++ }
        Toast.makeText(this, "$i Spieler zum Abend hinzugefügt", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun addPlayerToEvening(player: Player) {
        val sqlState = "INSERT INTO places (evening, loser, nr, winner) " +
                "VALUES($eveningID,${player.id}, -1, -1);"
        database.execSQL(sqlState)
        val cursor = database.rawQuery("SELECT name FROM evenings", null)
        cursor.moveToLast()
        val entry = cursor.getString(cursor.getColumnIndex("name"))
        this.title = entry
        cursor.close()
    }

    private fun markUnselectablePlayers() {
        val sqlstate = "SELECT loser FROM places WHERE evening = $eveningID;"
        val cursor = database.rawQuery(sqlstate, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            Utils.getPlayerById(playerList, id)?.let {
                it.isSelectable = false
            }
        }
        cursor.close()
    }
}