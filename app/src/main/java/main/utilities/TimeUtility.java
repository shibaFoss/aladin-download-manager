package main.utilities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

/**
 * TimeUtility class helps us to calculate the time and date related problem.
 * This class is used as functional class since all the methods of this class is static.
 * <p/>
 * Created by shibaprasad on 10/16/2014.
 */
@SuppressWarnings("UnusedDeclaration")
public class TimeUtility implements Serializable {

    /**
     * Unused constructor.
     *
     * @deprecated
     */
    private TimeUtility() {
    }

    /**
     * Get the current date
     *
     * @return String of the current date.
     */
    public static String getCurrentDate() {
        String result;
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE); //date.
        int month = calendar.get(Calendar.MONTH); //month.
        int year = calendar.get(Calendar.YEAR); //year.
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        result = "" + date + " " + monthName + " " + year;
        return result;
    }

    public static String calculateTime(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        if (hour == 0)
            return minute + ":" + second + "";
        if (minute == 0)
            return second + ":";
        else
            return hour + ":" + minute + ":" + second + "";
    }

}






















