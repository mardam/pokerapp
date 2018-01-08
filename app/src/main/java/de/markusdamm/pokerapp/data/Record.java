package de.markusdamm.pokerapp.data;

import java.util.ArrayList;

/**
 * Created by Markus Damm on 08.01.2018.
 */

public class Record {

    private String evening;
    private String player;
    private String value;

    public Record(String evening, String player, String value) {
        this.evening = evening;
        this.player = player;
        this.value = value;
    }

    public String getEvening() {
        return(this.evening);
    }

    public String getPlayer() {
        return(this.player);
    }

    public String getValue() {
        return(this.value);
    }

    public static ArrayList<String> getPossibleRecords() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add("Längster Abend");
        ret.add("Kürzester Abend");
        ret.add("Frühester Beginn");
        ret.add("Spätester Beginn");
        ret.add("Frühestes Ende");
        ret.add("Spätestes Ende");
        ret.add("Männlichster Abend");
        ret.add("Weiblichster Abend");
        ret.add("Frühestes Ausscheiden");
        ret.add("Spätestes Aussscheiden");
        ret.add("Multikills");

        return(ret);
    }
}
