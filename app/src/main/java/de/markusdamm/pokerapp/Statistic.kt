package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import de.markusdamm.pokerapp.data.Gender
import de.markusdamm.pokerapp.data.Player
import de.markusdamm.pokerapp.data.PlayerStatistic
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.database.DatabaseHelper.Companion.getLastDouble
import de.markusdamm.pokerapp.database.DatabaseHelper.Companion.getLastInt
import de.markusdamm.pokerapp.utils.ArrayAdapterStatistic
import de.markusdamm.pokerapp.utils.DateFormats
import java.text.ParseException
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Statistic : AppCompatActivity() {

    private val pStatistics = ArrayList<PlayerStatistic>()
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var gender: Int = 0
    private var tv: TextView? = null
    private var choice1: String? = null
    private var choice2: String? = null
    private var choice3: String? = null
    private var location: String? = null


    val players: Set<Player>
        get() {
            val players = HashSet<Player>()
            val cursor = database.rawQuery("SELECT id, name, gender FROM players", null)
            while (cursor.moveToNext()) {
                val entry = cursor.getString(cursor.getColumnIndex("name"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val gender = cursor.getInt(2)
                val player = Player(entry, id, gender)
                players.add(player)
            }
            cursor.close()
            return players
        }


    private val locationStringForSqlQuery: String
        get() {
            if (location == "alle") {
                return ""
            }
            val id = idForLocation
            return " AND e.location = $id"
        }

    private val idForLocation: Int
        get() {
            val cursor = database.rawQuery("SELECT id FROM locations WHERE name = '$location';", null)
            cursor.moveToLast()
            val entry = cursor.getInt(cursor.getColumnIndex("id"))
            cursor.close()
            return entry
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        if (location == null) {
            location = "alle"
        }
        gender = Gender.BOTH_INT
        choice1 = PlayerStatistic.stWorsePlayer
        choice2 = PlayerStatistic.stMinuits
        choice3 = PlayerStatistic.stWins
        getPlayerStatistics()
        fillList()
        tv = findViewById<View>(R.id.tvValues) as TextView
        tv!!.text = "$choice1 | $choice2 | $choice3"
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_statistic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            val intent = Intent(this, StatisticOptions::class.java)
            intent.putExtra("gender", (gender + 1) % 3)
            intent.putExtra("choice1", choice1)
            intent.putExtra("choice2", choice2)
            intent.putExtra("choice3", choice3)
            intent.putExtra("location", location)
            startActivityForResult(intent, 0)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getPlayerStatistics() {
        val players = players
        pStatistics.clear()
        for (pl in players) {
            val ps = PlayerStatistic(pl)
            ps.bestPlace = getBestPlace(pl)
            ps.worstPlace = getWorstPlace(pl)
            ps.wins = getNumberOfTopPositions(pl, 1)
            ps.headUps = getNumberOfTopPositions(pl, 2)
            ps.podiums = getNumberOfTopPositions(pl, 3)
            ps.beatenPlayers = getBeatenPlayers(pl)
            ps.participations = getNumberOfParticipations(pl)
            ps.lastPlaces = getLastPlaces(pl)
            ps.minuits = getMinuits(pl)
            ps.participators = getParticipators(pl)
            ps.sumOfPlaces = getGetSumOfPlaces(pl)
            ps.multikills = getMultikills(pl)
            ps.average = getAverage(pl)
            ps.median = getMedian(pl)
            ps.sd = getSD(pl)
            ps.normalizedMean = getNormalizedMean(pl)
            pStatistics.add(ps)
        }
    }

    private fun getMinuits(pl: Player): Int {
        var minuits = 0
        val sqlState = "SELECT p.time, e.date FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE nr>0 AND loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
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


    fun getMultikills(pl: Player): Int {
        val sqlState = "SELECT count(*)\n" +
                "FROM (\n" +
                "SELECT pl.name as player, e.name as name, time, count(*) as value, count(DISTINCT p.winner) as winners\n" +
                "FROM places as p, evenings as e, players as pl\n" +
                "WHERE p.evening = e.id and e.name != 'Abend 1' and p.nr != 1 and pl.id = p.winner and pl.id = " + pl.id + locationStringForSqlQuery + "\n" +
                "group by time\n" +
                ") WHERE value > 1 and winners = 1\n" +
                "ORDER BY value DESC, player"

        val cursor = database.rawQuery(sqlState, null)
        cursor.moveToLast()
        val ret = cursor.getInt(0)
        cursor.close()
        return ret
    }

    private fun getGetSumOfPlaces(pl: Player): Int {
        val sqlState = "SELECT sum(p.nr) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr>0 AND p.loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }


    private fun getParticipators(pl: Player): Int {
        val sqlState = "SELECT count(p.id) FROM places p " +
                "Inner JOIN places p2 ON p.evening = p2.evening " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p2.loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }


    private fun getNumberOfParticipations(pl: Player): Int {
        val sqlState = "SELECT count(p.evening) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }

    private fun getBeatenPlayers(pl: Player): Int {
        val sqlState = "SELECT count(p.loser) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.winner = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }


    private fun getNumberOfTopPositions(pl: Player, worstPosition: Int): Int {
        val sqlState = "SELECT count(p.evening) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr <= " + worstPosition + " AND p.loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }

    private fun getBestPlace(pl: Player): Int {
        val sqlState = "SELECT min(p.nr) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }

    private fun getWorstPlace(pl: Player): Int {
        val sqlState = "SELECT max(p.nr) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.loser = " + pl.id +
                locationStringForSqlQuery +
                ";"
        return getLastInt(sqlState)
    }

    private fun getLastPlaces(pl: Player): Int {
        val sqlState = "SELECT count(*)\n" +
                "FROM (\n" +
                "SELECT p.evening as evening, p.nr as nr\n" +
                "FROM places p, players pl \n" +
                "WHERE pl.id = p.loser AND pl.id = " + pl.id +
                " intersect\n" +
                "SELECT evening, max(nr) as nr FROM places group by evening\n" +
                ");"
        return getLastInt(sqlState)
    }

    private fun getSD(pl: Player): Double {
        val sqlState = "with mean as (\n" +
                "SELECT avg(nr) AS Mean FROM places p JOIN evenings e ON e.id = p.evening " + locationStringForSqlQuery + " WHERE p.loser = " + pl.id + ")\n" +
                "SELECT avg((p1.nr-mean.mean)*(p1.nr-mean.mean)) as sd from places p1, mean\n" +
                "JOIN evenings e ON e.id = p1.evening " + locationStringForSqlQuery + "\n" +
                "WHERE p1.loser = " + pl.id
        return sqrt(getLastDouble(sqlState))
    }

    private fun getAverage(pl: Player): Double {
        val sqlState = "SELECT avg(p.nr) FROM places p\n" +
                "JOIN evenings e ON p.loser = " + pl.id + " AND e.id = p.evening " +
                locationStringForSqlQuery
        return getLastDouble(sqlState)
    }

    private fun getMedian(pl: Player): Double {
        val sqlState = "with curr_table AS (\n" +
                "SELECT nr FROM places p\n" +
                "INNER JOIN evenings e ON p.evening = e.id " +
                locationStringForSqlQuery +
                " AND p.loser = " + pl.id + ")\n" +
                "\n" +
                "SELECT avg(nr)\n" +
                "FROM (SELECT nr\n" +
                "FROM curr_table\n" +
                "ORDER BY nr\n" +
                "LIMIT 2 - (SELECT COUNT(*) FROM curr_table) % 2    -- odd 1, even 2\\n\" +\n" +
                "OFFSET (SELECT (COUNT(*) - 1) / 2\n" +
                "FROM curr_table))"
        return getLastDouble(sqlState)
    }

    private fun getNormalizedMean(pl: Player): Double {
        val sqlState = "SELECT avg(1.0 * nr/max_val) as normalized from places p1\n" +
                "INNER JOIN (SELECT max(p.nr) as max_val, p.evening FROM places p JOIN evenings e ON\n" +
                "e.id = p.evening " +
                locationStringForSqlQuery +
                " GROUP BY evening) AS p2\n" +
                "ON p1.evening = p2.evening AND p1.loser = " + pl.id
        return getLastDouble(sqlState)
    }


    private fun fillList() {
        val lvStatistics = findViewById<View>(R.id.lvStatistics) as ListView
        val listenAdapter: ListAdapter
        if (gender == Gender.BOTH_INT) {
            listenAdapter = ArrayAdapterStatistic(this, pStatistics, choice1, choice2, choice3)
            lvStatistics.adapter = listenAdapter
        } else {
            val psList = ArrayList<PlayerStatistic>()
            for (ps in pStatistics) {
                if (ps.player?.genderAsInt == gender) {
                    psList.add(ps)
                }
            }
            listenAdapter = ArrayAdapterStatistic(this, psList, choice1, choice2, choice3)
            lvStatistics.adapter = listenAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            gender = Gender.getIntByString(data!!.getStringExtra("gender"))
            choice1 = data.getStringExtra("choice1")
            choice2 = data.getStringExtra("choice2")
            choice3 = data.getStringExtra("choice3")
            tv!!.text = "$choice1 | $choice2 | $choice3"
            location = data.getStringExtra("location")
            getPlayerStatistics()
            fillList()
        }
    }
}