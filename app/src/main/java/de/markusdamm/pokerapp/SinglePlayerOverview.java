package de.markusdamm.pokerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.markusdamm.pokerapp.data.Gender;
import de.markusdamm.pokerapp.data.Player;
import de.markusdamm.pokerapp.data.PlayerStatistic;
import de.markusdamm.pokerapp.utils.DateFormats;


public class SinglePlayerOverview extends ActionBarActivity {

    private SQLiteDatabase database;
    private PlayerStatistic ps;
    private Switch sGender;
    private EditText etName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_overview);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",-1);
        Player player = getPlayer(id);
        etName = (EditText)findViewById(R.id.etName);
        etName.setText(player.getName());
        this.setTitle(player.getName());
        ps = new PlayerStatistic(player);
        fillStatistic();
        fillListView();
        sGender = (Switch)findViewById(R.id.sGender);
        sGender.setChecked(player.getGender());
        sGender.setText("Geschlecht: " + Gender.getString(player.getGender()));

        addSwitchListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_single_player_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            if (ps.getParticipations() > 0){
                Toast.makeText(this, ps.getPlayer().getName() +
                        " hat bereits an Abenden teilgenommen und kann deshalb nicht gelöscht werden.",
                        Toast.LENGTH_LONG).show();
            } else {
                String sqlState = "DELETE FROM players " +
                        "WHERE id = " + ps.getPlayer().getId();
                database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
                database.execSQL(sqlState);
                database.close();
                Toast.makeText(this, ps.getPlayer().getName() + " gelöscht.",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(7, intent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }



    public Player getPlayer(int id){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);


        Cursor cursor  = database.rawQuery("SELECT id, name, gender FROM players WHERE id = " + id, null);
        cursor.moveToLast();
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int gender = cursor.getInt(2);
        Player player = new Player(name, id, gender);
        cursor.close();
        database.close();
        return player;
    }

    public void fillListView() {
        ListView lvStatistics = (ListView)findViewById(R.id.lvStatistics);
        ListAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ps.getStatisticList());
        lvStatistics.setAdapter(listenAdapter);
    }




    public void fillStatistic(){
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        ps.setBestPlace(getBestPlace());
        ps.setWorstPlace(getWorstPlace());
        ps.setWins(getNumberOfTopPositions(1));
        ps.setHeadUps(getNumberOfTopPositions(2));
        ps.setPodiums(getNumberOfTopPositions(3));
        ps.setBeatenPlayers(getBeatenPlayers());
        ps.setParticipations(getNumberOfParticipations());
        ps.setLastPlaces(getNumberOfLastPlaces());
        ps.setMinuits(getMinuits());
        ps.setParticipators(getParticipators());
        ps.setSumOfPlaces(getGetSumOfPlaces());
        ps.setMultikills(getMultikills());
        ps.setMostDeaths(getMostDeaths());
        ps.setMostKills(getMostKills());
        ps.setSd(getSD());
        ps.setMedian(getMedian());
        ps.setNormalizedMean(getNormalizedMean());


        database.close();
    }


    public int getMinuits(){
        int minuits = 0;
        String sqlState = "SELECT p.time, e.date FROM places p " +
                "INNER JOIN evenings e ON e.id = p.evening " +
                "WHERE nr>0 AND loser = " + ps.getPlayer().getId() + ";";
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


    public int getGetSumOfPlaces(){
        String sqlState = "SELECT sum(nr) FROM places " +
                "WHERE nr>0 AND loser = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }


    public int getParticipators(){
        String sqlState = "SELECT count(p1.id) FROM places p1 " +
                "Inner JOIN places p2 ON p1.evening = p2.evening " +
                "WHERE p2.loser = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }



    public int getNumberOfParticipations(){
        String sqlState = "SELECT count(evening) FROM places WHERE nr > 0 AND loser = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getBeatenPlayers(){
        String sqlState = "SELECT count(loser) FROM places WHERE nr > 0 AND winner = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }


    public int getNumberOfTopPositions(int worstPosition){
        String sqlState = "SELECT count(evening) FROM places WHERE nr <= " + worstPosition + " AND loser = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getBestPlace(){
        String sqlState = "SELECT min(nr) FROM places WHERE nr > 0 AND loser = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getWorstPlace(){
        String sqlState = "SELECT max(nr) FROM places WHERE nr > 0 AND loser = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getNumberOfLastPlaces(){
        String sqlState = "SELECT count(*)\n" +
                "FROM evenings as e, places as p, players as pl\n" +
                "WHERE e.id = p.evening and pl.id = p.loser AND e.name != 'Abend 1'\n" +
                "AND (p.evening, p.nr) IN (SELECT evening, max(nr) FROM places group by evening) AND pl.id = " + ps.getPlayer().getId() + ";";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public int getMultikills() {
        String sqlState = "SELECT count(*)\n" +
                "FROM (\n" +
                "SELECT pl.name as player, e.name as name, time, count(*) as value, count(DISTINCT p.winner) as winners\n" +
                "FROM places as p, evenings as e, players as pl\n" +
                "WHERE p.evening = e.id and e.name != 'Abend 1' and p.nr != 1 and pl.id = p.winner and pl.id = " + ps.getPlayer().getId() + "\n" +
                "group by time\n" +
                ") WHERE value > 1 and winners = 1\n" +
                "ORDER BY value DESC, player";

        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }

    public Pair<Integer, List<String>> getMostKills() {
        String sqlState = "with y as (\n" +
                "SELECT count(*) as value, pl.name as player\n" +
                "FROM places p, players pl\n" +
                "WHERE p.nr != 1 AND p.evening != 1 AND pl.id = p.loser and p.winner = " + ps.getPlayer().getId() + "\n" +
                "GROUP BY winner, loser)\n" +
                "\n" +
                "SELECT * FROM y\n" +
                "WHERE value = (SELECT max(value) FROM y)";
        Cursor cursor = database.rawQuery(sqlState, null);

        List<String> ret = new ArrayList<>();
        int val = -1;

        while(cursor.moveToNext()){
            String entry = cursor.getString(cursor.getColumnIndex("player"));
            val = cursor.getInt(cursor.getColumnIndex("value"));
            ret.add(entry);
        }
        cursor.close();
        return new Pair<>(val, ret);
    }

    public Pair<Integer, List<String>> getMostDeaths() {
        String sqlState = "with y as (\n" +
                "SELECT count(*) as value, pl.name as player\n" +
                "FROM places p, players pl\n" +
                "WHERE p.nr != 1 AND p.evening != 1 AND pl.id = p.winner and p.loser = " + ps.getPlayer().getId() + "\n" +
                "GROUP BY winner, loser)\n" +
                "\n" +
                "SELECT * FROM y\n" +
                "WHERE value = (SELECT max(value) FROM y)";
        Cursor cursor = database.rawQuery(sqlState, null);

        List<String> ret = new ArrayList<>();
        int val = -1;

        while(cursor.moveToNext()){
            String entry = cursor.getString(cursor.getColumnIndex("player"));
            val = cursor.getInt(cursor.getColumnIndex("value"));
            ret.add(entry);
        }
        cursor.close();
        return new Pair<>(val, ret);
    }

    public double getSD() {
        String sqlState = "with mean as (\n" +
                "SELECT avg(nr) AS Mean FROM places WHERE loser = " + ps.getPlayer().getId() + ")\n" +
                "SELECT avg((nr-mean.mean)*(nr-mean.mean)) as sd from places, mean\n" +
                "WHERE loser = " + ps.getPlayer().getId();

        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return Math.sqrt(cursor.getDouble(0));
    }

    public double getMedian() {
        String sqlState = "with curr_table AS (\n" +
                "SELECT nr FROM places WHERE loser = " + ps.getPlayer().getId() + ")\n" +
                "\n" +
                "SELECT avg(nr)\n" +
                "FROM (SELECT nr\n" +
                "      FROM curr_table\n" +
                "      ORDER BY nr\n" +
                "      LIMIT 2 - (SELECT COUNT(*) FROM curr_table) % 2    -- odd 1, even 2\n" +
                "      OFFSET (SELECT (COUNT(*) - 1) / 2\n" +
                "              FROM curr_table))";

        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getDouble(0);
    }

    public double getNormalizedMean() {
        String sqlState = "SELECT avg(1.0 * nr/max_val) as normalized from places p1\n" +
                "JOIN (SELECT max(nr) as max_val, evening FROM places GROUP BY evening) AS p2 \n" +
                "ON p1.evening = p2.evening AND p1.loser = " + ps.getPlayer().getId();

        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        return cursor.getDouble(0);
    }

    public void saveChanges(View view){
        Player newPl = new Player(etName.getText().toString(), ps.getPlayer().getId(),0);
        newPl.setGender(sGender.isChecked());
        database = openOrCreateDatabase("pokerDB", MODE_PRIVATE,null);
        String sqlState = "SELECT count(name) FROM players WHERE name = '" + newPl.getName() + "';";
        Cursor cursor = database.rawQuery(sqlState, null);
        cursor.moveToLast();
        if (cursor.getInt(0) == 0 || newPl.getName().equals(ps.getPlayer().getName())){
            sqlState = "UPDATE players " +
                    "SET name = '" + newPl.getName() + "', gender = " + newPl.getGenderAsInt() + " " +
                    "WHERE id = " + ps.getPlayer().getId();
            database.execSQL(sqlState);
            Toast.makeText(this,"Spieler gespeichert",Toast.LENGTH_LONG).show();
            cursor.close();
            database.close();
            Intent intent = new Intent();
            setResult(7,intent);
            finish();
        }
        else{
            Toast.makeText(this,"Spieler existiert bereits",Toast.LENGTH_LONG).show();
        }
        cursor.close();
        database.close();


    }

    public void addSwitchListener(){
        sGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sGender.setText("Geschlecht: " + Gender.getString(sGender.isChecked()));
            }
        });
    }

}
