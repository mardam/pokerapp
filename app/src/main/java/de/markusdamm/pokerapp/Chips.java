package de.markusdamm.pokerapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class Chips extends ActionBarActivity {

    TextView playerCount;
    TextView tvReds;
    TextView tvBlues;
    TextView tvGreens;
    TextView tvWhites;
    TextView tvBlacks;
    TextView tvChipCount;

    private int blacks = 8*9+4;
    private int greens = 8*9+7;
    private int blues = 11*9+5;
    private int reds = 11*9+4;
    private int whites = 16*9+6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chips);

        SeekBar seekBar = (SeekBar) findViewById(R.id.sbPlayerCount);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        playerCount = (TextView) findViewById(R.id.tvPlayerCount);



        tvReds = (TextView) findViewById(R.id.countRed);
        tvBlues = (TextView) findViewById(R.id.countBlue);
        tvGreens = (TextView) findViewById(R.id.countGreen);
        tvWhites = (TextView) findViewById(R.id.countWhite);
        tvBlacks = (TextView) findViewById(R.id.countBlack);
        tvChipCount = (TextView) findViewById(R.id.chipCount);

        updateTextLabels(progress);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            updateTextLabels(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };


    private void updateTextLabels(int progress) {
        progress = progress + 2;
        int redCount = (int) Math.floor((double) reds / progress);
        int blueCount = (int) Math.floor((double) blues / progress);
        int greenCount = (int) Math.floor((double) greens / progress);
        int whiteCount = (int) Math.floor((double) whites / progress);
        int blackCount = (int) Math.floor((double) blacks / progress);

        int chipCount = redCount * 5 + blueCount * 10 + greenCount * 25 + whiteCount * 50 + blackCount * 100;
        int totalCount = chipCount * progress;

        tvReds.setText(Integer.toString(redCount));
        tvBlues.setText(Integer.toString(blueCount));
        tvGreens.setText(Integer.toString(greenCount));
        tvWhites.setText(Integer.toString(whiteCount));
        tvBlacks.setText(Integer.toString(blackCount));
        tvChipCount.setText("Gesamtwert an Chips pro Spieler: " + String.format("%,d", chipCount) + "\n" + "Wert an Chips im Spiel: " + String.format("%,d", totalCount));
        playerCount.setText("Anzahl an Teilnehmern: " + progress);
    }

}
