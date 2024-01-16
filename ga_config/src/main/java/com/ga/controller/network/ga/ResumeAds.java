package com.ga.controller.network.ga;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.ga.controller.network.adx.OpenAdGAM;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.AppFlyerAnalytics;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.PurchaseUtils;
import com.ga.controller.utils.ResumeLoading;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class ResumeAds {

    private static ResumeAds resumeAds;

    public static ResumeAds getResumeAds() {
        if (resumeAds == null) {
            resumeAds = new ResumeAds();
        }
        return resumeAds;
    }

    private ResumeAds() {
    }

    private AppOpenAd.AppOpenAdLoadCallback appOpenAdLoadCallback;
    private ResumeLoading mWhiteResumeDialog;
    private boolean isShowOpenResume = false;
    private AppOpenAd mAppOpenAd = null;

    public void loadAds(final Context context) {
        if (!PurchaseUtils.isNoAds(context)) {
            appOpenAdLoadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd appOpenAd) {
                    mAppOpenAd = appOpenAd;
                    AppFlyerAnalytics.appFlyerTracking(context, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                    appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            isShowOpenResume = true;
                            mAppOpenAd = null;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            isShowOpenResume = false;
                            FirebaseQuery.setHomeResume(context, false);
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
                            AppFlyerAnalytics.appFlyerTracking(context, AppFlyerAnalytics.AF_INTERS_DISPLAYED_IMPRESSION);
                            isShowOpenResume = true;
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mAppOpenAd = null;
                    isShowOpenResume = true;
                }
            };
            AppOpenAd.load(context, FirebaseQuery.getIdOpenResume(context), getAdRequest(), 1, appOpenAdLoadCallback);
        }
    }

    public void showAdsResume(Activity activity) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (InternetUtils.checkInternet(activity) && !FirebaseQuery.getEnableAds(activity)) {
                if (FirebaseQuery.getHomeResume(activity)) {
                    if (FirebaseQuery.getResumeAds(activity)) {
                        if (!isShowOpenResume && !IntersInApp.getInstance().isShowing) {
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
                                        if (mAppOpenAd != null)
                                            mAppOpenAd.show(activity);
                                    }
                                }, 500L);
                            } else {
                                OpenAdGAM.getInstance().openResumeGAM(activity);
                            }
                            FirebaseQuery.setHomeResume(activity, false);
                        }
                    }
                }
            }
        }
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }
}
