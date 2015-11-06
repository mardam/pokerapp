package de.markusdamm.pokerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Location;
import de.markusdamm.pokerapp.data.Player;


public class LocationManagement extends ActionBarActivity {
    private SQLiteDatabase database;
    private ListView players;
    private Button btn;
    private EditText et;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_management);
        fillList();
        btn = (Button) findViewById(R.id.saveLocation);
        et = (EditText) findViewById(R.id.newLocation);
    }


    public ArrayList<String> getLocations(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<String> ret = new ArrayList<String>();

        Cursor cursor  = database.rawQuery("SELECT name FROM locations", null);
        while(cursor.moveToNext() == true){
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            ret.add(entry);
        }
        database.close();
        return ret;
    }

    public void saveLocation(View view){
        String s = et.getText().toString();
        if (!s.equals("")){
            Location location = new Location(s);
            addPlayerToDB(location);
        }
        et.setText("");
    }

    public void fillList(){
        ArrayList<String> locationList = getLocations();
        players = (ListView) findViewById(R.id.locations);
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList);
        players.setAdapter(listenAdapter);
    }

    public void addPlayerToDB(Location location){
        ArrayList<String> oldLocations = getLocations();
        if (!oldLocations.contains(location.getName())){
            database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
            database.execSQL("INSERT INTO locations (name) VALUES('" + location.getName() + "');'");
            database.close();
            fillList();
        }
        else{
            Toast.makeText(this,"Spieler existiert schon",Toast.LENGTH_LONG).show();
        }

    }
}
