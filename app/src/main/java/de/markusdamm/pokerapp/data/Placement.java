package de.markusdamm.pokerapp.data;

import java.util.Date;

/**
 * Created by Markus Damm on 23.03.2015.
 */
public class Placement implements Comparable<Placement>{

    private int number;
    private Player player;
    private Player winner;
    private Date date;

    public Placement(int number, Player player){
        this.number = number;
        this.player = player;
    }


    public Player getWinner(){
        return winner;
    }

    public int getNumber() {
        return number;
    }

    public Player getPlayer() {
        return player;
    }

    public Date getDate(){
        return date;
    }

    public void setWinner(Player winner){
        this.winner = winner;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    public void setDate(Date date){
        this.date = date;
    }

    @Override
    public int compareTo(Placement another) {
        if (this.getNumber() == another.getNumber()) {
            return 0;
        }
        return this.getNumber() < another.getNumber() ? -1 : 1;
    }
}
