package de.markusdamm.pokerapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner

import java.util.ArrayList

import de.markusdamm.pokerapp.data.Evening
import de.markusdamm.pokerapp.data.Player

class Participations : AppCompatActivity() {

    private lateinit var database: SQLiteDatabase
    private lateinit var participations: List<Participation>
    private lateinit var entriesLV: ListView
    private lateinit var selection: Spinner
    private lateinit var sorting: Spinner
    private lateinit var players: List<String>
    private lateinit var sortings: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participations)
        entriesLV = findViewById<ListView>(R.id.entries)
        selection = findViewById<Spinner>(R.id.type)
        sorting = findViewById<Spinner>(R.id.sorting)

        players = getPlayers()

        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, players)
        selection.adapter = listenAdapter

        sortings = listOf("Nach Datum", "Nach Platzierung")

        val listenAdapterSorting = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortings)
        sorting.adapter = listenAdapterSorting

        selection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                participations = getParticipations(players[position], sorting.selectedItem as String)
                fillList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                // sometimes you need nothing here
            }
        }


        sorting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                participations = getParticipations(selection.selectedItem as String, sortings[position])
                fillList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                // sometimes you need nothing here
            }
        }
    }

    fun fillList() {
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, participations)
        entriesLV.adapter = listenAdapter
    }

    private fun getPlayers(): ArrayList<String> {
        val ret = ArrayList<String>()

        val cursor = database.rawQuery("SELECT name FROM players ORDER BY name", null)
        while (cursor.moveToNext()) {
            ret.add(cursor.getString(cursor.getColumnIndex("name")))
        }
        cursor.close()
        return ret
    }

    fun getParticipations(playerName: String, sortingBy: String): List<Participation> {
        val ret = ArrayList<Participation>()

        var sortingString = ""

        if (sortingBy == "Nach Datum") {
            sortingString = " date"
        }

        if (sortingBy == "Nach Platzierung") {
            sortingString = " p.nr, e2.count desc, date"
        }

        if (sortingString == "") {
            throw IllegalArgumentException("sortingBy should be either \"Nach Datum\" or \"Nach Platzierung\". You actually should never end up here.")
        }

        val cursor = database.rawQuery("SELECT p.nr as position, e.name as evening, pl2.name as beatenby, e2.count as count FROM players pl1, places p, evenings e\n" +
                "INNER JOIN (SELECT count(*) as count, evening from places\n" +
                "GROUP BY evening) as e2 ON e.id = e2.evening\n" +
                "LEFT JOIN players pl2 ON pl2.id = p.winner\n" +
                "WHERE e.id = p.evening and p.loser = pl1.id AND pl1.name = \"$playerName\"\n" +
                "ORDER BY $sortingString", null)
        while (cursor.moveToNext()) {
            val eveningName = cursor.getString(cursor.getColumnIndex("evening"))
            val position = cursor.getInt(cursor.getColumnIndex("position"))
            val max = cursor.getInt(cursor.getColumnIndex("count"))
            val beatenBy = Player(cursor.getString(cursor.getColumnIndex("beatenby")), -1, -1)
            ret.add(Participation(Evening(null, null, eveningName), position, max, beatenBy))
        }
        cursor.close()
        return ret
    }
}
