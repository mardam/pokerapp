package de.markusdamm.pokerapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import de.markusdamm.pokerapp.data.Gender;
import de.markusdamm.pokerapp.data.PlayerStatistic;


public class StatisticOptions extends ActionBarActivity {

    private Spinner spGender, spChoice1, spChoice2, spChoice3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_options);
        spGender = (Spinner) findViewById(R.id.spGender);
        spChoice1 = (Spinner) findViewById(R.id.spChoice1);
        spChoice2 = (Spinner) findViewById(R.id.spChoice2);
        spChoice3 = (Spinner) findViewById(R.id.spChoice3);
        fillLists();
        Intent intent = getIntent();
        spGender.setSelection(intent.getIntExtra("gender",0));
        spChoice1.setSelection(getPositionForString(intent.getStringExtra("choice1")));
        spChoice2.setSelection(getPositionForString(intent.getStringExtra("choice2")));
        spChoice3.setSelection(getPositionForString(intent.getStringExtra("choice3")));
    }

    public int getPositionForString(String string){
        PlayerStatistic ps = new PlayerStatistic(null);
        ArrayList<String> strings = ps.getStrings();
        for (int i = 0; i<strings.size();i++){
            if (string.equals(strings.get(i))){
                return i;
            }
        }
        return 0;
    }


    public void fillLists(){
        PlayerStatistic ps = new PlayerStatistic(null);
        fillGender();
        ArrayList<String> choiceList = ps.getStrings();
        ArrayAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,choiceList);
        spChoice1.setAdapter(listenAdapter);
        spChoice2.setAdapter(listenAdapter);
        spChoice3.setAdapter(listenAdapter);
    }

    public void fillGender(){
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add(Gender.BOTH_STRING);
        genderList.add(Gender.MALE_STRING);
        genderList.add(Gender.FEMALE_STRING);
        ArrayAdapter listenAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderList);
        spGender.setAdapter(listenAdapter);
    }

    public void saveOptions(View view){
        Intent intent = new Intent();
        intent.putExtra("gender",(String)spGender.getSelectedItem());
        intent.putExtra("choice1",(String)spChoice1.getSelectedItem());
        intent.putExtra("choice2",(String)spChoice2.getSelectedItem());
        intent.putExtra("choice3",(String)spChoice3.getSelectedItem());

        setResult(1, intent);
        finish();
    }
}
