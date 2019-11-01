package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import de.markusdamm.pokerapp.data.Gender
import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.data.PlayerStatistic
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.database.DatabaseHelper.Companion.getLastDouble
import de.markusdamm.pokerapp.database.DatabaseHelper.Companion.getLastInt
import de.markusdamm.pokerapp.utils.DateFormats
import java.text.ParseException
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt


class SinglePlayerOverview : AppCompatActivity() {

    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var ps: PlayerStatistic? = null
    private var sGender: Switch? = null
    private var etName: EditText? = null

    private val minuits: Int
        get() {
            var minuits = 0
            val sqlState = "SELECT p.time, e.date FROM places p " +
                    "INNER JOIN evenings e ON e.id = p.evening " +
                    "WHERE nr>0 AND loser = " + ps!!.player!!.id + ";"
            val cursor = database.rawQuery(sqlState, null)
            while (cursor.moveToNext()) {
                val df = DateFormats.getDataBaseFormat()
                val s1 = cursor.getString(0)
                val s2 = cursor.getString(1)
                var d1 = Date()
                var d2 = Date()
                try {
                    d1 = df.parse(s1)
                    d2 = df.parse(s2)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                val diff = ((d1.time - d2.time) / 60000).toFloat().roundToInt()
                minuits += diff

            }
            cursor.close()
            return minuits
        }


    private val getSumOfPlaces: Int
        get() {
            val sqlState = "SELECT sum(nr) FROM places " +
                    "WHERE nr>0 AND loser = " + ps!!.player!!.id + ";"
            return getLastInt(sqlState)
        }


    private val participators: Int
        get() {
            val sqlState = "SELECT count(p1.id) FROM places p1 " +
                    "Inner JOIN places p2 ON p1.evening = p2.evening " +
                    "WHERE p2.loser = " + ps!!.player!!.id + ";"
            return getLastInt(sqlState)
        }


    private val numberOfParticipations: Int
        get() {
            val sqlState = "SELECT count(evening) FROM places WHERE nr > 0 AND loser = " + ps!!.player!!.id + ";"
            return getLastInt(sqlState)
        }

    private val beatenPlayers: Int
        get() {
            val sqlState = "SELECT count(loser) FROM places WHERE nr > 0 AND winner = " + ps!!.player!!.id + ";"
            return getLastInt(sqlState)
        }

    private val bestPlace: Int
        get() {
            val sqlState = "SELECT min(nr) FROM places WHERE nr > 0 AND loser = " + ps!!.player!!.id + ";"
            return getLastInt(sqlState)
        }

    private val worstPlace: Int
        get() {
            val sqlState = "SELECT max(nr) FROM places WHERE nr > 0 AND loser = " + ps!!.player!!.id + ";"
            return getLastInt(sqlState)
        }

    private val numberOfLastPlaces: Int
        get() {
            val sqlState = "SELECT count(*)\n" +
                    "FROM (\n" +
                    "SELECT p.evening as evening, p.nr as nr\n" +
                    "FROM places p, players pl \n" +
                    "WHERE pl.id = p.loser AND  pl.id = " + ps!!.player!!.id +
                    " intersect\n" +
                    "SELECT evening, max(nr) as nr FROM places group by evening\n" +
                    ");"
            return getLastInt(sqlState)
        }

    private val multikills: Int
        get() {
            val sqlState = "SELECT count(*)\n" +
                    "FROM (\n" +
                    "SELECT pl.name as player, e.name as name, time, count(*) as value, count(DISTINCT p.winner) as winners\n" +
                    "FROM places as p, evenings as e, players as pl\n" +
                    "WHERE p.evening = e.id and e.name != 'Abend 1' and p.nr != 1 and pl.id = p.winner and pl.id = " + ps!!.player!!.id + "\n" +
                    "group by time\n" +
                    ") WHERE value > 1 and winners = 1\n" +
                    "ORDER BY value DESC, player"
            return getLastInt(sqlState)
        }

    private val mostKills: Pair<Int, List<String>>
        get() {
            val sqlState = "with y as (\n" +
                    "SELECT count(*) as value, pl.name as player\n" +
                    "FROM places p, players pl\n" +
                    "WHERE p.nr != 1 AND p.evening != 1 AND pl.id = p.loser and p.winner = " + ps!!.player!!.id + "\n" +
                    "GROUP BY winner, loser)\n" +
                    "\n" +
                    "SELECT * FROM y\n" +
                    "WHERE value = (SELECT max(value) FROM y)"
            val cursor = database.rawQuery(sqlState, null)

            val ret = ArrayList<String>()
            var value = -1

            while (cursor.moveToNext()) {
                val entry = cursor.getString(cursor.getColumnIndex("player"))
                value = cursor.getInt(cursor.getColumnIndex("value"))
                ret.add(entry)
            }
            cursor.close()
            return Pair(value, ret)
        }

    private val mostDeaths: Pair<Int, List<String>>
        get() {
            val sqlState = "with y as (\n" +
                    "SELECT count(*) as value, pl.name as player\n" +
                    "FROM places p, players pl\n" +
                    "WHERE p.nr != 1 AND p.evening != 1 AND pl.id = p.winner and p.loser = " + ps!!.player!!.id + "\n" +
                    "GROUP BY winner, loser)\n" +
                    "\n" +
                    "SELECT * FROM y\n" +
                    "WHERE value = (SELECT max(value) FROM y)"
            val cursor = database.rawQuery(sqlState, null)

            val ret = ArrayList<String>()
            var value = -1

            while (cursor.moveToNext()) {
                val entry = cursor.getString(cursor.getColumnIndex("player"))
                value = cursor.getInt(cursor.getColumnIndex("value"))
                ret.add(entry)
            }
            cursor.close()
            return Pair(value, ret)
        }

    private val sd: Double
        get() {
            val sqlState = "with mean as (\n" +
                    "SELECT avg(nr) AS Mean FROM places WHERE loser = " + ps!!.player!!.id + ")\n" +
                    "SELECT avg((nr-mean.mean)*(nr-mean.mean)) as sd from places, mean\n" +
                    "WHERE loser = " + ps!!.player!!.id

            return sqrt(getLastDouble(sqlState))
        }

    private val average: Double
        get() {
            val sqlState = "SELECT avg(places.nr) FROM places\n" +
                    "WHERE loser = " + ps!!.player!!.id
            return getLastDouble(sqlState)
        }

    private val median: Double
        get() {
            val sqlState = "with curr_table AS (\n" +
                    "SELECT nr FROM places WHERE loser = " + ps!!.player!!.id + ")\n" +
                    "\n" +
                    "SELECT avg(nr)\n" +
                    "FROM (SELECT nr\n" +
                    "      FROM curr_table\n" +
                    "      ORDER BY nr\n" +
                    "      LIMIT 2 - (SELECT COUNT(*) FROM curr_table) % 2    -- odd 1, even 2\n" +
                    "      OFFSET (SELECT (COUNT(*) - 1) / 2\n" +
                    "              FROM curr_table))"
            return getLastDouble(sqlState)
        }

    private val normalizedMean: Double
        get() {
            val sqlState = "SELECT avg(1.0 * nr/max_val) as normalized from places p1\n" +
                    "JOIN (SELECT max(nr) as max_val, evening FROM places GROUP BY evening) AS p2 \n" +
                    "ON p1.evening = p2.evening AND p1.loser = " + ps!!.player!!.id
            return getLastDouble(sqlState)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player_overview)
        val intent = intent
        val id = intent.getIntExtra("id", -1)
        val player = getPlayer(id)
        etName = findViewById<View>(R.id.etName) as EditText
        etName!!.setText(player.name)
        this.title = player.name
        ps = PlayerStatistic(player)
        fillStatistic()
        fillListView()
        sGender = findViewById<View>(R.id.sGender) as Switch
        sGender!!.isChecked = player.gender
        sGender!!.text = "Geschlecht: ${Gender.getString(player.gender)}"

        addSwitchListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_single_player_overview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_delete) {
            if (ps!!.participations > 0) {
                Toast.makeText(this, ps!!.player!!.name + " hat bereits an Abenden teilgenommen und kann deshalb nicht gelöscht werden.",
                        Toast.LENGTH_LONG).show()
            } else {
                val sqlState = "DELETE FROM players " +
                        "WHERE id = " + ps!!.player!!.id
                database.execSQL(sqlState)
                Toast.makeText(this, ps!!.player!!.name + " gelöscht.",
                        Toast.LENGTH_LONG).show()
                val intent = Intent()
                setResult(7, intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getPlayer(id: Int): Player {
        val cursor = database.rawQuery("SELECT id, name, gender FROM players WHERE id = $id", null)
        cursor.moveToLast()
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val gender = cursor.getInt(2)
        val player = Player(name, id, gender)
        cursor.close()
        return player
    }

    private fun fillListView() {
        val lvStatistics = findViewById<View>(R.id.lvStatistics) as ListView
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ps!!.statisticList)
        lvStatistics.adapter = listenAdapter
    }

    private fun fillStatistic() {
        ps!!.bestPlace = bestPlace
        ps!!.worstPlace = worstPlace
        ps!!.wins = getNumberOfTopPositions(1)
        ps!!.headUps = getNumberOfTopPositions(2)
        ps!!.podiums = getNumberOfTopPositions(3)
        ps!!.beatenPlayers = beatenPlayers
        ps!!.participations = numberOfParticipations
        ps!!.lastPlaces = numberOfLastPlaces
        ps!!.minuits = minuits
        ps!!.participators = participators
        ps!!.sumOfPlaces = getSumOfPlaces
        ps!!.multikills = multikills
        ps!!.setMostDeaths(mostDeaths)
        ps!!.setMostKills(mostKills)
        ps!!.sd = sd
        ps!!.median = median
        ps!!.normalizedMean = normalizedMean
        ps!!.average = average
    }


    fun getNumberOfTopPositions(worstPosition: Int): Int {
        val sqlState = "SELECT count(evening) FROM places WHERE nr <= " + worstPosition + " AND loser = " + ps!!.player!!.id + ";"
        return getLastInt(sqlState)
    }

    fun saveChanges(view: View) {
        val newPl = Player(etName!!.text.toString(), ps!!.player!!.id, 0)
        newPl.gender = sGender!!.isChecked
        var sqlState = "SELECT count(name) FROM players WHERE name = '" + newPl.name + "';"
        val cursor = database.rawQuery(sqlState, null)
        cursor.moveToLast()
        if (cursor.getInt(0) == 0 || newPl.name == ps!!.player!!.name) {
            sqlState = "UPDATE players " +
                    "SET name = '" + newPl.name + "', gender = " + newPl.genderAsInt + " " +
                    "WHERE id = " + ps!!.player!!.id
            database.execSQL(sqlState)
            Toast.makeText(this, "Spieler gespeichert", Toast.LENGTH_LONG).show()
            cursor.close()
            val intent = Intent()
            setResult(7, intent)
            finish()
        } else {
            Toast.makeText(this, "Spieler existiert bereits", Toast.LENGTH_LONG).show()
        }
        cursor.close()
    }

    private fun addSwitchListener() {
        sGender!!.setOnCheckedChangeListener { _, _ -> sGender!!.text = "Geschlecht: ${Gender.getString(sGender!!.isChecked)}"}
    }
}