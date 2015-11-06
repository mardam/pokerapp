package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.utils.DateFormats;
import de.markusdamm.pokerapp.utils.Utils;


public class PlayerLooses extends ActionBarActivity {

    private Set <Player> playerSet = new HashSet<>();
    private SQLiteDatabase database;
    private Spinner sLoser, sWinner;
    private int eveningId, number;
    private EditText etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_looses);

        Intent intent = getIntent();
        eveningId = intent.getIntExtra("eveId",-1);
        number = intent.getIntExtra("place",-1);
        this.setTitle("Platz " + Integer.toString(number));
        if (number<0){
            throw new NullPointerException();
        }

        fillPlayerSet();
        sLoser = (Spinner)findViewById(R.id.sLooser);
        sWinner = (Spinner)findViewById(R.id.sWinner);
        etTime = (EditText)findViewById(R.id.etTime);

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        etTime.setText(df.format(date));

        ArrayList<String> loserList = getPlayerList();
        ArrayAdapter listenAdapterLoser = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, loserList);
        sLoser.setAdapter(listenAdapterLoser);

        ArrayList<String> winnerList = getPlayerList();
        ArrayAdapter listenAdapterWinner = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, winnerList);
        sWinner.setAdapter(listenAdapterWinner);
    }


    public void fillPlayerSet(){
        playerSet.clear();
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        Cursor cursor  = database.rawQuery(
                "SELECT p2.id, p2.name, p2.gender " +
                        "FROM places p1 " +
                        "INNER JOIN players p2 ON p1.loser = p2.id " +
                        "WHERE p1.nr = -1 AND p1.evening = " + Integer.toString(eveningId) + ";", null);
        while(cursor.moveToNext()){
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            int gender = cursor.getInt(2);
            Player player = new Player(name,id,gender);
            playerSet.add(player);
        }
        cursor.close();
        database.close();
    }

    public ArrayList<String> getPlayerList(){
        ArrayList<String> playerList = new ArrayList<>();
        for (Player pl: playerSet){
            playerList.add(pl.getName());
        }
        return playerList;
    }


    public void saveLosing(View view){
        addLoosingToDB();
    }

    public Player getPlayerByName(String name){
        return Utils.getPlayerFromPlayerSetByName(playerSet,name);
    }

    public void addLoosingToDB(){
        SimpleDateFormat df = DateFormats.getDataBaseFormat();
        Player winner = getPlayerByName((String)sWinner.getSelectedItem());
        Player loser = getPlayerByName((String)sLoser.getSelectedItem());

        SimpleDateFormat sdT = DateFormats.getGermanDayAndTime();
        Date d = new Date();
        String dateString = etTime.getText().toString();
        try {
            d = sdT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this,"Datum falsch erkannt; " + dateString,Toast.LENGTH_LONG).show();
        }

        if (loser == winner){
            Toast.makeText(this,"Man kann nicht gegen sich selber verlieren",Toast.LENGTH_LONG).show();
        }
        else {
            String sqlState = "UPDATE places " +
                    "SET winner = " + winner.getId() + ", time = '" + df.format(d) + "', nr = " + number +
                    " WHERE loser = " + loser.getId() + " AND evening = " + eveningId + ";";



            database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
            database.execSQL(sqlState);
            if (number == 2){
                sqlState = "UPDATE places " +
                        "SET time = '" + df.format(d) + "', nr = 1 "+
                        " WHERE loser = " + winner.getId() + " AND evening = " + eveningId + ";";
                database.execSQL(sqlState);
            }
            database.close();
            Toast.makeText(this, "Ausscheiden von " + loser.getName() + " an " + winner.getName() + " hinzugefügt", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
