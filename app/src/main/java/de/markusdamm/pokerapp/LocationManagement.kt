package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import de.markusdamm.pokerapp.data.Location
import de.markusdamm.pokerapp.database.DatabaseHelper
import java.util.*


class LocationManagement : AppCompatActivity() {
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private lateinit var locations: ListView
    private lateinit var et: EditText
    private var locationList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_management)
        fillList()
        et = findViewById<View>(R.id.newLocation) as EditText
        addListenerOnListViewItemSelection()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 7) {
            fillList()
        }
    }

    private fun fillLocationList() {
        locationList.clear()
        val cursor = database.rawQuery("SELECT name FROM locations ORDER BY name ASC", null)
        while (cursor.moveToNext()) {
            val entry = cursor.getString(cursor.getColumnIndex("name"))
            locationList.add(entry)
        }
        cursor.close()
    }

    fun saveLocation(view: View) {
        val s = et.text.toString()
        if (s != "") {
            addLocationToDB(s)
        }
        et.setText("")
    }

    private fun fillList() {
        fillLocationList()
        locations = findViewById<View>(R.id.locations) as ListView
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        locations.adapter = listenAdapter
    }

    private fun addLocationToDB(location: String) {
        if (!locationList.contains(location)) {
            database.execSQL("INSERT INTO locations (name) VALUES('$location');'")
            fillList()
        } else {
            Toast.makeText(this, "Spieler existiert schon", Toast.LENGTH_LONG).show()
        }
    }

    private fun addListenerOnListViewItemSelection() {
        locations.isClickable = true
        locations.setOnItemClickListener { parent, _, position, _ ->
            val item = locations.getItemAtPosition(position) as String
            val location = Location(item)
            val intent = Intent(parent.context, SingleLocation::class.java)
            intent.putExtra("id", getIDForLocation(location))
            startActivityForResult(intent, 0)
        }
    }

    private fun getIDForLocation(location: Location): Int {
        val cursor = database.rawQuery("SELECT id FROM locations WHERE name = '${location.name}';", null)
        cursor.moveToLast()
        val entry = cursor.getInt(cursor.getColumnIndex("id"))
        cursor.close()
        return entry
    }
}