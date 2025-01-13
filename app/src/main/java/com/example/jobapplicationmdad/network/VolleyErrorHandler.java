package com.example.jobapplicationmdad.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class VolleyErrorHandler {
    public static Response.ErrorListener newErrorListener(Context context) {
        return volleyError -> {
            String errorMessage;
            int statusCode = 0;
            if (volleyError.networkResponse != null) {
                try {
                    // Convert byte array response to string
                    statusCode = volleyError.networkResponse.statusCode;
                    String errorBody = new String(volleyError.networkResponse.data, HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    JSONObject errorResponse = new JSONObject(errorBody);
                    errorMessage = errorResponse.getString("message");
                } catch (JSONException | UnsupportedEncodingException e) {
                    errorMessage = "Error parsing error response";
                }
            } else {
                if (!isInternetAvailable(context)) {
                    // Show a dialog for no internet connection
                    displayDialog(context, false, "No internet connection. Please check your network and try again.");
                    return;
                }
                // Handle network or client-side errors
                errorMessage = "Network error: " + "Something went wrong, please try again later";
            }
            boolean requireLogin = false;
            String dialogMessage = errorMessage;
            // Check if the session has expired
            if (errorMessage.equals("Token expired") && statusCode == 401) {
                dialogMessage = "\"Oops! Looks like your session has timed out\\nPlease log in again";
                clearSharedPreferences(context);
                requireLogin = true;
            }
            // Check if the user's account exists in db
            else if (errorMessage.equals("User does not exist in database") && statusCode == 403) {
                dialogMessage = "Oops! We couldn't verify your account credentials\nPlease log in again";
                clearSharedPreferences(context);
                requireLogin = true;
            }
            displayDialog(context,requireLogin,dialogMessage);

        };
    }
    private static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }

    private static void clearSharedPreferences(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    private static void displayDialog(Context context, boolean requireLogin, String dialogMessage) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_message, null);
        ImageView ivIcon = dialogView.findViewById(R.id.ivDialogIcon);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        View dialogMessageSpacer = dialogView.findViewById(R.id.dialogMessageSpacer);
        Button btnOutline = dialogView.findViewById(R.id.btnDialogOutline);
        btnOutline.setVisibility(View.GONE);
        Button btnPrimary = dialogView.findViewById(R.id.btnDialogPrimary);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        ivIcon.setImageResource(R.drawable.ic_error);
        tvTitle.setText("Error");
        tvMessage.setText(dialogMessage);

        btnPrimary.setText("OK");
        dialogMessageSpacer.setVisibility(View.GONE);
        btnPrimary.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (requireLogin) {
                    Intent i = new Intent(context, LoginActivity.class);
                    // Clear the fragment back stack
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);
                }
            }
        });
        dialog.show();
    }


}