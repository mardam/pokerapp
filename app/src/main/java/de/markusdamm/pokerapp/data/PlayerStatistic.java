package de.markusdamm.pokerapp.data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import de.markusdamm.pokerapp.utils.Utils;

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
    private int mostKills;
    private double median;
    private double sd;
    private double normalizedMean;
    private double average;
    private List<String> killed = new ArrayList<>();
    private int mostDeaths;
    private List<String> killers = new ArrayList<>();
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
    public final static String stMostKills = "Häufigste getötete Gegner";
    public final static String stMostDeaths = "Am häufigsten getötet von";
    public final static String stSD = "Standardabweichung";
    public final static String stMedian = "Median";
    public final static String stNormalizedMean = "Normalisierter Durchschnitt über Teilnehmerzahl";


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

    public int getMostKills() {
        return mostKills;
    }

    public int getMostDeaths() {
        return mostDeaths;
    }

    public String getMostKilled() {
        String ret = "";
        for (String kill: killed) {
            if (ret == "") {
                ret = kill;
            } else {
                ret = ret + ", " + kill;
            }
        }
        return ret;
    }

    public String getMostKillers() {
        String ret = "";
        for (String kill: killers) {
            if (ret == "") {
                ret = kill;
            } else {
                ret = ret + ", " + kill;
            }
        }
        return ret;
    }

    public double getMedian() {
        return median;
    }

    public double getSd() {
        return sd;
    }

    public double getAverage() {
        return average;
    }

    public double getNormalizedMean() {
        return normalizedMean;
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


    public void setMostKills(Pair<Integer, List<String>> value) {
        this.mostKills = value.first;
        this.killers = value.second;
    }

    public void setMostDeaths(Pair<Integer, List<String>> value) {
        this.mostDeaths = value.first;
        this.killed = value.second;
    }


    public void setValues(String value1, String value2, String value3){
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    private String buildMostKillers() {
        if (mostKills == -1) {
            return stMostKills + ": niemand";
        }

        return stMostKills + ": " + mostKills + " Mal: " + getMostKillers();
    }

    private String buildMostKilled() {
        if (mostDeaths == -1) {
            return stMostDeaths + ": niemanden";
        }

        return stMostDeaths + ": " + mostDeaths + " Mal von " + getMostKilled();
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public void setSd(double sd) {
        this.sd = sd;
    }

    public void setNormalizedMean(double normalizedMean) {
        this.normalizedMean = normalizedMean;
    }

    public void setAverage(double average) {
        this.average = average;
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
        statisticList.add(stMinuits + ": " + minuits + " Minuten bzw. " + Utils.formatTimeToString(minuits));
        statisticList.add(stBeatenPlayers + ": " + beatenPlayers);
        statisticList.add(stNumberOfOponents + ": " + participators);
        statisticList.add(stSumOfPlaces + ": " + sumOfPlaces);
        statisticList.add(stWorsePlayer + ": " + Integer.toString(participators - sumOfPlaces));
        statisticList.add(stAveragePlace + ": " + average);
        statisticList.add(stNormalizedMean + ": " + normalizedMean);
        statisticList.add(stMedian + ": " + median);
        statisticList.add(stSD + ": " + sd);
        statisticList.add(stMultikills + ": " + multikills);
        statisticList.add(buildMostKillers());
        statisticList.add(buildMostKilled());

        return statisticList;
    }



    public <T extends Number> T getValue(String value){
        switch (value){
            case stBestPlace:
                return (T) Integer.valueOf(getBestPlace());
            case stWins:
                return (T) Integer.valueOf(getWins());
            case stMinuits:
                return (T) Integer.valueOf(getMinuits());
            case stBeatenPlayers:
                return (T) Integer.valueOf(getBeatenPlayers());
            case stNumberOfOponents:
                return (T) Integer.valueOf(getParticipators());
            case stSumOfPlaces:
                return (T) Integer.valueOf(getSumOfPlaces());
            case stWorsePlayer:
                return (T) Integer.valueOf(getParticipators() - getSumOfPlaces());
            case stParticipations:
                return (T) Integer.valueOf(getParticipations());
            case stAveragePlace:
                return (T) Double.valueOf(getAverage());
            case stMultikills:
                return (T) Integer.valueOf(getMultikills());
            case stHeadUps:
                return (T) Integer.valueOf(getHeadUps());
            case stPodiums:
                return (T) Integer.valueOf(getPodiums());
            case stWorstPlace:
                return (T) Integer.valueOf(getWorstPlace());
            case stLastPlaces:
                return (T) Integer.valueOf(getLastPlaces());
            case stMostKills:
                return (T) Integer.valueOf(getMostKills());
            case stMostDeaths:
                return (T) Integer.valueOf(getMostDeaths());
            case stMedian:
                return (T) Double.valueOf(getMedian());
            case stSD:
                return (T) Double.valueOf(getSd());
            case stNormalizedMean:
                return (T) Double.valueOf(getNormalizedMean());
            default:
                return (T) Integer.valueOf(-1);
        }
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
        strings.add(stMedian);
        strings.add(stSD);
        strings.add(stNormalizedMean);
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
        ret.add(stSD);
        ret.add(stMedian);
        ret.add(stAveragePlace);
        ret.add(stNormalizedMean);

        return ret;
    }

    public List<String> floatingValues() {
        List<String> ret = new ArrayList<>();
        ret.add(stSD);
        ret.add(stMedian);
        ret.add(stAveragePlace);
        ret.add(stNormalizedMean);
        return ret;
    }

    private int compareNumbers(Double x, Double y, String value) {
        if (inverseToSort().contains(value)) {
            return x.compareTo(y);
        } else {
            return y.compareTo(x);
        }
    }

    @Override
    public int compareTo(PlayerStatistic another) {
        Double x = this.getValue(value1).doubleValue();
        Double y = another.getValue(value1).doubleValue();

        if (x.doubleValue() != y.doubleValue()) {
            return compareNumbers(x, y, value1);
        }

        x = this.getValue(value2).doubleValue();
        y = another.getValue(value2).doubleValue();

        if (x.doubleValue() != y.doubleValue()) {
            return compareNumbers(x, y, value2);
        }

        x = this.getValue(value3).doubleValue();
        y = another.getValue(value3).doubleValue();

        return compareNumbers(x, y, value3);
    }
}
