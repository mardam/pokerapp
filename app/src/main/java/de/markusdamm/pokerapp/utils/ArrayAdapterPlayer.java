package de.markusdamm.pokerapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.markusdamm.pokerapp.R;
import de.markusdamm.pokerapp.data.Gender;
import de.markusdamm.pokerapp.data.Player;

/**
 * Created by Markus Damm on 28.03.2015.
 */
public class ArrayAdapterPlayer extends ArrayAdapter<Player> {
    private Context context;
    private List<Player> players;

    public ArrayAdapterPlayer(Context context, List<Player> players) {
        super(context, R.layout.activity_list_item_player, players);
        this.context = context;
        this.players = players;
        Collections.sort(players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.activity_list_item_player, parent, false);

        LinearLayout linLayout = (LinearLayout) row.findViewById(R.id.lin_layout_text);
        TextView name = (TextView) row.findViewById(R.id.name_value);
        TextView age = (TextView) row.findViewById(R.id.age_value);
        TextView separator = (TextView) row.findViewById(R.id.seperator);

        name.setText(players.get(position).getName());
        age.setText(Gender.INSTANCE.getString(players.get(position).getGender()) + "");
        if (!players.get(position).isSelectable()){
            players.get(position).setSelected(false);
            name.setTextColor(Color.GREEN);
        }
        if (players.get(position).isSelected()){
            name.setBackgroundColor(Color.CYAN);
        }

        if(position == 0) {
            separator.setVisibility(View.VISIBLE);
            separator.setText(players.get(position).getName().substring(0, 1));
        } else if(!players.get(position).getName().substring(0, 1).
                equals(players.get(position - 1).getName().substring(0, 1))) {
            separator.setVisibility(View.VISIBLE);
            separator.setText(players.get(position).getName().substring(0, 1));
        } else {
            separator.setVisibility(View.GONE);
            separator.setText("");
        }

        return row;
    }
}

