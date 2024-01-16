package com.ga.controller.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class HomeUtils {

    private static Context mContext;
    private static IntentFilter mFilter;
    private static ResumeReceiver mReceiver;
    public OnHomeResumeListener mListener;

    class ResumeReceiver extends BroadcastReceiver {

        ResumeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String stringExtra = intent.getStringExtra("reason");
                if (stringExtra != null && HomeUtils.this.mListener != null) {
                    if (stringExtra.equals("homekey")) {
                        HomeUtils.this.mListener.onHomePressed();
                    } else if (stringExtra.equals("recentapps")) {
                        HomeUtils.this.mListener.onHomeLongPressed();
                    }
                }
            }
        }
    }

    public interface OnHomeResumeListener {
        void onHomeLongPressed();

        void onHomePressed();
    }

    public HomeUtils(Context context) {
        mContext = context;
        mFilter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
    }

    public void setOnHomePressedListener(OnHomeResumeListener onHomeResumeListener) {
        this.mListener = onHomeResumeListener;
        mReceiver = new ResumeReceiver();
    }

    public static void startHome() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    public static void stopHome() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}
