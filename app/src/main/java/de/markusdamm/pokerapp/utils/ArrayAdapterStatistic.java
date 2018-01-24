package de.markusdamm.pokerapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import de.markusdamm.pokerapp.R;
import de.markusdamm.pokerapp.data.PlayerStatistic;

/**
 * Created by Markus Damm on 31.03.2015.
 */
public class ArrayAdapterStatistic extends ArrayAdapter<PlayerStatistic> {
    private Context context;
    private List<PlayerStatistic> players;
    private String choice1, choice2, choice3;
    private DecimalFormat f = new DecimalFormat("#0.00");

    public ArrayAdapterStatistic(Context context, List<PlayerStatistic> players, String choice1, String choice2, String choice3) {
        super(context, R.layout.activity_list_item_player, players);
        this.context = context;
        this.players = players;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        for (PlayerStatistic ps: players){
            ps.setValues(choice1,choice2,choice3);
        }
        Collections.sort(players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayerStatistic ps = players.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.activity_list_item_player, parent, false);
        TextView name = (TextView) row.findViewById(R.id.name_value);
        TextView age = (TextView) row.findViewById(R.id.age_value);

        String namenText = (position + 1) + ": " + ps.getPlayer().getName();

        name.setText(namenText);


        String ageText;

        if (choice1.equals(PlayerStatistic.stAveragePlace)){
            ageText = f.format(((double)ps.getSumOfPlaces())/ps.getParticipations()) + " | ";
        }
        else{
            ageText = players.get(position).getValue(choice1) + " | ";
        }

        if (choice2.equals(PlayerStatistic.stAveragePlace)){
            ageText = ageText + f.format(((double)ps.getSumOfPlaces())/ps.getParticipations()) + " | ";
        }
        else{
            ageText = ageText + players.get(position).getValue(choice2) + " | ";
        }
        if (choice3.equals(PlayerStatistic.stAveragePlace)){
            ageText = ageText + f.format(((double)ps.getSumOfPlaces())/ps.getParticipations());
        }
        else{
            ageText = ageText + players.get(position).getValue(choice3);
        }
        age.setText(ageText);
        //age.setText(players.get(position).getMinuits() + " | " + players.get(position).formatTimeToString());

        return row;
    }
}
