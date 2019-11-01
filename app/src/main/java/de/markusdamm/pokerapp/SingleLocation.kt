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
import de.markusdamm.pokerapp.data.Location
import de.markusdamm.pokerapp.database.DatabaseHelper
import java.util.*

class SingleLocation : AppCompatActivity() {

    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var etName: EditText? = null
    private var location: Location? = null
    private var locationId: Int = 0
    private var evenings: ListView? = null
    private var eveningList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_location)

        val intent = intent
        this.locationId = intent.getIntExtra("id", -1)
        setLocation()
        etName = findViewById<View>(R.id.etName) as EditText
        etName!!.setText(location!!.name)
        this.title = location!!.name
        evenings = findViewById<View>(R.id.lvEvenings) as ListView
        fillListView()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_single_location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_delete) {
            if (eveningList.isEmpty()) {
                val sqlState = "DELETE FROM locations " +
                        "WHERE id = " + locationId
                database.execSQL(sqlState)
                Toast.makeText(this, location!!.name + " gelöscht.",
                        Toast.LENGTH_LONG).show()
                val intent = Intent()
                setResult(7, intent)
                finish()
            } else {
                Toast.makeText(this, location!!.name + " war bereits Austragungsort für Abende und kann deshalb nicht gelöscht werden.",
                        Toast.LENGTH_LONG).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setLocation() {
        val cursor = database.rawQuery("SELECT id, name FROM locations WHERE id = $locationId", null)
        cursor.moveToLast()
        val name = cursor.getString(cursor.getColumnIndex("name"))
        location = Location(name)
        cursor.close()
    }

    fun saveChanges(view: View) {
        val newLoc = Location(etName!!.text.toString())
        var sqlState = "SELECT count(name) FROM locations WHERE name = '${newLoc.name}';"
        val cursor = database.rawQuery(sqlState, null)
        cursor.moveToLast()
        if (cursor.getInt(0) == 0) {
            sqlState = "UPDATE locations " +
                    "SET name = '${newLoc.name}' " +
                    "WHERE id = $locationId"
            database.execSQL(sqlState)
            Toast.makeText(this, "Ort gespeichert", Toast.LENGTH_LONG).show()
            cursor.close()
            val intent = Intent()
            setResult(7, intent)
            finish()
        } else {
            Toast.makeText(this, "Ort existiert bereits", Toast.LENGTH_LONG).show()
        }
        cursor.close()
    }

    private fun getEvenings(): ArrayList<String> {
        val ret = ArrayList<String>()

        val cursor = database.rawQuery("SELECT name FROM evenings " +
                "WHERE location = $locationId", null)
        while (cursor.moveToNext()) {
            val entry = cursor.getString(cursor.getColumnIndex("name"))
            ret.add(entry)
        }
        cursor.close()
        return ret
    }

    private fun fillListView() {
        eveningList = getEvenings()
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eveningList)
        evenings!!.adapter = listenAdapter
    }
}