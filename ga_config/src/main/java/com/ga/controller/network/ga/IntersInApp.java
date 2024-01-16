package com.ga.controller.network.ga;

import android.app.Activity;
import android.os.Handler;

import com.ga.controller.controller.callback;
import com.ga.controller.network.adx.IntersGAM;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.AppFlyerAnalytics;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.PurchaseUtils;
import com.ga.controller.utils.WhiteResumeDialog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class IntersInApp {

    public callback mCallBacksInterstitialAds;
    private InterstitialAd interstitialAd;
    private long timeLoad = 0;
    private WhiteResumeDialog mWhiteResumeDialog;
    public boolean isShowing = false;
    private int countShowInter = 0;

    private static IntersInApp mIntersInApp;

    public static IntersInApp getInstance() {
        if (mIntersInApp == null) {
            mIntersInApp = new IntersInApp();
        }
        return mIntersInApp;
    }

    private IntersInApp() {
    }

    public void loadAM(final Activity activity) {
        if (activity != null && InternetUtils.checkInternet(activity)) {
            if (!FirebaseQuery.getEnableAds(activity)) {
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, FirebaseQuery.getIdIntersGA(activity), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(InterstitialAd interstitialAd) {
                                AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                                IntersInApp.this.interstitialAd = interstitialAd;
                                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        IntersInApp.this.interstitialAd = null;
                                        IntersGAM.getInstance().loadIntersGAM(activity);
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        FirebaseQuery.setHomeResume(activity, false);
                                        OpenAdsUtils.getOpenAdsUtils().loadResume = false;
                                        timeLoad = System.currentTimeMillis();
                                        isShowing = false;
                                        try {
                                            if (mWhiteResumeDialog != null) {
                                                mWhiteResumeDialog.dismiss();
                                                mWhiteResumeDialog = null;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        loadAM(activity);
                                        if (mCallBacksInterstitialAds != null) {
                                            mCallBacksInterstitialAds.onChangeScreen();
                                        }
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        isShowing = true;
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_DISPLAYED_IMPRESSION);
                                        isShowing = true;
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                interstitialAd = null;
                                IntersGAM.getInstance().loadIntersGAM(activity);
                            }
                        });
            }
        }
    }

    public void showIntersAdMob(Activity activity, callback onDismissInter) {
        mCallBacksInterstitialAds = onDismissInter;
        if (InternetUtils.checkInternet(activity)) {
            if (!FirebaseQuery.getEnableAds(activity)) {
                if (interstitialAd != null) {
                    showAdmob(activity);

                } else if (IntersGAM.getInstance().mAdManagerInterstitialAd != null) {
                    loadAM(activity);
                    IntersGAM.getInstance().showIntersGAM(activity, onDismissInter);

                } else {
                    loadAM(activity);
                    if (mCallBacksInterstitialAds != null) {
                        mCallBacksInterstitialAds.onChangeScreen();
                    }
                }
            } else {
                if (onDismissInter != null)
                    onDismissInter.onChangeScreen();
            }
        } else {
            if (onDismissInter != null)
                onDismissInter.onChangeScreen();
        }
    }

    private void showAdmob(Activity activity) {
        if (interstitialAd != null) {
            long timeQuery = Long.valueOf(FirebaseQuery.getTimeShowInters(activity));
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - timeLoad;
            if (elapsedTime >= timeQuery) {
                try {
                    if (mWhiteResumeDialog == null) {
                        mWhiteResumeDialog = new WhiteResumeDialog(activity);
                        mWhiteResumeDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_AD_ELIGIBLE);
                try {
                    countShowInter = FirebaseQuery.getCountShow(activity);
                    if (countShowInter >= 0) {
                        countShowInter += 1;
                        FirebaseQuery.setCountShow(activity, countShowInter);
                    }
                    AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_DISPLAYED + countShowInter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (interstitialAd != null) {
                            interstitialAd.show(activity);
                        } else {
                            try {
                                if (mWhiteResumeDialog != null) {
                                    mWhiteResumeDialog.dismiss();
                                    mWhiteResumeDialog = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 500L);
            } else {
                if (mCallBacksInterstitialAds != null) {
                    mCallBacksInterstitialAds.onChangeScreen();
                }
            }
        } else {
            if (mCallBacksInterstitialAds != null) {
                mCallBacksInterstitialAds.onChangeScreen();
            }
        }
    }

    public void loadIntersInScreen(Activity activity) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (InternetUtils.checkInternet(activity) && !FirebaseQuery.getEnableAds(activity)) {
                if (FirebaseQuery.getEnableInters(activity)) {
                    loadAM(activity);
                } else {
                    IntersGAM.getInstance().loadIntersGAM(activity);
                }
            }
        }
    }

    public void showIntersInScreen(Activity activity, callback onDismissInter) {
        if (InternetUtils.checkInternet(activity) && !FirebaseQuery.getEnableAds(activity)) {
            if (!PurchaseUtils.isNoAds(activity)) {
                if (!FirebaseQuery.getHomeResume(activity)) {
                    if (FirebaseQuery.getEnableInters(activity)) {
                        showIntersAdMob(activity, onDismissInter);
                    } else {
                        IntersGAM.getInstance().showIntersGAM(activity, onDismissInter);
                    }
                } else {
                    if (onDismissInter != null) {
                        onDismissInter.onChangeScreen();
                    }
                }
            } else {
                if (onDismissInter != null) {
                    onDismissInter.onChangeScreen();
                }
            }
        } else {
            if (onDismissInter != null) {
                onDismissInter.onChangeScreen();
            }
        }
    }
}
