package com.ga.controller.network.ga;

import android.app.Activity;
import android.os.Handler;

import com.ga.controller.controller.callback;
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

public class InterstitialSplash {

    private static InterstitialSplash interstitialSplash;
    private WhiteResumeDialog mWhiteResumeDialog;

    public static InterstitialSplash getInstance() {
        if (interstitialSplash == null) {
            interstitialSplash = new InterstitialSplash();
        }
        return interstitialSplash;
    }

    private InterstitialSplash() {
    }

    public void loadAnsShowAM(final Activity activity, callback mCallBack) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                if (!FirebaseQuery.getEnableAds(activity)) {
                    FirebaseQuery.setHomeResume(activity, false);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(activity, FirebaseQuery.getIdInterstitialSplash(activity), adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(InterstitialAd interstitialAd) {
                                    AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                                            if (mCallBack != null)
                                                mCallBack.onChangeScreen();
                                        }

                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            try {
                                                if (mWhiteResumeDialog != null) {
                                                    mWhiteResumeDialog.dismiss();
                                                    mWhiteResumeDialog = null;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (mCallBack != null) {
                                                mCallBack.onChangeScreen();
                                            }
                                        }

                                        @Override
                                        public void onAdImpression() {
                                            AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_DISPLAYED_IMPRESSION);
                                        }
                                    });
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
                                            interstitialAd.show(activity);
                                        }
                                    }, 500L);
                                }

                                @Override
                                public void onAdFailedToLoad(LoadAdError loadAdError) {
                                    if (mCallBack != null)
                                        mCallBack.onChangeScreen();
                                }
                            });
                } else {
                    if (mCallBack != null) {
                        mCallBack.onChangeScreen();
                    }
                }
            } else {
                if (mCallBack != null) {
                    mCallBack.onChangeScreen();
                }
            }
        } else {
            if (mCallBack != null) {
                mCallBack.onChangeScreen();
            }
        }
    }
}
