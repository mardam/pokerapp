package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Location;
import de.markusdamm.pokerapp.data.Placement;
import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.utils.DateFormats;
import de.markusdamm.pokerapp.utils.Utils;


public class SingleEvening extends ActionBarActivity {

    private Evening evening;
    private EditText day, time, name, loc;
    private SQLiteDatabase database;
    private String evName;
    private ListView lvPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_evening);
        Intent intent = getIntent();
        evName = intent.getStringExtra("eveningName");
        this.setTitle(evName);
        evening = getEvening(evName);
        name = (EditText) findViewById(R.id.eveningName);
        day = (EditText) findViewById(R.id.eveningDate);
        time = (EditText) findViewById(R.id.eveningTime);
        loc = (EditText) findViewById(R.id.eveningLocation);
        name.setText(evening.getName());
        day.setText(DateFormats.getGermanDay().format(evening.getDate()));
        time.setText(DateFormats.getGermanTime().format(evening.getDate()));
        loc.setText(evening.getLocation().getName());
        lvPlayers = (ListView) findViewById(R.id.lvPlayers);
        //fillList();
    }



    @Override
    protected void onResume(){
        super.onResume();
        evening = getEvening(evName);
        getPlaces();
        fillList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_evening, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            if (evening.isStarted()) {
                Toast.makeText(this,"Abend hat bereits angefangen",Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent = new Intent(this, PlayerAdderToEvening.class);
                intent.putExtra("eveId", evening.getId());
                startActivity(intent);
            }
            return true;
        }

        if (id == R.id.action_loose){
            if (evening.isFinished()){
                Toast.makeText(this,"Abend bereits beendet",Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(this, PlayerLooses.class);
                intent.putExtra("eveId", evening.getId());
                intent.putExtra("place", evening.getWorstPlaceForUnsetPlayer());
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getPlaces(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);

        Cursor cursor  = database.rawQuery("SELECT p1.nr, p2.name, p2.id, p1.winner, p1.time, p2.gender " +
                "FROM places p1 " +
                "INNER JOIN players p2 ON p1.loser = p2.id " +
                "WHERE p1.evening = " + Integer.toString(evening.getId()) + " " +
                "ORDER BY p1.nr ASC;", null);
        while(cursor.moveToNext()){
            int place = cursor.getInt(0);
            String plName = cursor.getString(1);
            int plId = cursor.getInt(2);



            Player loser = Utils.getPlayerFromPlayerSetById(evening.getPlayers(), plId);
            if (loser == null){
                loser = new Player(plName, plId, cursor.getInt(5));
            }
            Placement pm = new Placement(place,loser);

            SimpleDateFormat df = DateFormats.getDataBaseFormat();

            String stDate = cursor.getString(4);
            Date date = new Date();
            try {
                date = df.parse(stDate);
            } catch (ParseException e) {
                Toast.makeText(this,"Datum falsch erkannt " + stDate, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }


            pm.setDate(date);
            int wId = cursor.getInt(3);
            if (place>1 && wId >0){
                Player winner = Utils.getPlayerFromPlayerSetById(evening.getPlayers(), wId);
                if (winner == null){
                    winner = getPlayerByID(wId);
                }
                pm.setWinner(winner);
            }
            evening.enterPlacement(pm);
        }
        cursor.close();
        database.close();
    }

    public Player getPlayerByID(int id){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE, null);
        String sqlstate = "SELECT name, gender FROM players WHERE name = " + id + ";";
        Cursor cursor = database.rawQuery(sqlstate, null);
        cursor.moveToLast();
        String name = cursor.getString(0);
        int gender = cursor.getInt(1);
        Player pl = new Player(name, id, gender);
        cursor.close();
        database.close();
        return pl;
    }


    public Evening getEvening(String name){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        Cursor cursor  = database.rawQuery(
                "SELECT e.id, e.date, l.name " +
                        "FROM evenings e " +
                        "INNER JOIN locations l ON l.id = e.location " +
                        "WHERE e.name = '" + name + "';", null);
        cursor.moveToLast();

        SimpleDateFormat df = DateFormats.getDataBaseFormat();

        String stDate = cursor.getString(1);
        Date date = new Date();
        try {
            date = df.parse(stDate);
        } catch (ParseException e) {
            Toast.makeText(this,"Datum falsch erkannt " + stDate, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        int id = cursor.getInt(0);
        String loc = cursor.getString(2);
        Location location = new Location(loc);
        cursor.close();
        database.close();
        Evening evening = new Evening(location,date, name);
        evening.setId(id);
        return evening;
    }


    public ArrayList<String> getPlayers(){
        SimpleDateFormat sdf = DateFormats.getGermanTime();
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ArrayList<String> ret = new ArrayList<>();
        for (Placement pm: evening.getPlacements()){
            if (pm.getNumber() <= 0){
                ret.add(pm.getPlayer().getName());
            }
            else{
                if (pm.getWinner() == null || pm.getNumber() == 1){
                    ret.add(Integer.toString(pm.getNumber()) +  ": " + pm.getPlayer().getName());
                }
                else{
                    if (pm.getNumber() > 1){
                        ret.add(Integer.toString(pm.getNumber()) + ": " + pm.getPlayer().getName() + " an "
                                + pm.getWinner().getName() + " um " + sdf.format(pm.getDate()));
                    }
                }
            }
        }
        return ret;
    }

    public void fillList(){
        ArrayList<String> playerList = getPlayers();
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, playerList);
        lvPlayers.setAdapter(listenAdapter);
    }

}
