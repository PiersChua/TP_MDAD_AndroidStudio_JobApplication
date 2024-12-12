package com.example.jobapplicationmdad.util;

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
}
