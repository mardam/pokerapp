package de.markusdamm.pokerapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import de.markusdamm.pokerapp.data.Record
import de.markusdamm.pokerapp.database.DatabaseHelper
import java.util.*

class Records : AppCompatActivity() {
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()
    private var records: ArrayList<String>? = null
    private var entriesLV: ListView? = null
    private var selection: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        entriesLV = findViewById<View>(R.id.entries) as ListView
        selection = findViewById<View>(R.id.type) as Spinner

        records = requestRecords("Längster Abend")

        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Record.possibleRecords)
        selection!!.adapter = listenAdapter

        selection!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                records = requestRecords(Record.possibleRecords[position])
                fillList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // sometimes you need nothing here
            }
        }
    }

    fun fillList() {
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records!!)
        entriesLV!!.adapter = listenAdapter
    }

    private fun requestRecords(kind: String): ArrayList<String> {
        val query = Record.getDBRequest(kind)
        val type = Record.getType(kind)
        var position = 1
        val ret = ArrayList<String>()
        val cursor = database.rawQuery(query, null)
        while (cursor.moveToNext()) {
            ret.add(parseRecordEntry(cursor, position, type))
            position++
        }
        cursor.close()
        return ret
    }

    private fun parseRecordEntry(cursor: Cursor, position: Int, type: String): String {
        var player: String? = null
        if (listOf(*cursor.columnNames).contains("player")) {
            player = cursor.getString(cursor.getColumnIndex("player"))
        }
        val evening = cursor.getString(cursor.getColumnIndex("name"))
        val value = cursor.getString(cursor.getColumnIndex("value"))
        val record = Record(position, evening, player, value, type)
        return record.toString()
    }
}