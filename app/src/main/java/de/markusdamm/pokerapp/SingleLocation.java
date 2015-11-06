package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.Switch;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Gender;
import de.markusdamm.pokerapp.data.Location;
import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.data.PlayerStatistic;

public class SingleLocation extends ActionBarActivity {

    private SQLiteDatabase database;
    private EditText etName;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_location);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        setLocation(id);
        etName = (EditText)findViewById(R.id.etName);
        etName.setText(location.getName());
        this.setTitle(location.getName());
        //fillStatistic();
        //fillListView();

    }

    public void setLocation(int id){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);


        Cursor cursor  = database.rawQuery("SELECT id, name FROM locations WHERE id = " + id, null);
        cursor.moveToLast();
        String name = cursor.getString(cursor.getColumnIndex("name"));
        location = new Location(name);
        cursor.close();
        database.close();
    }

}
