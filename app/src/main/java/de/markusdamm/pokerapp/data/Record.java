package de.markusdamm.pokerapp.data;

import android.widget.Toast;

import java.util.ArrayList;

import de.markusdamm.pokerapp.utils.Utils;

/**
 * Created by Markus Damm on 08.01.2018.
 */

public class Record {

    private String evening;
    private String player;
    private String value;
    private String type;
    private int position;

    public Record(int position, String evening, String player, String value, String type) {
        this.position = position;
        this.evening = evening;
        this.player = player;
        this.value = value;
        this.type = type;
    }

    public Record(int position, String evening, String value, String type) {
        this(position, evening, null, value, type);
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
        ret.add("Frühester Beginn");
        ret.add("Frühestes Ende");
        ret.add("Weiblichster Abend");
        ret.add("Frühestes Ausscheiden");
        ret.add("Schnellstes Ausscheiden");
        ret.add("Schnellster Letzter");
        ret.add("Multikills");
        ret.add("Meiste Teilnehmer");
        ret.add("Sieger");
        ret.add("Kills");
        ret.add("Tode");
        ret.add("Längste Killstreak");

        return(ret);
    }

    public static String getDBRequest(String kind) {
        if (kind == "Längster Abend") {
            return getLongestEvening();
        }
        if (kind == "Frühester Beginn") {
            return getEarliestStart();
        }
        if (kind == "Frühestes Ende") {
            return getEarliestEnd();
        }
        if (kind == "Weiblichster Abend") {
            return getMostFemaleEvening();
        }
        if (kind == "Frühestes Ausscheiden") {
            return getEarliestDead();
        }
        if (kind == "Schnellstes Ausscheiden") {
            return getFastestDead();
        }
        if (kind == "Meiste Teilnehmer") {
            return getMostParticipants();
        }
        if (kind == "Multikills") {
            return getMultikills();
        }
        if (kind == "Schnellster Letzter") {
            return getFastestLast();
        }
        if (kind == "Sieger") {
            return getWinners();
        }
        if (kind == "Kills") {
            return getKills();
        }
        if (kind == "Tode") {
            return getDeaths();
        }
        if (kind == "Längste Killstreak") {
            return getLongestStreak();
        }
        throw new IllegalArgumentException("Illegal kind in getDBRequest for Record" + kind);
    }

    private static String getLongestEvening() {
        return "SELECT e.name as name, " +
                "((julianday(p.time) - julianday(e.date)) * 24 * 60) AS value " +
                " FROM evenings as e, places as p " +
                " WHERE e.id = p.evening AND p.nr = 1 AND e.name != 'Abend 1' " +
                " order by value desc";
    }

    private static String getEarliestStart() {
        return "SELECT name, strftime('%H:%M',date) as value FROM evenings " +
                "order by value";
    }

    private static String getEarliestEnd() {
        return "SELECT e.name as name, strftime('%H:%M',p.TIME) as value " +
                "FROM evenings as e, places as p " +
                "WHERE e.id = p.evening and p.nr = 1 and e.name != 'Abend 1' " +
                "ORDER BY strftime('%H:%M',DATETIME(p.TIME, '-12 hours'))";
    }

    private static String getMostFemaleEvening() {
        return "select name, females, males, " +
                "females * 100.0 / (males + females) as value " +
                "from ( " +
                "select e.name as name, " +
                "SUM( case when pl.gender = 1 then 1 else 0 end) AS females, " +
                "SUM( case when pl.gender = 0 then 1 else 0 end) AS males " +
                "From evenings as e, places as p, players as pl " +
                "where e.id = p.evening AND pl.id = p.loser " +
                "group by e.id " +
                ") order by value DESC";
    }

    private static String getEarliestDead() {
        return "select e.name as name, pl.name as player, \n" +
                "strftime('%H:%M', p.time) as value\n" +
                "from evenings as e, places as p, players as pl\n" +
                "WHERE e.id = p.evening AND p.loser = pl.id AND e.name != 'Abend 1'\n" +
                "ORDER BY strftime('%H:%M',DATETIME(p.TIME, '-12 hours'))";
    }

    private static String getFastestDead() {
        return "SELECT e.name as name, pl.name as player, \n" +
                "((julianday(p.time) - julianday(e.date)) * 24 * 60) AS value \n" +
                "FROM evenings as e, places as p, players as pl \n" +
                "WHERE e.id = p.evening and pl.id = p.loser AND e.name != 'Abend 1'\n" +
                "ORDER BY value ASC";
    }

    private static String getMostParticipants() {
        return "select e.name as name, count(*) as value " +
                "from places as p, evenings as e " +
                "where p.evening = e.id " +
                "group by e.id " +
                "order by value desc";
    }

    private static String getMultikills() {
        return "SELECT player, name, value\n" +
                "FROM (\n" +
                "SELECT pl.name as player, e.name as name, time, count(*) as value, count(DISTINCT p.winner) as winners\n" +
                "FROM places as p, evenings as e, players as pl\n" +
                "WHERE p.evening = e.id and e.name != 'Abend 1' and p.nr != 1 and pl.id = p.winner\n" +
                "group by time\n" +
                ") WHERE value > 1 and winners = 1\n" +
                "ORDER BY value DESC, player";
    }

    private static String getFastestLast() {
        return "SELECT e.name as name, pl.name as player, \n" +
                "((julianday(p.time) - julianday(e.date)) * 24 * 60) AS value\n" +
                "FROM evenings as e, places as p, players as pl\n" +
                "WHERE e.id = p.evening and pl.id = p.loser AND e.name != 'Abend 1' \n" +
                "AND (p.evening, p.nr) IN (SELECT evening, max(nr) FROM places group by evening)\n" +
                "ORDER BY value ASC";
    }

    private static String getWinners() {
        return "SELECT e.name as name, pl2.name as player, pl.name as value\n" +
                "FROM evenings as e, players as pl, places as p, players as pl2\n" +
                "WHERE e.id = p.evening and p.nr = 2 and p.loser = pl.id and pl2.id = p.winner\n" +
                "order by e.date ASC";
    }

    private static String getKills() {
        return "SELECT count(*) as value, p1.name as player, p2.name as name\n" +
                "FROM places p, players p1, players p2\n" +
                "WHERE p.nr != 1 AND p.evening != 1 AND p1.id = p.winner and p2.id = p.loser\n" +
                "GROUP BY winner, loser\n" +
                "ORDER BY value DESC, player";
    }

    private static String getDeaths() {
        return "SELECT count(*) as value, p1.name as name, p2.name as player\n" +
                "FROM places p, players p1, players p2\n" +
                "WHERE p.nr != 1 AND p.evening != 1 AND p1.id = p.winner and p2.id = p.loser\n" +
                "GROUP BY winner, loser\n" +
                "ORDER BY value DESC, player";
    }

    private static String getLongestStreak() {
        return "with v as (SELECT p.nr as position, e.name as evening, pl.name as player, p.time as time FROM players pl, places p, evenings e\n" +
                "where pl.id = p.winner AND e.id = p.evening),\n" +
                "\n" +
                "w as (SELECT player as name, (select count(*) from v as v2 where v.time > v2.time OR (v.position < v2.position AND v.time = v2.time)) as nummer FROM v\n" +
                "ORDER BY nummer ASC),\n" +
                "\n" +
                "x as (select p1.nummer as Nummer1, p2.nummer as Nummer2, p1.name as name from w p1, w p2 where p2.name == p1.name and p1.nummer < p2.nummer),\n" +
                "y as (select t1.nummer1, t1.nummer2, t1.name from x as t1, x as t2\n" +
                "where (t2.nummer1 > t1.nummer1 and t2.nummer1 < t1.nummer2) and t1.name != t2.name),\n" +
                "z as (select * from x EXCEPT select * from y),\n" +
                "a as (select z2.nummer1, z2.nummer2, z2.name from z as z1, z as z2\n" +
                "where z1.name = z2.name  and (z1.nummer1 != z2.nummer1 or z1.nummer2 != z2.nummer2) \n" +
                "and ((z2.nummer1 > z1.nummer1 and z2.nummer1 < z1.nummer2)\n" +
                "or (z2.nummer2 > z1.nummer1 and z2.nummer2 < z1.nummer2)))\n" +
                "\n" +
                "SELECT name, nummer2-nummer1 + 1 as value FROM (select * from z EXCEPT select * from a) ORDER BY value DESC";
    }

    public static String getType(String kind) {
        if (kind == "Längster Abend") {
            return "minuts";
        }
        if (kind == "Frühester Beginn") {
            return "time";
        }
        if (kind == "Frühestes Ende") {
            return "time";
        }
        if (kind == "Weiblichster Abend") {
            return "percent";
        }
        if (kind == "Frühestes Ausscheiden") {
            return "time+player";
        }
        if (kind == "Schnellstes Ausscheiden") {
            return "minuts+player";
        }
        if (kind == "Meiste Teilnehmer") {
            return "number";
        }
        if (kind == "Multikills") {
            return "number+player";
        }
        if (kind == "Schnellster Letzter") {
            return "minuts+player";
        }
        if (kind == "Sieger") {
            return "number+player";
        }
        if (kind == "Kills") {
            return "number+player";
        }
        if (kind == "Tode") {
            return "number+player";
        }
        if (kind == "Längste Killstreak") {
            return "number";
        }
        throw new IllegalArgumentException("Illegal kind for type in Records");
    }

    @Override
    public String toString() {
        if (this.type == "time") {
            return position + ": " + evening + " (" + value + ")";
        }

        if (this.type == "minuts") {
            return position + ": " + evening + " (" + value + " Minuten | " + Utils.formatTimeToString(Integer.parseInt(value)) + ")";
        }

        if (this.type == "percent") {
            return position + ": " + evening + " (" + (Math.round(Double.parseDouble(value) * 100)) / 100.0 + " %)";
        }

        if (this.type == "number") {
            return position + ": " + evening + " (" + value + ")";
        }

        if (this.type == "time+player") {
            return position + ": " + player + " (" + evening + " | " + value + ")";
        }

        if (this.type == "minuts+player") {
            return position + ": " + player + " (" + evening + " | " + value + " Minuten | " + Utils.formatTimeToString(Integer.parseInt(value)) + ")";
        }

        if (this.type == "number+player") {
            return position + ": " + player + " (" + evening + " | " + value + ")";
        }

        return "Illegal type for record: " + this.type;
    }
}
