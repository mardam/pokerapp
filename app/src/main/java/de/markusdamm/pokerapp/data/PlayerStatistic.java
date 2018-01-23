package de.markusdamm.pokerapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Damm on 30.03.2015.
 */
public class PlayerStatistic implements Comparable<PlayerStatistic> {

    private int bestPlace;
    private int worstPlace;
    private int wins;
    private int headUps;
    private int podiums;
    private int lastPlaces;
    private int participations;
    private int minuits;
    private int beatenPlayers;
    private int participators;
    private int sumOfPlaces;
    private int multikills;
    private Player player;
    private ArrayList<String> strings;

    private String value1, value2, value3;

    public final static String stBestPlace = "Beste Platzierung";
    public final static String stWorstPlace = "Schlechteste Platzierung";
    public final static String stWins = "Siege";
    public final static String stHeadUps = "Heads-Ups";
    public final static String stPodiums = "Podiumsplätze";
    public final static String stParticipations = "Anzahl an Teilnahmen";
    public final static String stMinuits = "Gespielte Zeit";
    public final static String stBeatenPlayers = "Anzahl rausgeworfener Spieler";
    public final static String stNumberOfOponents = "Anzahl Gegner";
    public final static String stSumOfPlaces = "Summe der Plätze";
    public final static String stWorsePlayer = "Anzahl schlechtere Spieler bei Teilnahme";
    public final static String stAveragePlace = "Durchschnittliche Platzierung";
    public final static String stMultikills = "Multikills";
    public final static String stLastPlaces = "Letzte Plätze";


    public PlayerStatistic(Player player){
        this.player = player;
        strings = new ArrayList<>();
        fillStrings();
    }

    public int getSumOfPlaces(){
        return sumOfPlaces;
    }

    public int getWorstPlace() {
        return worstPlace;
    }

    public int getBestPlace() {
        return bestPlace;
    }

    public int getWins() {
        return wins;
    }

    public int getParticipations() {
        return participations;
    }

    public int getMinuits() {
        return minuits;
    }

    public int getBeatenPlayers() {
        return beatenPlayers;
    }

    public int getParticipators() {
        return participators;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMultikills() {
        return multikills;
    }


    public int getPodiums() {
        return podiums;
    }

    public int getHeadUps() {
        return headUps;
    }

    public int getLastPlaces() {
        return lastPlaces;
    }


    public void setSumOfPlaces(int sumOfPlaces){
        this.sumOfPlaces = sumOfPlaces;
    }

    public void setBestPlace(int bestPlace) {
        this.bestPlace = bestPlace;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setParticipations(int participations) {
        this.participations = participations;
    }

    public void setMinuits(int minuits) {
        this.minuits = minuits;
    }

    public void setWorstPlace(int worstPlace) {
        this.worstPlace = worstPlace;
    }

    public void setBeatenPlayers(int beatenPlayers) {
        this.beatenPlayers = beatenPlayers;
    }

    public void setParticipators(int participators) {
        this.participators = participators;
    }

    public void setMultikills(int multikills) {
        this.multikills = multikills;
    }

    public void setPodiums(int podiums) {
        this.podiums = podiums;
    }

    public void setHeadUps(int headUps) {
        this.headUps = headUps;
    }

    public void setLastPlaces(int lastPlaces) {
        this.lastPlaces = lastPlaces;
    }


    public void setValues(String value1, String value2, String value3){
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }



    public ArrayList<String> getStatisticList(){
        ArrayList<String> statisticList = new ArrayList<>();
        statisticList.add(stBestPlace + ": " + bestPlace);
        statisticList.add(stWorstPlace + ": " + worstPlace);
        statisticList.add(stWins + ": " + wins);
        statisticList.add(stHeadUps + ": " + headUps);
        statisticList.add(stPodiums + ": " + podiums);
        statisticList.add(stLastPlaces + ": " + lastPlaces);
        statisticList.add(stParticipations + ": " + participations);
        statisticList.add(stMinuits + ": " + minuits + " Minuten bzw. " + formatTimeToString());
        statisticList.add(stBeatenPlayers + ": " + beatenPlayers);
        statisticList.add(stNumberOfOponents + ": " + participators);
        statisticList.add(stSumOfPlaces + ": " + sumOfPlaces);
        statisticList.add(stWorsePlayer + ": " + Integer.toString(participators - sumOfPlaces));
        statisticList.add(stAveragePlace + ": " + ((double)sumOfPlaces) / participations);
        statisticList.add(stMultikills + ": " + multikills);



        return statisticList;
    }


    public int getValue(String value){
        switch (value){
            case stBestPlace:
                return getBestPlace();
            case stWins:
                return getWins();
            case stMinuits:
                return getMinuits();
            case stBeatenPlayers:
                return getBeatenPlayers();
            case stNumberOfOponents:
                return getParticipators();
            case stSumOfPlaces:
                return getSumOfPlaces();
            case stWorsePlayer:
                return getParticipators() - getSumOfPlaces();
            case stParticipations:
                return getParticipations();
            case stAveragePlace:
                return sumOfPlaces / participations;
            case stMultikills:
                return getMultikills();
            case stHeadUps:
                return getHeadUps();
            case stPodiums:
                return getPodiums();
            case stWorstPlace:
                return getWorstPlace();
            case stLastPlaces:
                return getLastPlaces();
            default:
                return -1;
        }
    }


    public String formatTimeToString(){
        int days = (int)Math.floor(minuits/(60*24));
        int hours = (int)Math.floor((minuits - days * 60 * 24)/60);
        String hour = hours + "";
        if (hours <10){
            hour = "0" + hour;
        }
        int rest = (int)Math.floor(minuits - days * 60 * 24 - hours * 60);
        String res = rest + "";
        if (rest < 10){
            res = "0" + rest;
        }
        return days + "d" + hour + "h" + res + "m";
    }


    public void fillStrings(){
        strings.clear();
        strings.add(stBestPlace);
        strings.add(stWorstPlace);
        strings.add(stWins);
        strings.add(stHeadUps);
        strings.add(stPodiums);
        strings.add(stLastPlaces);
        strings.add(stParticipations);
        strings.add(stMinuits);
        strings.add(stBeatenPlayers);
        strings.add(stNumberOfOponents);
        strings.add(stSumOfPlaces);
        strings.add(stWorsePlayer);
        strings.add(stAveragePlace);
        strings.add(stMultikills);
    }

    public ArrayList<String> getStrings(){
        return strings;
    }

    public List<String> inverseToSort() {
        List<String> ret = new ArrayList<>();
        ret.add(stBestPlace);
        ret.add(stSumOfPlaces);
        ret.add(stWorstPlace);
        ret.add(stLastPlaces);

        return ret;
    }

    @Override
    public int compareTo(PlayerStatistic another) {
        if (value1.equals(stAveragePlace)){
            Double a = ((double)this.getSumOfPlaces())/this.getParticipations();
            Double b = ((double)another.getSumOfPlaces())/another.getParticipations();
            if (a.floatValue() != b.floatValue()){
                return a.compareTo(b);
            }
        }
        else {
            Integer a = this.getValue(value1);
            Integer b = another.getValue(value1);
            if (a.intValue() != b.intValue()) {
                if (inverseToSort().contains(value1)) {
                    return a.compareTo(b);
                } else {
                    return b.compareTo(a);
                }
            }
        }

        if (value2.equals(stAveragePlace)){
            Double a = ((double)this.getSumOfPlaces())/this.getParticipations();
            Double b = ((double)another.getSumOfPlaces())/another.getParticipations();
            if (a.floatValue() != b.floatValue()){
                return a.compareTo(b);
            }
        }
        else {
            Integer a = this.getValue(value2);
            Integer b = another.getValue(value2);
            if (a.intValue() != b.intValue()) {
                if (value2.equals(stBestPlace) || value2.equals(stSumOfPlaces) || value2.equals(stWorstPlace)) {
                    return a.compareTo(b);
                } else {
                    return b.compareTo(a);
                }
            }
        }


        if (value3.equals(stAveragePlace)){
            Double a = ((double)this.getSumOfPlaces())/this.getParticipations();
            Double b = ((double)another.getSumOfPlaces())/another.getParticipations();
            if (a.floatValue() != b.floatValue()){
                return a.compareTo(b);
            }
        }
        else {
            Integer a = this.getValue(value3);
            Integer b = another.getValue(value3);
            if (value3.equals(stBestPlace) || value3.equals(stSumOfPlaces) || value3.equals(stWorstPlace)) {
                return a.compareTo(b);
            } else {
                return b.compareTo(a);
            }
        }
        return 0;
    }
}
