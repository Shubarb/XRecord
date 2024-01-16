package com.ga.controller.utils;

import android.content.Context;

import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;

public class AppFlyerAnalytics {

    public static final String AF_INTERS_AD_ELIGIBLE = "af_inters_ad_eligible";
    public static final String AF_INTERS_API_CALLED = "af_inters_api_called";
    public static final String AF_INTERS_DISPLAYED = "af_inters_displayed_";
    public static final String AF_INTERS_DISPLAYED_IMPRESSION = "af_inters_displayed";

    public static void appFlyerTracking(Context context, String nameEvent, String nameAction, String value) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put(nameAction, value);
        AppsFlyerLib.getInstance().logEvent(context, nameEvent, eventValue);
    }

    public static void appFlyerTracking(Context context, String nameEvent) {
        Map<String, Object> eventValue = new HashMap<>();
        AppsFlyerLib.getInstance().logEvent(context, nameEvent, eventValue);
    }

    public static void initAppsflyer(Context context) {
        AppsFlyerLib appsFlyerLib = AppsFlyerLib.getInstance();
        appsFlyerLib.init("Np5W3ykoo4v6PJHPquZwPm", null, context);
        appsFlyerLib.start(context);
    }
}
