package de.markusdamm.pokerapp.utils;

import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.markusdamm.pokerapp.data.Placement;
import de.markusdamm.pokerapp.data.Player;

/**
 * Created by Markus Damm on 25.03.2015.
 */
public class Utils {

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        int year, month,day;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        return addZeros(day) + "." + addZeros(month) + "." + Integer.toString(year);
    }

    public static String addZeros(int i){
        if (i<10){
            return "0" + Integer.toString(i);
        }
        else{
            return Integer.toString(i);
        }
    }

    public static String getTime(){
        Calendar c = Calendar.getInstance();
        int hour, minute;
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        return addZeros(hour) + ":" + addZeros(minute);
    }


    public static Date getCurrentDate(){
        return new Date();
    }

    public static Player getPlayerFromMap(Map<Player,Boolean> players,String name){
        for (Player pl:players.keySet()){
            if (pl.getName().equals(name)){
                return pl;
            }
        }
        return null;
    }

    public static Player getPlayerFromPlayerSetById(Set<Player> players, int id){
        for (Player pl:players){
            if (pl.getId() == id){
                return pl;
            }
        }
        return null;
    }

    public static Player getPlayerFromPlayerSetByName(Set<Player> players, String name){
        for (Player pl:players){
            if (pl.getName().equals(name)){
                return pl;
            }
        }
        return null;
    }


    public static Player getPlayerFromList(List<Player> playerList,String name){
        for (Player pl:playerList){
            if (pl.getName().equals(name)){
                return pl;
            }
        }
        return null;
    }

    public static Player getPlayerFromListById(List<Player> playerList,int id){
        for (int i = 0; i<playerList.size();i++){
            Player pl = playerList.get(i);
            if (pl.getId() == id){
                return pl;
            }
        }
        return null;
    }
}
