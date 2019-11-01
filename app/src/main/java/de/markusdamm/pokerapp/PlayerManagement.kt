package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.utils.ArrayAdapterPlayer
import de.markusdamm.pokerapp.utils.Utils
import java.util.*

class PlayerManagement : AppCompatActivity() {
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var playerLV: ListView? = null
    private var et: EditText? = null
    private var playerList = ArrayList<Player>()


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
        setContentView(R.layout.activity_player_management)
        et = findViewById<View>(R.id.newPlayerName) as EditText
        fillList()
        addListenerOnListViewItemSelection()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 7) {
            playerList.clear()
            fillList()
        }
    }

    fun savePlayer(view: View) {
        val s = et!!.text.toString()
        if (s != "") {
            val player = Player(s, -1, 0)
            addPlayerToDB(player)
        } else {
            Toast.makeText(this, "Bitte Namen eingeben", Toast.LENGTH_LONG).show()
        }
    }

    private fun fillList() {
        playerList = players
        playerLV = findViewById<View>(R.id.players) as ListView
        val listenAdapter = ArrayAdapterPlayer(this, playerList)
        playerLV!!.adapter = listenAdapter
    }

    private fun addPlayerToDB(player: Player) {
        val pl = Utils.getPlayerByName(playerList, player.name)
        if (pl == null) {
            database.execSQL("INSERT INTO players (name, gender) VALUES('${player.name}', ${player.genderAsInt});")
            Toast.makeText(this, "${player.name} wurde gespeichert", Toast.LENGTH_LONG).show()
            fillList()
        } else {
            Toast.makeText(this, "Spieler existiert schon", Toast.LENGTH_LONG).show()
        }

    }

    private fun addListenerOnListViewItemSelection() {
        playerLV!!.isClickable = true
        playerLV!!.setOnItemClickListener { parent, _, position, _ ->
            val item = playerLV!!.getItemAtPosition(position) as Player
            val intent = Intent(parent.context, SinglePlayerOverview::class.java)
            intent.putExtra("id", item.id)
            startActivityForResult(intent, 0)
        }
    }
}