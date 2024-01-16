package com.ga.controller.application;

import android.app.Application;
import com.ga.controller.utils.AppFlyerAnalytics;

public class UniApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppFlyerAnalytics.initAppsflyer(this);
    }
}
