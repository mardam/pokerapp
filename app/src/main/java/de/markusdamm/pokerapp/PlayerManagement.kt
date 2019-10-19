package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.utils.ArrayAdapterPlayer;
import de.markusdamm.pokerapp.utils.Utils;


public class PlayerManagement extends ActionBarActivity {
    private SQLiteDatabase database;
    private ListView playerLV;
    private EditText et;
    private ArrayList<Player> playerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_management);
        et = (EditText) findViewById(R.id.newPlayerName);
        fillList();
        addListenerOnListViewItemSelection();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 7){
            playerList.clear();
            fillList();
        }
    }


    public ArrayList<Player> getPlayers(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<Player> ret = new ArrayList<>();

        Cursor cursor  = database.rawQuery("SELECT id, name, gender FROM players", null);
        while(cursor.moveToNext()){
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

    public void savePlayer(View view){
        String s = et.getText().toString();
        if (!s.equals("")){
            Player player = new Player(s,-1,0);
            addPlayerToDB(player);
        }
        else{
            Toast.makeText(this,"Bitte Namen eingeben",Toast.LENGTH_LONG).show();
        }
    }


    public void fillList(){
        playerList = getPlayers();
        playerLV = (ListView) findViewById(R.id.players);
        ListAdapter listenAdapter = new ArrayAdapterPlayer(this, playerList);
        playerLV.setAdapter(listenAdapter);
    }


    public void addPlayerToDB(Player player){

        Player pl = Utils.getPlayerFromList(playerList,player.getName());
        if (pl == null){
            database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
            database.execSQL("INSERT INTO players (name, gender) VALUES('" + player.getName() + "', " + player.getGenderAsInt() + ");");
            database.close();
            Toast.makeText(this, player.getName() + " wurde gespeichert", Toast.LENGTH_LONG).show();
            fillList();
        }
        else{
            Toast.makeText(this,"Spieler existiert schon",Toast.LENGTH_LONG).show();
        }

    }

    public void addListenerOnListViewItemSelection() {
        playerLV.setClickable(true);
        playerLV.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player item = (Player) playerLV.getItemAtPosition(position);
                Intent intent = new Intent(parent.getContext(), SinglePlayerOverview.class);
                intent.putExtra("id",item.getId());
                startActivityForResult(intent,0);
            }
        });

    }


}
