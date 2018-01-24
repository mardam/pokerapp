package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.markusdamm.pokerapp.R;
import de.markusdamm.pokerapp.data.Gender;
import de.markusdamm.pokerapp.data.Location;
import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.data.PlayerStatistic;
import de.markusdamm.pokerapp.utils.ArrayAdapterStatistic;
import de.markusdamm.pokerapp.utils.DateFormats;
import de.markusdamm.pokerapp.utils.Utils;

public class Statistic extends ActionBarActivity {

    private ArrayList<PlayerStatistic> pStatistics = new ArrayList<>();
    private SQLiteDatabase database;
    private int gender;
    private TextView tv;
    private String choice1, choice2, choice3, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        if (location == null){
            location = "alle";
        }
        gender = Gender.BOTH_INT;
        choice1 = PlayerStatistic.stWorsePlayer;
        choice2 = PlayerStatistic.stMinuits;
        choice3 = PlayerStatistic.stWins;
        getPlayerStatistics();
        fillList();
        tv = (TextView)findViewById(R.id.tvValues);
        tv.setText(choice1 + " | " + choice2 + " | " + choice3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, StatisticOptions.class);
            intent.putExtra("gender", (gender + 1)%3);
            intent.putExtra("choice1", choice1);
            intent.putExtra("choice2", choice2);
            intent.putExtra("choice3", choice3);
            intent.putExtra("location", location);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public Set<Player> getPlayers(){
        Set<Player> players = new HashSet<>();
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        Cursor cursor  = database.rawQuery("SELECT id, name, gender FROM players", null);
        while(cursor.moveToNext()) {
            String entry = cursor.getString(cursor.getColumnIndex("name"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int gender = cursor.getInt(2);
            Player player = new Player(entry, id, gender);
            players.add(player);
        }
        cursor.close();
        database.close();
        return players;
    }

    public void getPlayerStatistics(){
        Set<Player> players = getPlayers();
        pStatistics.clear();
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        for (Player pl: players){
            PlayerStatistic ps = new PlayerStatistic(pl);
            ps.setBestPlace(getBestPlace(pl));
            ps.setWorstPlace(getWorstPlace(pl));
            ps.setWins(getNumberOfTopPositions(pl,1));
            ps.setHeadUps(getNumberOfTopPositions(pl,2));
            ps.setPodiums(getNumberOfTopPositions(pl,3));
            ps.setBeatenPlayers(getBeatenPlayers(pl));
            ps.setParticipations(getNumberOfParticipations(pl));
            ps.setLastPlaces(getLastPlaces(pl));
            ps.setMinuits(getMinuits(pl));
            ps.setParticipators(getParticipators(pl));
            ps.setSumOfPlaces(getGetSumOfPlaces(pl));
            ps.setMultikills(getMultikills(pl));
            ps.setAverage(0.3343423434);
            pStatistics.add(ps);


        }
        database.close();

    }

    public int getMinuits(Player pl){
        int minuits = 0;
        String sqlState = "SELECT p.time, e.date FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE nr>0 AND loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState,null);
        while(cursor.moveToNext()){
            DateFormat df = DateFormats.getDataBaseFormat();
            String s1 = cursor.getString(0);
            String s2 = cursor.getString(1);
            Date d1 = new Date();
            Date d2 = new Date();
            try {
                d1 = df.parse(s1);
                d2 = df.parse(s2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int diff = Math.round((d1.getTime() - d2.getTime())/60000);
            minuits = minuits + diff;

        }
        return minuits;
    }


    public int getMultikills(Player pl) {
        String sqlState = "SELECT count(*)\n" +
                "FROM (\n" +
                "SELECT pl.name as player, e.name as name, time, count(*) as value, count(DISTINCT p.winner) as winners\n" +
                "FROM places as p, evenings as e, players as pl\n" +
                "WHERE p.evening = e.id and e.name != 'Abend 1' and p.nr != 1 and pl.id = p.winner and pl.id = " + pl.getId() + getLocationStringForSqlQuery() + "\n" +
                "group by time\n" +
                ") WHERE value > 1 and winners = 1\n" +
                "ORDER BY value DESC, player";

        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getGetSumOfPlaces(Player pl){
        String sqlState = "SELECT sum(p.nr) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr>0 AND p.loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }


    public int getParticipators(Player pl){
        String sqlState = "SELECT count(p.id) FROM places p " +
                "Inner JOIN places p2 ON p.evening = p2.evening " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p2.loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }



    public int getNumberOfParticipations(Player pl){
        String sqlState = "SELECT count(p.evening) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getBeatenPlayers(Player pl){
        String sqlState = "SELECT count(p.loser) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.winner = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }


    public int getNumberOfTopPositions(Player pl, int worstPosition){
        String sqlState = "SELECT count(p.evening) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr <= " + worstPosition + " AND p.loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getBestPlace(Player pl){

        String sqlState = "SELECT min(p.nr) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                 ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getWorstPlace(Player pl){

        String sqlState = "SELECT max(p.nr) FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE p.nr > 0 AND p.loser = " + pl.getId() +
                getLocationStringForSqlQuery() +
                ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getLastPlaces(Player pl) {
        String sqlState = "SELECT count(*)\n" +
                "FROM evenings as e, places as p, players as pl\n" +
                "WHERE e.id = p.evening and pl.id = p.loser AND e.name != 'Abend 1'\n" +
                "AND (p.evening, p.nr) IN (SELECT evening, max(nr) FROM places group by evening) AND pl.id = " + pl.getId() +
                getLocationStringForSqlQuery() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public void fillList(){
        ListView lvStatistics = (ListView)findViewById(R.id.lvStatistics);
        ListAdapter listenAdapter;
        if (gender == Gender.BOTH_INT) {
            listenAdapter = new ArrayAdapterStatistic(this, pStatistics,choice1,choice2,choice3);
            lvStatistics.setAdapter(listenAdapter);
        }
        else{
            ArrayList<PlayerStatistic> psList = new ArrayList<>();
            for (PlayerStatistic ps : pStatistics) {
                if (ps.getPlayer().getGenderAsInt() == gender) {
                    psList.add(ps);
                }
            }
            listenAdapter = new ArrayAdapterStatistic(this, psList, choice1, choice2, choice3);
            lvStatistics.setAdapter(listenAdapter);
        }
    }


    private String getLocationStringForSqlQuery(){
        if (location.equals("alle")) {
            return "";
        }
        int id = getIDForLocation();
        return " AND e.location = " + id;
    }

    public int getIDForLocation(){

        //database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT id FROM locations WHERE name = '" + location + "';", null);
        cursor.moveToLast();
        int entry = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();
        //database.close();
        return entry;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            gender = Gender.getIntByString(data.getStringExtra("gender"));
            choice1 = data.getStringExtra("choice1");
            choice2 = data.getStringExtra("choice2");
            choice3 = data.getStringExtra("choice3");
            tv.setText(choice1 + " | " + choice2 + " | " + choice3);
            location = data.getStringExtra("location");
            getPlayerStatistics();
            fillList();
        }
    }

}
