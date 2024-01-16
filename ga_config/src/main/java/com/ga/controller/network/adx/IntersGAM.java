package com.ga.controller.network.adx;

import android.app.Activity;
import android.os.Handler;

import com.ga.controller.controller.callback;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.AppFlyerAnalytics;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.WhiteResumeDialog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

public class IntersGAM {

    private static IntersGAM mIntersGAM;

    public static IntersGAM getInstance() {
        if (mIntersGAM == null) {
            mIntersGAM = new IntersGAM();
        }
        return mIntersGAM;
    }

    private IntersGAM() {
    }

    public AdManagerInterstitialAd mAdManagerInterstitialAd;
    private callback mCallBacksInterstitialAds;
    private WhiteResumeDialog mWhiteResumeDialog;

    public void loadIntersGAM(Activity activity) {
        if (!FirebaseQuery.getEnableAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                AdManagerInterstitialAdLoadCallback callback = new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AdManagerInterstitialAd adManagerInterstitialAd) {
                        mAdManagerInterstitialAd = adManagerInterstitialAd;
                        adManagerInterstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        timeLoad = System.currentTimeMillis();
                                        try {
                                            if (mWhiteResumeDialog != null) {
                                                mWhiteResumeDialog.dismiss();
                                                mWhiteResumeDialog = null;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (mCallBacksInterstitialAds != null) {
                                            mCallBacksInterstitialAds.onChangeScreen();
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        mAdManagerInterstitialAd = null;
                                    }

                                    @Override
                                    public void onAdImpression() {
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mAdManagerInterstitialAd = null;
                    }
                };
                AdManagerInterstitialAd.load(activity, FirebaseQuery.getIdIntersAdAdx(activity), adRequest, callback);
            }
        }
    }

    private long timeLoad = 0;
    private long timeQuery = 0;

    public void showIntersGAM(Activity activity, callback callBacksInterstitialAds) {
        if (!FirebaseQuery.getEnableAds(activity)) {
            mCallBacksInterstitialAds = callBacksInterstitialAds;
            if (InternetUtils.checkInternet(activity)) {
                try {
                    timeQuery = Long.valueOf(FirebaseQuery.getTimeShowInters(activity));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    timeQuery = 10000;
                }
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - timeLoad;
                if (elapsedTime >= timeQuery) {
                    AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_AD_ELIGIBLE);
                    if (mAdManagerInterstitialAd != null) {
                        try {
                            if (mWhiteResumeDialog == null) {
                                mWhiteResumeDialog = new WhiteResumeDialog(activity);
                                mWhiteResumeDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mAdManagerInterstitialAd != null) {
                                    mAdManagerInterstitialAd.show(activity);
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
                        if (callBacksInterstitialAds != null) {
                            callBacksInterstitialAds.onChangeScreen();
                        }
                    }
                } else {
                    if (callBacksInterstitialAds != null) {
                        callBacksInterstitialAds.onChangeScreen();
                    }
                }
            } else {
                if (callBacksInterstitialAds != null) {
                    callBacksInterstitialAds.onChangeScreen();
                }
            }
        } else {
            if (callBacksInterstitialAds != null) {
                callBacksInterstitialAds.onChangeScreen();
            }
        }
    }
}
