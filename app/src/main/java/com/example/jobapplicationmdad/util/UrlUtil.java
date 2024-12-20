package com.example.jobapplicationmdad.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

public class UrlUtil {
    public static String constructUrl(String baseUrl, Map<String, String> params) {
        // delimiter for separating the queries in the url
        StringJoiner queryJoiner = new StringJoiner("&");
        try {
            // Loops through the map
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                queryJoiner.add(encodedKey + "=" + encodedValue);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return baseUrl + "?" + queryJoiner;
    }
}
