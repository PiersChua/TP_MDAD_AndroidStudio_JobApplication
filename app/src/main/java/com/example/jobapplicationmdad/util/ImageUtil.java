package com.example.jobapplicationmdad.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.jobapplicationmdad.R;
import com.google.android.material.snackbar.Snackbar;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil {
    /**
     * Encodes a bitmap to base64 string
     * Used when sending image data to server
     *
     * @param bitmap The corresponding image
     * @return A base64 encoded string of the image
     */
    public static String encodeBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Decodes a base64 string to a bitmap
     * Used when retrieving image data from server
     *
     * @param base64Image The base64 encoded string of the image
     * @return A bitmap that can be used to display the image
     */
    public static Bitmap decodeBase64(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }
        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /**
     * Initialises UCrop with the relevant configurations(options)
     * The aspect ratio is defaulted to 1:1 for a squared image, that will be displayed in a circular card
     *
     * @param sourceUri
     * @param context
     * @param fragment
     */
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
        options.withMaxResultSize(200, 200);
        options.setShowCropGrid(false);
        options.setShowCropFrame(false);
        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(context, fragment);
    }

    public static String dispatchTakePictureIntent(Fragment fragment,Activity activity, Context context, int captureCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            Object[] resultObject = null;
            File photoFile = null;
            try {
                resultObject = createImageFile(activity);
                photoFile = (File) resultObject[0];
            } catch (IOException ex) {
                Snackbar.make(activity.findViewById(android.R.id.content), "Unable to create temp file", Snackbar.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                fragment.startActivityForResult(takePictureIntent, captureCode);
                return (String) resultObject[1];
            }

        }
        return null;
    }

    private static Object[] createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SgJobMarket_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return new Object[]{image, currentPhotoPath};
    }

}
