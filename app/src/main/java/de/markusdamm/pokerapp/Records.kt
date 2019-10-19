package de.markusdamm.pokerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import de.markusdamm.pokerapp.data.Record;

public class Records extends ActionBarActivity {
    private SQLiteDatabase database;
    private ArrayList<String> records;
    private ListView entriesLV;
    private Spinner selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        entriesLV = (ListView) findViewById(R.id.entries);
        selection = (Spinner) findViewById(R.id.type);

        records = requestRecords("Längster Abend");

        final ArrayAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Record.getPossibleRecords());
        selection.setAdapter(listenAdapter);

        selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                records = requestRecords(Record.getPossibleRecords().get(position));
                fillList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
    }

    public void fillList(){
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, records);
        entriesLV.setAdapter(listenAdapter);
    }

    private ArrayList<String> requestRecords(String kind) {
        String query = Record.getDBRequest(kind);
        String type = Record.getType(kind);
        int position = 1;
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
        ArrayList<String> ret = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ret.add(parseRecordEntry(cursor, position, type));
            position++;
        }
        cursor.close();
        database.close();
        return ret;
    }

    private String parseRecordEntry(Cursor cursor, int position, String type) {
        String player = null;
        if (Arrays.asList(cursor.getColumnNames()).contains("player")) {
            player = cursor.getString(cursor.getColumnIndex("player"));
        }
        String evening = cursor.getString(cursor.getColumnIndex("name"));
        String value = cursor.getString(cursor.getColumnIndex("value"));
        Record record = new Record(position, evening, player, value, type);
        return record.toString();
    }

}
