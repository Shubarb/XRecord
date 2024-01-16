package com.ga.controller.network.ga;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;

import com.ga.controller.controller.callback;
import com.ga.controller.network.adx.OpenAdGAM;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.AppFlyerAnalytics;
import com.ga.controller.utils.HomeUtils;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.PurchaseUtils;
import com.ga.controller.utils.ResumeLoading;
import com.ga.controller.utils.WhiteResumeDialog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class OpenAdsUtils {

    private AppOpenAd mAppOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback appOpenAdLoadCallback;
    public long loadTime;
    private callback mCallBack;
    private WhiteResumeDialog mWhiteOpenDialog;
    private boolean isShowing = false;
    private boolean isTimeOut = false;
    public boolean loadResume = false;
    public boolean isShowResume = false;
    private static OpenAdsUtils openAdsUtils;

    public static OpenAdsUtils getOpenAdsUtils() {
        if (openAdsUtils == null) {
            openAdsUtils = new OpenAdsUtils();
        }
        return openAdsUtils;
    }

    private OpenAdsUtils() {
    }

    public void loadAndShow(final Activity activity, callback onDismissOpen) {
        mCallBack = onDismissOpen;
        if (!isAdAvailable()) {
            timeOut(onDismissOpen);
            FirebaseQuery.setHomeResume(activity, false);
            AppOpenAd.AppOpenAdLoadCallback mAppOpenAdLoadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd appOpenAd) {
                    AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                    isShowing = true;
                    isTimeOut = true;
                    appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            if (onDismissOpen != null)
                                OpenAdGAM.getInstance().fetchAd(activity, onDismissOpen);
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            isShowing = false;
                            try {
                                if (mWhiteOpenDialog != null) {
                                    mWhiteOpenDialog.dismiss();
                                    mWhiteOpenDialog = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (mCallBack != null)
                                mCallBack.onChangeScreen();
                        }

                        @Override
                        public void onAdImpression() {
                            AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_DISPLAYED_IMPRESSION);
                        }
                    });
                    try {
                        if (mWhiteOpenDialog == null) {
                            mWhiteOpenDialog = new WhiteResumeDialog(activity);
                            mWhiteOpenDialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (appOpenAd != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                appOpenAd.show(activity);
                            }
                        }, 500L);
                    } else {
                        if (mCallBack != null)
                            mCallBack.onChangeScreen();
                    }
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    isShowing = true;
                    isTimeOut = true;
                    if (onDismissOpen != null)
                        OpenAdGAM.getInstance().fetchAd(activity, onDismissOpen);
                }
            };
            AppOpenAd.load(activity, FirebaseQuery.getIdStartOpenAds(activity), getAdRequest(), 1, mAppOpenAdLoadCallback);
        } else {
            if (mCallBack != null)
                mCallBack.onChangeScreen();
        }
    }

    private void timeOut(callback callBack) {
        CountDownTimer countDownTimer = new CountDownTimer(18000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!isTimeOut) {
                    if (callBack != null) {
                        callBack.onChangeScreen();
                        cancel();
                    }
                }
            }
        };
        countDownTimer.start();
    }

    private boolean wasLoadTimeLessThanNHoursAgo() {
        return new Date().getTime() - loadTime < 14400000;
    }

    private boolean isAdAvailable() {
        return mAppOpenAd != null && wasLoadTimeLessThanNHoursAgo();
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private ResumeLoading mWhiteResumeDialog;
    private boolean isRepeatResume = false;

    public void loadAndShowResume(final Activity activity) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                if (!FirebaseQuery.getEnableAds(activity)) {
                    if (FirebaseQuery.getHomeResume(activity)) {
                        if (FirebaseQuery.getResumeAds(activity)) {
                            if (!IntersInApp.getInstance().isShowing && !RewardUtils.getInstance().isShowing) {
                                if (!isRepeatResume) {
                                    appOpenAdLoadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(AppOpenAd appOpenAd) {
                                            mAppOpenAd = appOpenAd;
                                            AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                                            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                @Override
                                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                                    loadResume = false;
                                                    mAppOpenAd = null;
                                                    OpenAdGAM.getInstance().openResumeGAM(activity);
                                                }

                                                @Override
                                                public void onAdShowedFullScreenContent() {
                                                    isShowResume = true;
                                                }

                                                @Override
                                                public void onAdDismissedFullScreenContent() {
                                                    isRepeatResume = false;
                                                    isShowResume = false;
                                                    loadResume = false;
                                                    FirebaseQuery.setHomeResume(activity, false);
                                                    try {
                                                        if (mWhiteResumeDialog != null) {
                                                            mWhiteResumeDialog.dismiss();
                                                            mWhiteResumeDialog = null;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onAdImpression() {
                                                    isShowResume = true;
                                                    AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_DISPLAYED_IMPRESSION);
                                                }
                                            });

                                            if (mAppOpenAd != null) {
                                                try {
                                                    if (mWhiteResumeDialog == null) {
                                                        mWhiteResumeDialog = new ResumeLoading(activity);
                                                        mWhiteResumeDialog.show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            mAppOpenAd.show(activity);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            if (mWhiteResumeDialog != null) {
                                                                mWhiteResumeDialog.dismiss();
                                                                mWhiteResumeDialog = null;
                                                            }
                                                        }
                                                    }
                                                }, 500L);
                                            }
                                        }

                                        @Override
                                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                                            loadResume = false;
                                            mAppOpenAd = null;
                                            OpenAdGAM.getInstance().openResumeGAM(activity);
                                        }
                                    };
                                    AppOpenAd.load(activity, FirebaseQuery.getIdOpenResume(activity), getAdRequest(), 1, appOpenAdLoadCallback);
                                    isRepeatResume = true;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void showOpenAds(Activity activity, callback onDismissOpen) {
        FirebaseQuery.setHomeResume(activity, false);
        if (InternetUtils.checkInternet(activity)) {
            if (FirebaseQuery.getEnableOpenAds(activity)) {
                if (!FirebaseQuery.getEnableAds(activity)) {
                    if (!PurchaseUtils.isNoAds(activity)) {
                        if (!isShowing) {
                            loadAndShow(activity, onDismissOpen);
                            isShowing = true;
                        } else {
                            if (onDismissOpen != null) {
                                onDismissOpen.onChangeScreen();
                            }
                        }
                    } else {
                        if (onDismissOpen != null) {
                            onDismissOpen.onChangeScreen();
                        }
                    }
                } else {
                    if (onDismissOpen != null) {
                        onDismissOpen.onChangeScreen();
                    }
                }
            } else {
                if (onDismissOpen != null) {
                    onDismissOpen.onChangeScreen();
                }
            }
        } else {
            if (onDismissOpen != null) {
                onDismissOpen.onChangeScreen();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                FirebaseQuery.setHomeResume((Activity) context, true);
                ResumeAds.getResumeAds().loadAds(context);
            }

            if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                ResumeAds.getResumeAds().showAdsResume((Activity) context);
            }
        }
    };

    public void resScreen(Activity activity) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        activity.registerReceiver(mReceiver, intentFilter);
    }

    public void onResume(Activity activity) {
        loadResume = false;
        HomeUtils homeUtils = new HomeUtils(activity);
        homeUtils.setOnHomePressedListener(new HomeUtils.OnHomeResumeListener() {
            @Override
            public void onHomeLongPressed() {
                FirebaseQuery.setHomeResume(activity, true);
            }

            @Override
            public void onHomePressed() {
            }
        });
        HomeUtils.startHome();
        OpenAdsUtils.getOpenAdsUtils().resScreen(activity);
    }
}
