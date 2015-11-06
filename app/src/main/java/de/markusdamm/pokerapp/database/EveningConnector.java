package de.markusdamm.pokerapp.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Location;

/**
 * Created by Markus Damm on 28.03.2015.
 */



public class EveningConnector {
    private SQLiteOpenHelper databse;


    public EveningConnector(SQLiteOpenHelper db){
        this.databse = db;
    }

    public void insertNewEvening(Evening evening){

        SQLiteDatabase connection = databse.getWritableDatabase();


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sqlState = "INSERT INTO evenings (location, name, date) VALUES(" + Integer.toString(getIDForLocation(evening.getLocation(),connection)) + ", '" + evening.getName() + "', '" + df.format(evening.getDate()) + "');";

        connection.execSQL(sqlState);
        //Toast.makeText(this, sqlState, Toast.LENGTH_LONG).show();
        Cursor cursor  = connection.rawQuery("SELECT date FROM evenings", null);
        cursor.moveToLast();
        String entry = cursor.getString(cursor.getColumnIndex("date"));
        connection.close();
    }

    public int getIDForLocation(Location location, SQLiteDatabase connection){
        ArrayList<String> ret = new ArrayList<>();

        //Toast.makeText(this, location.getName(), Toast.LENGTH_LONG).show();

        Cursor cursor  = connection.rawQuery("SELECT id FROM locations WHERE name = '" + location.getName() + "';", null);
        cursor.moveToLast();
        return cursor.getInt(cursor.getColumnIndex("id"));
    }


}
