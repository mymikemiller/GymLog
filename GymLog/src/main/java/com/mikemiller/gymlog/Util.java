package com.mikemiller.gymlog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Mike on 7/5/2014. COPYRIGHT OLIO.
 */
public class Util {
    public static Calendar getMostRecentMondayFrom(long dateInMillis) {
        Calendar referenceDay =  new GregorianCalendar();
        referenceDay.setFirstDayOfWeek(Calendar.MONDAY);
        referenceDay.setTimeInMillis(dateInMillis);

        Calendar calendar = new GregorianCalendar();
        // Clear the calendar since the default is the current time
        calendar.clear();
        // Directly set year and week of year
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.YEAR, referenceDay.get(Calendar.YEAR));
        calendar.set(Calendar.WEEK_OF_YEAR, referenceDay.get(Calendar.WEEK_OF_YEAR));
        // Start date for the week
        //Date startDate = calendar.getTime();

        //c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Move the date to the previous Monday

        return calendar;
    }

    public static Calendar getMostRecentMonday() {
        return getMostRecentMondayFrom(Calendar.getInstance().getTimeInMillis());
    }
}
