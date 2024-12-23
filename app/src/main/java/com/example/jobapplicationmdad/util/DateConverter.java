package com.example.jobapplicationmdad.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    /**
     *
     * @param milliseconds The date in milliseconds
     * @return A formatted date in dd/MM/yyyy
     */
    public static String formatDateFromMilliseconds(Long milliseconds){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(milliseconds);
        return dateFormat.format(date);
    }
    /**
     *
     * @param date The date in dd/MM/yyyy
     * @return A date in milliseconds
     */
    public static long formatDateToMilliseconds(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try{
            Date formattedDate = dateFormat.parse(date);
            assert formattedDate != null;
            return formattedDate.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the date from date picker to the correct format accepted by SQL
     * @param date The date in dd/MM/yyyy
     * @return A formatted date in yyyy/MM/dd
     */
    public static String formatDateForSql(String date){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        try {
            Date inputDate = inputDateFormat.parse(date);
            assert inputDate != null;
            return outputDateFormat.format(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the date from sql to the correct format represented on the date picker
     * @param date The date in yyyy-MM-dd
     * @return A formatted date in dd/MM/yyyy
     */
    public static String formatDateFromSql(String date) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date inputDate = inputDateFormat.parse(date);
            assert inputDate != null;
            return outputDateFormat.format(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
