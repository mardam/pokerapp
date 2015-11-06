package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Location;


public class LocationManagement extends ActionBarActivity {
    private SQLiteDatabase database;
    private ListView locations;
    private EditText et;
    ArrayList<String> locationList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_management);
        fillList();
        et = (EditText) findViewById(R.id.newLocation);
        addListenerOnListViewItemSelection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 7){
            fillList();
        }
    }


    public void fillLocationList(){
        locationList.clear();
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);

        Cursor cursor  = database.rawQuery("SELECT name FROM locations ORDER BY name ASC", null);
        while(cursor.moveToNext()){
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            locationList.add(entry);
        }
        database.close();
    }

    public void saveLocation(View view){
        String s = et.getText().toString();
        if (!s.equals("")){
            addLocationToDB(s);
        }
        et.setText("");
    }

    public void fillList(){
        fillLocationList();
        locations = (ListView) findViewById(R.id.locations);
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList);
        locations.setAdapter(listenAdapter);
    }

    public void addLocationToDB(String location){
        if (!locationList.contains(location)){
            database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
            database.execSQL("INSERT INTO locations (name) VALUES('" + location + "');'");
            database.close();
            fillList();
        }
        else{
            Toast.makeText(this,"Spieler existiert schon",Toast.LENGTH_LONG).show();
        }

    }

    public void addListenerOnListViewItemSelection() {
        locations.setClickable(true);
        locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) locations.getItemAtPosition(position);
                Location location = new Location(item);
                Intent intent = new Intent(parent.getContext(), SingleLocation.class);
                intent.putExtra("id", getIDForLocation(location));
                startActivityForResult(intent, 0);
            }
        });
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
}
