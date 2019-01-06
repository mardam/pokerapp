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

import java.util.ArrayList;
import java.util.List;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Player;

public class Participations extends ActionBarActivity {

    private SQLiteDatabase database;
    private List<Participation> participations;
    private ListView entriesLV;
    private Spinner selection;
    private List<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participations);
        entriesLV = (ListView) findViewById(R.id.entries);
        selection = (Spinner) findViewById(R.id.type);

        players = getPlayers();

        final ArrayAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, players);
        selection.setAdapter(listenAdapter);

        selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                participations = getParticipations(players.get(position));
                fillList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
    }

    public void fillList(){
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, participations);
        entriesLV.setAdapter(listenAdapter);
    }

    public ArrayList<String> getPlayers(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<String> ret = new ArrayList<>();

        Cursor cursor  = database.rawQuery("SELECT name FROM players ORDER BY name", null);
        while(cursor.moveToNext()){
            ret.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();
        database.close();
        return ret;
    }

    public List<Participation> getParticipations(String playerName) {
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<Participation> ret = new ArrayList<>();

        Cursor cursor  = database.rawQuery("SELECT p.nr as position, e.name as evening, pl2.name as beatenby, e2.count as count FROM players pl1, places p, evenings e\n" +
                "INNER JOIN (SELECT count(*) as count, evening from places\n" +
                "GROUP BY evening) as e2 ON e.id = e2.evening\n" +
                "LEFT JOIN players pl2 ON pl2.id = p.winner\n" +
                "WHERE e.id = p.evening and p.loser = pl1.id AND pl1.name = \"" +
                playerName +
                "\"\n" +
                "ORDER BY date", null);
        while(cursor.moveToNext()){
            String eveningName = cursor.getString(cursor.getColumnIndex("evening"));
            int position = cursor.getInt(cursor.getColumnIndex("position"));
            int max = cursor.getInt(cursor.getColumnIndex("count"));
            Player beatenBy = new Player(cursor.getString(cursor.getColumnIndex("beatenby")), -1,  -1);
            ret.add(new Participation(new Evening(null, null, eveningName), position, max, beatenBy));
        }
        cursor.close();
        database.close();
        return ret;
    }
}
