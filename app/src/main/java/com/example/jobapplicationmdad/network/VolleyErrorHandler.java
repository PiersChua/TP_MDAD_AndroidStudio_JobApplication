package com.example.jobapplicationmdad.network;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.jobapplicationmdad.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class VolleyErrorHandler {
    public static Response.ErrorListener newErrorListener(Context context) {
        return volleyError -> {
            String errorMessage;
            if (volleyError.networkResponse != null) {
                try {
                    // Convert byte array response to string
                    String errorBody = new String(volleyError.networkResponse.data, HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    JSONObject errorResponse = new JSONObject(errorBody);
                    errorMessage = errorResponse.getString("message");
                } catch (JSONException | UnsupportedEncodingException e) {
                    errorMessage = "Error parsing error response";
                }
            } else {
                // Handle network or client-side errors
                errorMessage = "Network error: " + volleyError.getMessage();
            }
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_message,null);
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
            tvMessage.setText(errorMessage);
            btnPrimary.setText("OK");
            dialogMessageSpacer.setVisibility(View.GONE);
            btnPrimary.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        };
    }

}