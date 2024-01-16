package com.ga.controller.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PurchaseUtils {

    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi5zloDqlaDB1uGOGVvszpKbPobkihd4SpLU0aaaZp3vxrSsXVwjo9UjtkiAeCU+06Hby43hsiRfMkQcESeFx4klqnhkFDMVT1YpUkVi0FUYO/rzolfrNjlEBif4bgwSwvGXBOwnC26jKpNXrWEsTw26iUFdwO6+w/+6U/8rS+L7J0NxG3DPJHUtFincuTlcfLW18sJADO/xCCuKpwrw0WvAS+6nOcdA7Zd0rWcboTa0zmZygDWMEwcvMKAeF/ShZrE4XDVyT0A0XFwEfVS/YueqQ+T7N/qbPZUoM4R2HVhJh6mXBPueO6KOY5WLPhSdsd3F4HNCFTmTqcNMCni9olQIDAQAB";
    private static final String PURCHASE_UTILS = "PURCHASE_UTILS";
    private static final String ID_REMOVE_ADS = "ID_REMOVE_ADS";
    private static final String ID_NO_SUB = "ID_NO_SUB";

    public static String getIdRemoveAds() {
        return "android.test.purchased";
//        return "remove_ads"; // Store
    }

    public static String getIdWeek() {
        return "sub_week";
    }

    public static String getIdMonth() {
//        return "sub_month";
        return "android.test.purchased";
    }

    public static String getIdTrialYear() {
//        return "sub_trial_3day_year";
        return "android.test.purchased";
    }

    public static String getIdYear() {
//        return "sub_trial_3day_year";
        return "android.test.purchased";
    }

    public static String getIdOneTime() {
//        return "sub_one_time";
        return "android.test.purchased";
    }

    public static boolean isNoAds(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        return preferences.getBoolean(ID_REMOVE_ADS, false);
    }

    public static void setNoAds(Context mContext, boolean isPurchase) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ID_REMOVE_ADS, isPurchase);
        editor.apply();
    }

    public static boolean isNoSub(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        return preferences.getBoolean(ID_NO_SUB, false);
    }

    public static void setNoSub(Context mContext, boolean isPurchase) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ID_NO_SUB, isPurchase);
        editor.apply();
    }
}
