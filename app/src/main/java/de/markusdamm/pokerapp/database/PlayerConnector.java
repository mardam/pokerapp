package de.markusdamm.pokerapp.database;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import de.markusdamm.pokerapp.data.Player;

/**
 * Created by Markus Damm on 28.03.2015.
 */
public class PlayerConnector extends Activity{
    private SQLiteDatabase database;
    private static PlayerConnector instance;

    private PlayerConnector(){
        super();
        Intent intent = new Intent(this,PlayerConnector.class);
        startActivity(intent);
    }

    public static PlayerConnector getInstance(){
        if (instance == null){
            instance = new PlayerConnector();
        }
        return instance;
    }

    public void addPlayerToDB(Player player){
        //ArrayList<String> oldPlayers = getPlayers();
            //if (!oldPlayers.contains(player.getName())){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        database.execSQL("INSERT INTO players (name) VALUES('" + player.getName() + "');'");
        database.close();

    }
}
