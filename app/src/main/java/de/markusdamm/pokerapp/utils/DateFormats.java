package de.markusdamm.pokerapp.utils;

import java.text.SimpleDateFormat;

/**
 * Created by Markus Damm on 29.03.2015.
 */
public class DateFormats {
    public static SimpleDateFormat getDataBaseFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public static SimpleDateFormat getGermanDay(){
        return new SimpleDateFormat("dd.MM.yyyy");
    }

    public static SimpleDateFormat getGermanTime(){
        return new SimpleDateFormat("HH:mm");
    }

    public static SimpleDateFormat getGermanDayAndTime(){
        return new SimpleDateFormat("dd.MM.yyyy HH:mm");
    }
}
