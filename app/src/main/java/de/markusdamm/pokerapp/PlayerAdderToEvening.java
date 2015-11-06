package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.utils.ArrayAdapterPlayer;
import de.markusdamm.pokerapp.utils.Utils;


public class PlayerAdderToEvening extends ActionBarActivity {

    private SQLiteDatabase database;
    private ListView playerLV;
    private EditText et;
    private ArrayList<Player> playerList = new ArrayList<>();
    private Button btn;
    private int eveningID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_adder_to_evening);
        et = (EditText) findViewById(R.id.newPlayerName);
        Intent intent = getIntent();
        fillList();
        eveningID = intent.getIntExtra("eveId", -1);
        markUnselectablePlayers();
        addListenerOnListViewItemSelection();
    }


    public ArrayList<Player> getPlayers() {
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
        ArrayList<Player> ret = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT id, name, gender FROM players", null);
        while (cursor.moveToNext()) {
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Player player = Utils.getPlayerFromListById(playerList, id);
            int gender = cursor.getInt(2);
            if (player == null) {
                player = new Player(entry,id,gender);
            }
            ret.add(player);
        }
        cursor.close();
        database.close();
        return ret;
    }

    public void savePlayer(View view) {
        String s = et.getText().toString();
        if (!s.equals("")) {
            Player player = new Player(s,-1,0);
            addPlayerToDB(player);
        }
        else{
            Toast.makeText(this,"Bitte Namen eingeben",Toast.LENGTH_LONG).show();
        }
    }


    public void fillList() {
        playerList = getPlayers();
        markUnselectablePlayers();
        playerLV = (ListView) findViewById(R.id.players);
        ListAdapter listenAdapter = new ArrayAdapterPlayer(this, playerList);
        playerLV.setAdapter(listenAdapter);
    }


    public void addPlayerToDB(Player player) {

        Player pl = Utils.getPlayerFromList(playerList, player.getName());
        if (pl == null) {
            database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
            database.execSQL("INSERT INTO players (name, gender) VALUES('" + player.getName() + "', " + player.getGenderAsInt() + ");");
            database.close();
            fillList();
            Toast.makeText(this, player.getName() + " wurde gespeichert", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Spieler existiert schon", Toast.LENGTH_LONG).show();
        }

    }

    public void addListenerOnListViewItemSelection() {
        playerLV.setClickable(true);
        playerLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player item = (Player) playerLV.getItemAtPosition(position);
                item.toggleSelected();
                fillList();
                Toast.makeText(parent.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void addPlayersToEvening(View view) {
        int i = 0;
        for (Player pl : playerList) {
            if (pl.isSelected()) {
                addPlayerToEvening(pl);
                i++;
            }
        }
        Toast.makeText(this,i + " Spieler zum Abend hinzugefügt",Toast.LENGTH_LONG).show();
        finish();
    }

    public void addPlayerToEvening(Player player) {
        String sqlState = "INSERT INTO places (evening, loser, nr, winner) " +
                "VALUES(" + eveningID + ", " + Integer.toString(player.getId()) + ", -1, -1);";

        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
        database.execSQL(sqlState);
        //Toast.makeText(this, sqlState, Toast.LENGTH_LONG).show();
        Cursor cursor = database.rawQuery("SELECT name FROM evenings", null);
        cursor.moveToLast();
        String entry = cursor.getString(cursor.getColumnIndex("name"));
        this.setTitle(entry);
        cursor.close();
        database.close();
        //Toast.makeText(this, entry, Toast.LENGTH_LONG).show();

    }

    public void markUnselectablePlayers() {
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
        String sqlstate = "SELECT loser FROM places WHERE evening = " + eveningID + ";";
        Cursor cursor = database.rawQuery(sqlstate, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            Player pl = Utils.getPlayerFromListById(playerList, id);
            pl.setSelectable(false);
        }
        cursor.close();
        database.close();

    }
}
