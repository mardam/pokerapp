package de.markusdamm.pokerapp

import android.Manifest
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.markusdamm.pokerapp.database.DatabaseHelper


class MainActivity : AppCompatActivity() {

    private var databaseHelper: DatabaseHelper? = null
    private var database : SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        setContentView(R.layout.activity_main)
        //DatabaseHelper.initDatabase()
        //database = DatabaseHelper.database
        //createDatabase()
        //enterData();
    }

    override fun onRequestPermissionsResult(requestCode : Int ,
                                            permissions: Array<String>,
                                            grantResults: IntArray){
        // Todo check fail
        databaseHelper = DatabaseHelper(this)
        DatabaseHelper.initDatabase(databaseHelper!!.writableDatabase)
        database = DatabaseHelper.getDatabase()
        createDatabase()
        enterData()
    }

    fun managePlayers(view: View) {
        val intent = Intent(this, PlayerManagement::class.java)
        startActivity(intent)
    }

    fun managePlaces(view: View) {
        val intent = Intent(this, LocationManagement::class.java)
        startActivity(intent)
    }

    fun manageSocieties(view: View) {
        val intent = Intent(this, SocietyManagement::class.java)
        startActivity(intent)
    }

    fun manageEvenings(view: View) {
        val intent = Intent(this, EveningManagement::class.java)
        startActivity(intent)
    }

    fun showStatistic(view: View) {
        val intent = Intent(this, Statistic::class.java)
        startActivity(intent)
    }

    fun showRecords(view: View) {
        val intent = Intent(this, Records::class.java)
        startActivity(intent)
    }

    private fun createDatabase() {
        for (sql in resources.getStringArray(R.array.create)) {
            database!!.execSQL(sql)
        }
    }

    fun enterData() {
        for (sql in resources.getStringArray(R.array.dropEverything)) {
            database!!.execSQL(sql)
        }
        createDatabase()
        for (sql in resources.getStringArray(R.array.oldEvenings)) {
            database!!.execSQL(sql)
        }
    }


    fun participations(view: View) {
        val intent = Intent(this, Participations::class.java)
        startActivity(intent)
    }

    fun showChips(view: View) {
        val intent = Intent(this, Chips::class.java)
        startActivity(intent)
    }
}
