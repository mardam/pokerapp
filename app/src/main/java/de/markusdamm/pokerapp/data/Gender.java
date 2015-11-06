package de.markusdamm.pokerapp.data;

/**
 * Created by Markus Damm on 30.03.2015.
 */
public class Gender{
    public final static boolean FEMALE = true;
    public final static boolean MALE = false;
    public final static int FEMALE_INT = 1;
    public final static int MALE_INT = 0;
    public final static int BOTH_INT = 2;
    public final static String MALE_STRING = "männlich";
    public final static String FEMALE_STRING = "weiblich";
    public final static String BOTH_STRING = "beide";


    public static int getIntByString(String st){
        switch (st){
            case MALE_STRING:
                return MALE_INT;
            case FEMALE_STRING:
                return FEMALE_INT;
            case BOTH_STRING:
                return BOTH_INT;
            default:
                throw new NullPointerException();
        }

    }

    public static int toInt(boolean b){
        if (b == Gender.MALE){
            return MALE_INT;
        }
        return FEMALE_INT;
    }

    public static boolean toBool(int i){
        if (i == Gender.MALE_INT){
            return MALE;
        }
        return FEMALE;
    }

    public static String getString(boolean b){
        if (b == MALE){
            return MALE_STRING;
        }
        return FEMALE_STRING;
    }

    public static String getStringByInt(int i){
        if (i == MALE_INT){
            return MALE_STRING;
        }
        else{
            if (i == FEMALE_INT){
                return FEMALE_STRING;
            }
            else{
                return BOTH_STRING;
            }
        }
    }

}
