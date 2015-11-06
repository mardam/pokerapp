package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class EveningManagement extends ActionBarActivity {

    private SQLiteDatabase database;
    private ListView evenings;


    @Override
    protected void onResume(){
        super.onResume();
        fillList();
        addListenerOnListViewItemSelection();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evening_management);
        evenings = (ListView) findViewById(R.id.evenings);
        fillList();
        addListenerOnListViewItemSelection();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evening_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new:
                newEvening();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<String> getEvenings(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<String> ret = new ArrayList<>();

        Cursor cursor  = database.rawQuery("SELECT name FROM evenings", null);
        while(cursor.moveToNext()){
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            ret.add(entry);
        }
        cursor.close();
        database.close();
        return ret;
    }

    public void fillList(){
        ArrayList<String> eveningList = getEvenings();
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eveningList);
        evenings.setAdapter(listenAdapter);
    }

    public void newEvening(){
        //Toast.makeText(this,"Neuer Abend", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, newEvening.class);
        startActivity(intent);
    }



    public void addListenerOnListViewItemSelection() {
        evenings.setClickable(true);
        evenings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = evenings.getItemAtPosition(position).toString();
                Intent intent = new Intent(view.getContext(), SingleEvening.class);
                intent.putExtra("eveningName", item);
                startActivity(intent);
            }
        });

    }

}
