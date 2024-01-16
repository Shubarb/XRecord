package com.ga.controller.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FirstOpenUtils {

    public static final String CONSTANTS_LANGUAGE = "CONSTANTS_LANGUAGE";

    public static void setFirstOpenApp(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(CONSTANTS_LANGUAGE, timeShow);
        edit.apply();
    }

    public static boolean getFirstOpenApp(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(CONSTANTS_LANGUAGE, false);
    }
}
