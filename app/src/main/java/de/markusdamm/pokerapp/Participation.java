package de.markusdamm.pokerapp;

import de.markusdamm.pokerapp.data.Evening;
import de.markusdamm.pokerapp.data.Player;

/**
 * Created by Markus Damm on 06.01.2019.
 */

public class Participation {
    private Evening evening;
    private int position;
    private int max;
    private Player beatenBy;

    public Participation(Evening evening, int position, int max, Player beatenBy) {
        this.evening = evening;
        this.position = position;
        this.max = max;
        this.beatenBy = beatenBy;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Evening getEvening() {
        return evening;
    }

    public void setEvening(Evening evening) {
        this.evening = evening;
    }

    public String toString() {
        String ret = evening.getName() + ": " + Integer.toString(position) + " von " + max;
        if (beatenBy.getName() != null) {
            ret = ret +  " (Killer: " + beatenBy.getName() + ")";
        }
        return(ret);
    }
}
