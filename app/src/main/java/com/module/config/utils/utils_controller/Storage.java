package com.module.config.utils.utils_controller;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.module.config.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Storage {
    public static final String DIRECTORY_TRIM = "trim";
    public static final String DIRECTORY_GIF = "gif";
    public static final String DIRECTORY_SCREEN_RECORD = "screen record";
    public static final String DIRECTORY_SCREEN_SHOT = "screen shot";

    public static String getDstVideoTrim(Context context) {
        File directory = new File(context.getFilesDir(), DIRECTORY_TRIM);
        String videoName = "Video_" + Calendar.getInstance().getTime().getTime() + ".mp4";
        File mFile = new File(directory, videoName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mFile.getAbsolutePath();
    }

    public static String getDstScreenRecord(Context context) {
        File directory = new File(context.getFilesDir(), DIRECTORY_SCREEN_RECORD);
        String videoName = "Video_" + Calendar.getInstance().getTime().getTime() + ".mp4";
        File mFile = new File(directory, videoName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mFile.getAbsolutePath();
    }

    public static String getDstGif(Context context, String name) {
        File directory = new File(context.getFilesDir(), DIRECTORY_GIF);
        File mFile = new File(directory, name);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mFile.getAbsolutePath();
    }

    public static String getDstScreenShot(Context context) {
        File directory = new File(context.getFilesDir(), DIRECTORY_SCREEN_SHOT);
        String videoName = "ScreenShot_" + Calendar.getInstance().getTime().getTime() + ".png";
        File mFile = new File(directory, videoName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mFile.getAbsolutePath();
    }

    public static File getFolder(Context context, String directory) {
        File mFile = new File(context.getFilesDir(), directory);
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        return mFile;
    }

    public static Uri getUriFromPath(Context context, File file) {
        return DocumentFile.fromFile(file).getUri();
    }

    public static String saveGif(Context context, byte[] bytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveGifAboveAndroid10(context, bytes);
        } else {
            return saveGifBelowAndroid10(context, bytes);
        }
    }

    private static String saveGifBelowAndroid10(Context context, byte[] bytes) {
        File mediaDirectory = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !mediaDirectory.isDirectory()) {
            mediaDirectory.mkdirs();
        }
        String fileName = "GIF_" + Calendar.getInstance().getTime().getTime() + ".gif";
        File mediaFile = new File(mediaDirectory, fileName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mediaFile));
            bos.write(bytes);
            bos.flush();
            bos.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), mediaFile.getAbsolutePath(), mediaFile.getName(), mediaFile.getName());
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(mediaFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaFile.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String saveGifAboveAndroid10(Context context, byte[] bytes) {
        String filename = "GIF_" + Calendar.getInstance().getTime().getTime() + ".gif";
        Uri gifUri;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/gif");
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 1);
        contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + context.getString(
                        R.string.app_name
                )
        );
        gifUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + context.getString(
                R.string.app_name
        ) + File.separator + filename);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mediaFile));
            bos.write(bytes);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        contentValues.clear();
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
        context.getContentResolver().update(gifUri, contentValues, null, null);
        return mediaFile.getAbsolutePath();
    }

    public static String saveImage(Context context, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveImageAboveAndroid10(context, bitmap);
        } else {
            return saveImageBelowAndroid10(context, bitmap);
        }
    }

    private static String saveImageBelowAndroid10(Context context, Bitmap bitmap) {
        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !mediaDirectory.isDirectory()) {
            mediaDirectory.mkdirs();
        }
        String fileName = "IMG_" + Calendar.getInstance().getTime().getTime() + ".jpg";
        File mediaFile = new File(mediaDirectory, fileName);
        try {
            OutputStream fileOutputStream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), mediaFile.getAbsolutePath(), mediaFile.getName(), mediaFile.getName());
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(mediaFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaFile.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String saveImageAboveAndroid10(Context context, Bitmap bitmap) {
        String filename = "IMG_" + Calendar.getInstance().getTime().getTime() + ".jpg";
        OutputStream outputStream;
        Uri imageUri;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + context.getString(
                        R.string.app_name
                )
        );
        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            outputStream = context.getContentResolver().openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        contentValues.clear();
        context.getContentResolver().update(imageUri, contentValues, null, null);
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + context.getString(
                R.string.app_name
        ) + File.separator + filename;
    }

    public static File[] getFilesImageInStorage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getFilesImageAboveAndroid10(context);
        } else {
            return getFilesImageBelowAndroid10(context);
        }
    }

    private static File[] getFilesImageBelowAndroid10(Context context) {
        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
        if (mediaDirectory.exists()) {
            return mediaDirectory.listFiles();
        }
        return null;
    }

    @SuppressLint("Range")
    private static File[] getFilesImageAboveAndroid10(Context context) {
        String uri = MediaStore.Images.Media.DATA;
        String condition = uri + " like '%/Pictures/" + context.getString(R.string.app_name) + "/%'";
        String[] projection = {uri, MediaStore.Images.Media.DATE_ADDED};
        List<File> lstFile = new ArrayList<>();
        try {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    condition, null, null);
            if (cursor != null) {
                boolean isDataPresent = cursor.moveToFirst();
                if (isDataPresent) {
                    do {
                        lstFile.add(new File(cursor.getString(cursor.getColumnIndex(uri))));
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        File[] lstOut = new File[lstFile.size()];
        for (int i = 0; i < lstFile.size(); i++) {
            lstOut[i] = lstFile.get(i);
        }
        return lstOut;
    }

    public static VideoValue saveVideo(Context context, boolean isVideoTrim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveVideoAboveAndroid10(context,isVideoTrim);
        } else {
            return saveVideoBelowAndroid10(context,isVideoTrim);
        }
    }

    public static VideoValue saveVideoBelowAndroid10(Context context, boolean isVideoTrim) {
        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), context.getString(R.string.app_name));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !mediaDirectory.isDirectory()) {
            mediaDirectory.mkdirs();
        }
        String fileName;
        if (isVideoTrim) {
            fileName = "edited_" + new SimpleDateFormat(PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, Config.itemsFileNameFomart[0].getValue())).format(new Date()) + ".mp4";
        } else {
            fileName = PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_PREFIX, "recording") + "_" + new SimpleDateFormat(PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, Config.itemsFileNameFomart[0].getValue())).format(new Date()) + ".mp4";
        }
        File mediaFile = new File(mediaDirectory, fileName);
        try {
            mediaFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        VideoValue videoValue = new VideoValue();
        videoValue.setPath(mediaFile.getAbsolutePath());
        return videoValue;
    }

    public static VideoValue saveVideoAboveAndroid10(Context context, boolean isVideoTrim) {
        String fileName;
        if (isVideoTrim) {
            fileName = "edited_" + new SimpleDateFormat(PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, Config.itemsFileNameFomart[0].getValue())).format(new Date()) + ".mp4";
        } else {
            fileName = PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_PREFIX, "recording") + "_" + new SimpleDateFormat(PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_FOMART, Config.itemsFileNameFomart[0].getValue())).format(new Date()) + ".mp4";
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + File.separator + context.getString(R.string.app_name));
        contentValues.put(MediaStore.Video.Media.TITLE, fileName);
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 1);
        Uri mUri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        VideoValue videoValue = new VideoValue();
        videoValue.setContentValues(contentValues);
        videoValue.setPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + context.getString(
                R.string.app_name
        ) + File.separator + fileName);
        videoValue.setUri(mUri);
        return videoValue;
    }

    public static String getDirectoryFileVideoInStorage(Context context) {
        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), context.getString(R.string.app_name));
        if (mediaDirectory.exists()) {
            return mediaDirectory.getAbsolutePath();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void updateGalleryBelow10(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[]{path}, null,
                (path1, uri) -> {

                });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void updateGalleryAbove10(Context context, ContentValues contentValues, Uri uri) {
        try {
            contentValues.clear();
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
            context.getContentResolver().update(uri, contentValues, null, null);
        } catch (Exception ignored) {

        }
    }

    public static class VideoValue {
        private String path;
        private Uri uri;
        private ContentValues contentValues;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public ContentValues getContentValues() {
            return contentValues;
        }

        public void setContentValues(ContentValues contentValues) {
            this.contentValues = contentValues;
        }
    }
}
