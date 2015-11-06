package de.markusdamm.pokerapp;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Location;
import de.markusdamm.pokerapp.utils.DateFormats;


public class newEvening extends ActionBarActivity {

    private EditText dateEdit, timeEdit, nameEdit;
    private SQLiteDatabase database;
    private Spinner locs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_evening);


        dateEdit = (EditText)findViewById(R.id.date);
        //dateEdit.setText(Utils.getDate());

        timeEdit = (EditText)findViewById(R.id.time);
        Date d = new Date();
        SimpleDateFormat dfd = DateFormats.getGermanDay();
        SimpleDateFormat dft = DateFormats.getGermanTime();
        dateEdit.setText(dfd.format(d));
        timeEdit.setText(dft.format(d));

        ArrayList<String> locationList = getLocations();
        locs = (Spinner) findViewById(R.id.locations);
        ArrayAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationList);
        locs.setAdapter(listenAdapter);
        //dateEdit.setText();
    }




    public void addEvening(View view){
        SimpleDateFormat sdT = DateFormats.getGermanDayAndTime();
        Date d = new Date();

        String dateString = dateEdit.getText().toString() + " " + timeEdit.getText().toString();
        try {
            d = sdT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this,"Datum falsch erkannt; " + dateString,Toast.LENGTH_LONG).show();
        }
        nameEdit = (EditText)findViewById(R.id.name);
        Evening evening = new Evening(new Location(locs.getSelectedItem().toString()),d, nameEdit.getText().toString());
        if (!evening.getName().equals("")) {
            if (addEveningToDB(evening)){
                finish();
            }
        }
        else{
            Toast.makeText(this, "Bitte Namen für den Abend eingeben", Toast.LENGTH_LONG).show();
        }
    }


    public int getIDForLocation(Location location){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);

        Cursor cursor  = database.rawQuery("SELECT id FROM locations WHERE name = '" + location.getName() + "';", null);
        cursor.moveToLast();
        int entry = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();
        database.close();

        return entry;
    }

    public boolean addEveningToDB(Evening evening){
        SimpleDateFormat df = DateFormats.getDataBaseFormat();

        String sqlState2 = "INSERT INTO evenings (location, name, date) " +
                "VALUES(" + Integer.toString(getIDForLocation(evening.getLocation())) + ", '" + evening.getName() + "', '" + df.format(evening.getDate()) + "');";


        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
        String sqlState = "SELECT name FROM evenings;";

        Cursor cursor = database.rawQuery(sqlState,null);

        while(cursor.moveToNext()){
            if (cursor.getString(0).equals(evening.getName())){
                Toast.makeText(this, "Abend mit diesem Namen existiert bereits", Toast.LENGTH_LONG).show();
                cursor.close();
                database.close();
                return false;
            }
        }



        cursor.close();
        database.execSQL(sqlState2);

        database.close();
        return true;
    }

    public ArrayList<String> getLocations(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<String> ret = new ArrayList<>();

        Cursor cursor  = database.rawQuery("SELECT name FROM locations", null);
        while(cursor.moveToNext()){
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            ret.add(entry);
        }
        cursor.close();
        database.close();
        return ret;
    }

}
