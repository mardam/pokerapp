package de.markusdamm.pokerapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import de.markusdamm.pokerapp.database.DatabaseHelper
import java.util.*


class EveningManagement : AppCompatActivity() {

    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private lateinit var evenings: ListView

    override fun onResume() {
        super.onResume()
        fillList()
        addListenerOnListViewItemSelection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evening_management)
        evenings = findViewById<View>(R.id.evenings) as ListView
        fillList()
        addListenerOnListViewItemSelection()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_evening_management, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_new -> {
                newEvening()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getEvenings(): ArrayList<String> {
        val ret = ArrayList<String>()

        val cursor = database.rawQuery("SELECT name FROM evenings", null)
        while (cursor.moveToNext()) {
            val entry = cursor.getString(cursor.getColumnIndex("name"))
            ret.add(entry)
        }
        cursor.close()
        return ret
    }

    private fun fillList() {
        val eveningList = getEvenings()
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eveningList)
        evenings.adapter = listenAdapter
    }

    private fun newEvening() {
        val intent = Intent(this, NewEvening::class.java)
        startActivity(intent)
    }


    private fun addListenerOnListViewItemSelection() {
        evenings.isClickable = true
        evenings.setOnItemClickListener { _, view, position, _ ->
            val item = evenings.getItemAtPosition(position).toString()
            val intent = Intent(view.context, SingleEvening::class.java)
            intent.putExtra("eveningName", item)
            startActivity(intent)
        }

    }

}
