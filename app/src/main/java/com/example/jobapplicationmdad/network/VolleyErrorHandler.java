package com.example.jobapplicationmdad.network;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class VolleyErrorHandler {
    public static Response.ErrorListener newErrorListener(Context context, View anchorView) {
        return volleyError -> {
            if (volleyError.networkResponse != null) {
                try {
                    // Convert byte array response to string
                    String errorBody = new String(volleyError.networkResponse.data, HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    JSONObject errorResponse = new JSONObject(errorBody);
                    String errorMessage = errorResponse.getString("message");
                    Snackbar.make(anchorView, "Error: " + errorMessage, Snackbar.LENGTH_SHORT).show();
                } catch (JSONException | UnsupportedEncodingException e) {
                    Snackbar.make(anchorView, "Error parsing error response", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                // Handle network or client-side errors
                Snackbar.make(anchorView, "Network error: " + volleyError.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };
    }
    public static Response.ErrorListener newErrorListener(Context context) {
        return volleyError -> {
            if (volleyError.networkResponse != null) {
                try {
                    // Convert byte array response to string
                    String errorBody = new String(volleyError.networkResponse.data, HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    JSONObject errorResponse = new JSONObject(errorBody);
                    String errorMessage = errorResponse.getString("message");
                    Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } catch (JSONException | UnsupportedEncodingException e) {
                    Toast.makeText(context, "Error parsing error response", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle network or client-side errors
                Toast.makeText(context, "Network error: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}