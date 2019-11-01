package de.markusdamm.pokerapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Markus Damm on 28.03.2015.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        private var databaseInternal : SQLiteDatabase? = null
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "pokerDB"

        fun initDatabase(db: SQLiteDatabase) {
            databaseInternal = db
        }

        fun getDatabase() : SQLiteDatabase {
            return databaseInternal ?: error("Database not found")
        }

        fun getLastInt(query: String) : Int {
            val cursor = databaseInternal!!.rawQuery(query, null)
            cursor.moveToLast()
            val ret = cursor.getInt(0)
            cursor.close()
            return ret
        }

        fun getLastDouble(query: String) : Double {
            val cursor = databaseInternal!!.rawQuery(query, null)
            cursor.moveToLast()
            val ret = cursor.getDouble(0)
            cursor.close()
            return ret
        }
    }
}