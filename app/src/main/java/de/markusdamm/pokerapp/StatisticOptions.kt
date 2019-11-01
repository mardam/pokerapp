package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import de.markusdamm.pokerapp.data.Gender
import de.markusdamm.pokerapp.data.PlayerStatistic
import de.markusdamm.pokerapp.database.DatabaseHelper
import java.util.*


class StatisticOptions : AppCompatActivity() {

    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var spGender: Spinner? = null
    private var spChoice1: Spinner? = null
    private var spChoice2: Spinner? = null
    private var spChoice3: Spinner? = null
    private var spLocation: Spinner? = null
    private val locationList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic_options)
        spGender = findViewById<View>(R.id.spGender) as Spinner
        spChoice1 = findViewById<View>(R.id.spChoice1) as Spinner
        spChoice2 = findViewById<View>(R.id.spChoice2) as Spinner
        spChoice3 = findViewById<View>(R.id.spChoice3) as Spinner
        spLocation = findViewById<View>(R.id.spLocation) as Spinner
        fillLists()
        val intent = intent
        spGender!!.setSelection(intent.getIntExtra("gender", 0))
        spChoice1!!.setSelection(getPositionForString(intent.getStringExtra("choice1")))
        spChoice2!!.setSelection(getPositionForString(intent.getStringExtra("choice2")))
        spChoice3!!.setSelection(getPositionForString(intent.getStringExtra("choice3")))
        spLocation!!.setSelection(locationList.indexOf(intent.getStringExtra("location")))
    }

    private fun getPositionForString(string: String): Int {
        val ps = PlayerStatistic(null)
        val strings = ps.strings
        for (i in strings.indices) {
            if (string == strings[i]) {
                return i
            }
        }
        return 0
    }


    private fun fillLists() {
        val ps = PlayerStatistic(null)
        fillGender()
        fillLocations()
        val choiceList = ps.strings
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, choiceList)
        spChoice1!!.adapter = listenAdapter
        spChoice2!!.adapter = listenAdapter
        spChoice3!!.adapter = listenAdapter
    }

    private fun fillGender() {
        val genderList = ArrayList<String>()
        genderList.add(Gender.BOTH_STRING)
        genderList.add(Gender.MALE_STRING)
        genderList.add(Gender.FEMALE_STRING)
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        spGender!!.adapter = listenAdapter
    }

    private fun fillLocations() {
        locationList.add("alle")

        val cursor = database.rawQuery("SELECT name FROM locations ORDER BY name ASC", null)
        while (cursor.moveToNext()) {
            val entry = cursor.getString(cursor.getColumnIndex("name"))
            locationList.add(entry)
        }
        cursor.close()

        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationList)
        spLocation!!.adapter = listenAdapter
    }

    fun saveOptions(view: View) {
        val intent = Intent()
        intent.putExtra("gender", spGender!!.selectedItem as String)
        intent.putExtra("choice1", spChoice1!!.selectedItem as String)
        intent.putExtra("choice2", spChoice2!!.selectedItem as String)
        intent.putExtra("choice3", spChoice3!!.selectedItem as String)
        intent.putExtra("location", spLocation!!.selectedItem as String)

        setResult(1, intent)
        finish()
    }
}