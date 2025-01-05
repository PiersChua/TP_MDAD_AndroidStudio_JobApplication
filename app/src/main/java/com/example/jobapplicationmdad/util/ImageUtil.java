package com.example.jobapplicationmdad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.jobapplicationmdad.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageUtil {
    public static String encodeBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String base64Image){
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }
        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes,0, imageBytes.length);
    }

    public static void startCrop(Uri sourceUri, Context context, Fragment fragment) {
        Uri destinationUri = Uri.fromFile(new File(context.getCacheDir(), "croppedImage.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(context, R.color.primary));
        // Toolbar font color
        options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.background));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(context, R.color.primary));
        options.setAspectRatioOptions(0,
                new AspectRatio("1:1", 1, 1));
        options.setCircleDimmedLayer(true);
        options.withMaxResultSize(300,300);
        options.setShowCropGrid(false);
        options.setShowCropFrame(false);
        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(context, fragment);
    }

}
