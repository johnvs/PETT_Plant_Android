package com.biotronisis.pettplant.util;

import java.util.Calendar;

public class Time {

    public static String getCurrentTime() {
        Calendar now = Calendar.getInstance();
        int hours = now.get(Calendar.HOUR_OF_DAY);
        int minutes = now.get(Calendar.MINUTE);
        int seconds = now.get(Calendar.SECOND);
        int mills = now.get(Calendar.MILLISECOND);
        String currentTime = String.format("Current time: %s:%s:%s.%s",
              hours, minutes, seconds, mills);
        return currentTime;
    }
}
