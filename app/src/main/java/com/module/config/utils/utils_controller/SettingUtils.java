package com.module.config.utils.utils_controller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import java.io.File;

public class SettingUtils {

    public static void funcPolicy(Context mContext, String linkPolicy) {
        try {
            mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(linkPolicy)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No Open Policy", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void funcShareFile(Activity mContext, String path) {
        File file = new File(path);
        Uri data = androidx.core.content.FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setDataAndType(data, "video/mp4");
        shareIntent.putExtra(Intent.EXTRA_STREAM, data);
        mContext.startActivity(Intent.createChooser(shareIntent, "Share video using"));
    }

    public static void deleteVideo(Activity activity, String pathVideo) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            new File(pathVideo).delete();
            activity.sendBroadcast(new Intent(Constants.UPDATE_VIDEO_MAIN));
            MediaScannerConnection.scanFile(activity,
                    new String[]{pathVideo}, new String[]{"video/mp4"},
                    (path1, uri) -> {
                        activity.finish();
                    });
        } else {
            MediaScannerConnection.scanFile(activity,
                    new String[]{pathVideo}, new String[]{"video/mp4"},
                    (path1, uri) -> {
                        if (uri != null) {
                            if (activity.getContentResolver().delete(uri, null, null) != -1) {
                                activity.sendBroadcast(new Intent(Constants.UPDATE_VIDEO_MAIN));
                            }
                            activity.finish();
                        }
                    });
        }
    }
}
