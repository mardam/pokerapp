package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();
        //enterData();

    }



    public void managePlayers(View view){
        Intent intent = new Intent(this,PlayerManagement.class);
        startActivity(intent);
    }

    public void managePlaces(View view){
        Intent intent = new Intent(this, LocationManagement.class);
        startActivity(intent);
    }

    public void manageSocieties(View view){
        Intent intent = new Intent(this, SocietyManagement.class);
        startActivity(intent);
    }

    public void manageEvenings(View view){
        Intent intent = new Intent(this, EveningManagement.class);
        startActivity(intent);
    }

    public void showStatistic(View view){
        Intent intent = new Intent(this,Statistic.class);
        startActivity(intent);
    }

    public void createDatabase(){
        SQLiteDatabase database;
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        for (String sql: getResources().getStringArray(R.array.create)){
            database.execSQL(sql);
        }
        database.close();

    }

    public void enterData(){
        SQLiteDatabase database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        for (String sql: getResources().getStringArray(R.array.dropEverything)){
            database.execSQL(sql);
        }
        createDatabase();
        for (String sql:getResources().getStringArray(R.array.oldEvenings)){
            database.execSQL(sql);
        }
        database.close();
    }




}
