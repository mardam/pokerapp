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

import java.util.ArrayList;

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

        records = new ArrayList<>();
        records.add("abc");
        records.add("def");

        final ArrayAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Record.getPossibleRecords());
        selection.setAdapter(listenAdapter);

        selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), Record.getPossibleRecords().get(position), Toast.LENGTH_SHORT).show();
                records.add(Record.getPossibleRecords().get(position));
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

}
