package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Gender;
import de.markusdamm.pokerapp.data.Location;
import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.data.PlayerStatistic;

public class SingleLocation extends ActionBarActivity {

    private SQLiteDatabase database;
    private EditText etName;
    private Location location;
    private int locationId;
    private ListView evenings;
    private ArrayList<String> eveningList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_location);

        Intent intent = getIntent();
        this.locationId = intent.getIntExtra("id", -1);
        setLocation();
        etName = (EditText)findViewById(R.id.etName);
        etName.setText(location.getName());
        this.setTitle(location.getName());
        evenings = (ListView)findViewById((R.id.lvEvenings));
        fillListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_single_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            if (eveningList.isEmpty()){
                String sqlState = "DELETE FROM locations " +
                        "WHERE id = " + locationId;
                database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
                database.execSQL(sqlState);
                database.close();
                Toast.makeText(this, location.getName() + " gelöscht.",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(7, intent);
                finish();
            } else {
                Toast.makeText(this, location.getName() +
                                " war bereits Austragungsort für Abende und kann deshalb nicht gelöscht werden.",
                        Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setLocation(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);


        Cursor cursor  = database.rawQuery("SELECT id, name FROM locations WHERE id = " + locationId, null);
        cursor.moveToLast();
        String name = cursor.getString(cursor.getColumnIndex("name"));
        location = new Location(name);
        cursor.close();
        database.close();
    }

    public void saveChanges(View view){
        Location newLoc = new Location(etName.getText().toString());
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        String sqlState = "SELECT count(name) FROM locations WHERE name = '" + newLoc.getName() + "';";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        if (cursor.getInt(0) == 0){
            sqlState = "UPDATE locations " +
                    "SET name = '" + newLoc.getName() + "' " +
                    "WHERE id = " + locationId;
            database.execSQL(sqlState);
            Toast.makeText(this, "Ort gespeichert", Toast.LENGTH_LONG).show();
            cursor.close();
            database.close();
            Intent intent = new Intent();
            setResult(7,intent);
            finish();
        }
        else{
            Toast.makeText(this,"Ort existiert bereits",Toast.LENGTH_LONG).show();
        }
        cursor.close();
        database.close();


    }

    public ArrayList<String> getEvenings(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<String> ret = new ArrayList<>();

        Cursor cursor  = database.rawQuery("SELECT name FROM evenings " +
                "WHERE location = " + locationId
                , null);
        while(cursor.moveToNext()){
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            ret.add(entry);
        }
        cursor.close();
        database.close();
        return ret;
    }

    public void fillListView(){
        eveningList = getEvenings();
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eveningList);
        evenings.setAdapter(listenAdapter);
    }

}
