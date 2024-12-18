package com.example.jobapplicationmdad.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    /**
     *
     * @param datetime The raw datetime returned from db
     * @return Formatted Date
     */
    public static Date convertDateTimeToDate(String datetime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * @param date The date converted from db
     * @return A formatted date in dd/MM//yyyy
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
}
