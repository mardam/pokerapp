package de.markusdamm.pokerapp


import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import de.markusdamm.pokerapp.data.Evening
import de.markusdamm.pokerapp.data.Location
import de.markusdamm.pokerapp.database.DatabaseHelper
import de.markusdamm.pokerapp.utils.DateFormats
import java.text.ParseException
import java.util.*


class NewEvening : AppCompatActivity() {

    private lateinit var dateEdit: EditText
    private lateinit var timeEdit: EditText
    private lateinit var locs: Spinner
    private var nameEdit: EditText? = null
    private var database: SQLiteDatabase = DatabaseHelper.getDatabase()

    val locations: ArrayList<String>
        get() {
            val ret = ArrayList<String>()

            val cursor = database.rawQuery("SELECT name FROM locations", null)
            while (cursor.moveToNext()) {
                val entry = cursor.getString(cursor.getColumnIndex("name"))
                ret.add(entry)
            }
            cursor.close()
            return ret
        }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_evening)

        dateEdit = findViewById<EditText>(R.id.date)
        timeEdit = findViewById<EditText>(R.id.time)

        val d = Date()
        val dfd = DateFormats.getGermanDay()
        val dft = DateFormats.getGermanTime()
        dateEdit.text = SpannableStringBuilder(dfd.format(d))
        timeEdit.text = SpannableStringBuilder(dft.format(d))

        val locationList = locations
        locs = findViewById<Spinner>(R.id.locations)
        val listenAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationList)
        locs.adapter = listenAdapter
    }


    fun addEvening(view: View) {
        val sdT = DateFormats.getGermanDayAndTime()
        var d = Date()

        val dateString = "${dateEdit.text} ${timeEdit.text}"
        try {
            d = sdT.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(this, "Datum falsch erkannt; $dateString", Toast.LENGTH_LONG).show()
        }

        nameEdit = findViewById<EditText>(R.id.name)
        val evening = Evening(Location(locs.selectedItem.toString()), d, nameEdit!!.text.toString())
        if (evening.name != "") {
            if (addEveningToDB(evening)) {
                finish()
            }
        } else {
            Toast.makeText(this, "Bitte Namen für den Abend eingeben", Toast.LENGTH_LONG).show()
        }
    }


    private fun getIDForLocation(location: Location): Int {
        val cursor = database.rawQuery("SELECT id FROM locations WHERE name = '${location.name}';", null)
        cursor.moveToLast()
        val entry = cursor.getInt(cursor.getColumnIndex("id"))
        cursor.close()

        return entry
    }

    private fun addEveningToDB(evening: Evening): Boolean {
        val df = DateFormats.getDataBaseFormat()

        val sqlState2 = "INSERT INTO evenings (location, name, date) " +
                "VALUES(${getIDForLocation(evening.location!!)}, '${evening.name}', '${df.format(evening.date)}');"


        val sqlState = "SELECT name FROM evenings;"

        val cursor = database.rawQuery(sqlState, null)

        while (cursor.moveToNext()) {
            if (cursor.getString(0) == evening.name) {
                Toast.makeText(this, "Abend mit diesem Namen existiert bereits", Toast.LENGTH_LONG).show()
                cursor.close()
                return false
            }
        }

        cursor.close()
        database.execSQL(sqlState2)
        return true
    }
}