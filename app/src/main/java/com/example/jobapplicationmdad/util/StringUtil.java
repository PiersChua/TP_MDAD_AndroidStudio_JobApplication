package com.example.jobapplicationmdad.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * @param name The input name
     * @return The first 2 initials of the given name
     */
    public static String getNameInitials(String name) {
        StringBuilder initials = new StringBuilder();
        if (name.trim().isEmpty()) {
            return "";
        }
        String[] nameArray = name.trim().split("\\s+");
        for (int i = 0; i < Math.min(2, nameArray.length); i++) {
            initials.append(nameArray[i].charAt(0));

        }
        return initials.toString().toUpperCase();
    }

    public static String generateOTP(){
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    /**
     *  Limit the number of digits on the left and right of the decimal
     */

    public static class DecimalDigitsInputFilter implements InputFilter {

        private final Pattern mPattern;

        public DecimalDigitsInputFilter(int places, int precision) {
            mPattern = Pattern.compile(
                    "^\\d{0," + places + "}(\\.\\d{0," + precision + "})?$"
            );
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String resultingText = dest.subSequence(0, dstart)
                    + source.toString()
                    + dest.subSequence(dend, dest.length());

            Matcher matcher = mPattern.matcher(resultingText);
            if (!matcher.matches()) {
                return "";
            }
            return null;
        }
    }
}
