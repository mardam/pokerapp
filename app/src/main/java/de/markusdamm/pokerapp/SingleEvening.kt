package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import de.markusdamm.pokerapp.data.Evening
import de.markusdamm.pokerapp.data.Location
import de.markusdamm.pokerapp.data.Placement
import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.utils.DateFormats
import de.markusdamm.pokerapp.utils.Utils
import java.text.ParseException
import java.util.*


class SingleEvening : AppCompatActivity() {

    private var evening: Evening? = null
    private var day: EditText? = null
    private var time: EditText? = null
    private var name: EditText? = null
    private var loc: EditText? = null
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var evName: String? = null
    private var lvPlayers: ListView? = null


    val players: ArrayList<String>
        get() {
            val sdf = DateFormats.getGermanTime()
            val ret = ArrayList<String>()
            for (pm in evening!!.placements) {
                if (pm.number <= 0) {
                    ret.add(pm.player.name)
                } else {
                    if (pm.winner == null || pm.number == 1) {
                        ret.add("${pm.number}: ${pm.player.name}")
                    } else {
                        if (pm.number > 1) {
                            ret.add("${pm.number}: ${pm.player.name} an ${pm.winner!!.name} um ${sdf.format(pm.date)}")
                        }
                    }
                }
            }
            return ret
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_evening)
        val intent = intent
        evName = intent.getStringExtra("eveningName")
        this.title = evName
        evening = getEvening(evName)
        name = findViewById<View>(R.id.eveningName) as EditText
        day = findViewById<View>(R.id.eveningDate) as EditText
        time = findViewById<View>(R.id.eveningTime) as EditText
        loc = findViewById<View>(R.id.eveningLocation) as EditText
        name!!.setText(evening!!.name)
        day!!.setText(DateFormats.getGermanDay().format(evening!!.date))
        time!!.setText(DateFormats.getGermanTime().format(evening!!.date))
        loc!!.setText(evening!!.location!!.name)
        lvPlayers = findViewById<View>(R.id.lvPlayers) as ListView
        //fillList();
    }

    override fun onResume() {
        super.onResume()
        evening = getEvening(evName)
        getPlaces()
        fillList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_single_evening, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_add) {
            if (evening!!.isStarted) {
                Toast.makeText(this, "Abend hat bereits angefangen", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, PlayerAdderToEvening::class.java)
                intent.putExtra("eveId", evening!!.id)
                startActivity(intent)
            }
            return true
        }

        if (id == R.id.action_loose) {
            if (evening!!.isFinished) {
                Toast.makeText(this, "Abend bereits beendet", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, PlayerLooses::class.java)
                intent.putExtra("eveId", evening!!.id)
                intent.putExtra("place", evening!!.worstPlaceForUnsetPlayer)
                startActivity(intent)
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    private fun getPlaces() {
        val cursor = database.rawQuery("SELECT p1.nr, p2.name, p2.id, p1.winner, p1.time, p2.gender " +
                "FROM places p1 " +
                "INNER JOIN players p2 ON p1.loser = p2.id " +
                "WHERE p1.evening = ${evening!!.id} " +
                "ORDER BY p1.nr ASC;", null)
        while (cursor.moveToNext()) {
            val place = cursor.getInt(0)
            val plName = cursor.getString(1)
            val plId = cursor.getInt(2)


            var loser = Utils.getPlayerById(evening!!.players, plId)
            if (loser == null) {
                loser = Player(plName, plId, cursor.getInt(5))
            }
            val pm = Placement(place, loser)

            val df = DateFormats.getDataBaseFormat()

            val stDate = cursor.getString(4)
            var date = Date()
            try {
                date = df.parse(stDate)
            } catch (e: ParseException) {
                Toast.makeText(this, "Datum falsch erkannt $stDate", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

            pm.date = date
            val wId = cursor.getInt(3)
            if (place > 1 && wId > 0) {
                var winner = Utils.getPlayerById(evening!!.players, wId)
                if (winner == null) {
                    winner = getPlayerByID(wId)
                }
                pm.winner = winner
            }
            evening!!.enterPlacement(pm)
        }
        cursor.close()
    }

    private fun getPlayerByID(id: Int): Player {
        val sqlstate = "SELECT name, gender FROM players WHERE name = $id;"
        val cursor = database.rawQuery(sqlstate, null)
        cursor.moveToLast()
        val name = cursor.getString(0)
        val gender = cursor.getInt(1)
        val pl = Player(name, id, gender)
        cursor.close()
        return pl
    }


    private fun getEvening(name: String?): Evening {
        val cursor = database.rawQuery(
                "SELECT e.id, e.date, l.name " +
                        "FROM evenings e " +
                        "INNER JOIN locations l ON l.id = e.location " +
                        "WHERE e.name = '$name';", null)
        cursor.moveToLast()

        val df = DateFormats.getDataBaseFormat()

        val stDate = cursor.getString(1)
        var date = Date()
        try {
            date = df.parse(stDate)
        } catch (e: ParseException) {
            Toast.makeText(this, "Datum falsch erkannt $stDate", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

        val id = cursor.getInt(0)
        val loc = cursor.getString(2)
        val location = Location(loc)
        cursor.close()
        val evening = Evening(location, date, name!!)
        evening.id = id
        return evening
    }

    private fun fillList() {
        val playerList = players
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playerList)
        lvPlayers!!.adapter = listenAdapter
    }
}