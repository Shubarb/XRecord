package com.ga.controller.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FBTracking {

    public static final String EVENT_FEATURE = "event_feature_";

    public static void funcTrackingFunction(Context context, String nameEvent, String value) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("action_click", value);
        mFirebaseAnalytics.logEvent(nameEvent, bundle);
    }

    public static void funcTrackingFunction(Context context, String nameEvent) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(nameEvent, null);
    }
}
