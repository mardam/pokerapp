package de.markusdamm.pokerapp.database;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Markus Damm on 28.03.2015.
 */
public class DatabaseUtils{
  /*  private String databaseName = "pokerDB";
    private SQLiteDatabase database;

    public String getDatabaseName(){
        return databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDatabase(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, name VARCHAR);");
        database.execSQL("CREATE TABLE IF NOT EXISTS locations (id INTEGER PRIMARY KEY, name VARCHAR);");
        //database.execSQL("DROP TABLE IF EXISTS evenings;");
        database.execSQL("CREATE TABLE IF NOT EXISTS evenings (id INTEGER PRIMARY KEY, location INTEGER, name VARCHAR, date DATE);");
        //database.execSQL("CREATE TABLE IF NOT EXISTS participations (id INTEGER AUTO INCREMENT, player INTEGER, winner INTEGER, place INTEGER, evening INTEGER, time INTEGER);");


        //database.execSQL("INSERT INTO players (name) VALUES('" + "Markus" + "');'");
        //database.execSQL("INSERT INTO players (name) VALUES('Markus'),('Peter'),('Claus');'");
        //database.execSQL("DELETE FROM players WHERE NOT name = '1'");
        database.close();

    }*/


}
