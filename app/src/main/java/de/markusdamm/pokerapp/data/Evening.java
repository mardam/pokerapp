package de.markusdamm.pokerapp.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Evening implements Comparable<Evening>{


    private Date date;
    private Location location;
    private String name;
    private boolean finished , started;
    //private Set<Player> players;
    private ArrayList<Placement> placements = new ArrayList<>();
    private int id;

    public Evening(Location location, Date date, String name){
        this.name = name;
        this.date = date;
        this.location = location;
        this.finished = false;
        this.started = false;
    }


    public int getId(){
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public ArrayList<Placement> getPlacements() {
        return placements;
    }

    public Date getDate() {
        return date;
    }

    public int getNumberOfParticipants() {
        return placements.size();
    }

    public Set<Player> getPlayers() {
        Set <Player> players = new HashSet<>();
        for (Placement pm: placements){
            players.add(pm.getPlayer());
            if (pm.getWinner() != null){
                players.add(pm.getWinner());
            }
        }
        return players;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setId(int id){
        this.id = id;
    }


    public boolean isStarted(){
        return started;
    }

    public boolean isFinished(){
        return finished;
    }

    public String getName(){
        return name;
    }


    public void enterPlacement(Placement pm){
        placements.add(pm);
        Collections.sort(placements);
        if (pm.getNumber() == 1){
            finished = true;
        }
        if (pm.getNumber()>0){
            started = true;
        }
    }

    public int getWorstPlaceForUnsetPlayer(){
        int ret = placements.size();
        for (Placement pm:placements){
            if (pm.getNumber()>0 && pm.getNumber()<=ret){
                ret = pm.getNumber() - 1;
            }
        }
        return ret;
    }

    @Override
    public int compareTo(Evening another) {
        return this.getDate().compareTo(another.getDate());
    }
}