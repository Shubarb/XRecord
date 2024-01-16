package com.ga.controller.network.adx;

import android.app.Activity;
import android.os.Handler;

import com.ga.controller.controller.callback;
import com.ga.controller.network.ga.IntersInApp;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.PurchaseUtils;
import com.ga.controller.utils.ResumeLoading;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class OpenAdGAM {

    private AppOpenAd appOpenAd = null;
    private boolean isShowingAd = false;
    private long loadTime = 0;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private callback openAdsListener;

    private static OpenAdGAM openAdGAM;
    private boolean isShowOpenResume = false;
    private ResumeLoading mWhiteResumeDialog;

    public static OpenAdGAM getInstance() {
        if (openAdGAM == null) {
            openAdGAM = new OpenAdGAM();
        }
        return openAdGAM;
    }

    private OpenAdGAM() {
    }

    public void fetchAd(Activity activity, callback onListener) {
        if (!FirebaseQuery.getEnableAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                FirebaseQuery.setHomeResume(activity, false);
                openAdsListener = onListener;
                if (InternetUtils.checkInternet(activity) && !PurchaseUtils.isNoAds(activity)) {
                    if (isAdAvailable()) {
                        return;
                    }
                    loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            OpenAdGAM.this.appOpenAd = ad;
                            OpenAdGAM.this.loadTime = (new Date()).getTime();
                            showAdIfAvailable(activity, onListener);
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            if (onListener != null) {
                                onListener.onChangeScreen();
                            }
                        }
                    };
                    AppOpenAd.load(activity, FirebaseQuery.getIdOpenAdAdx(activity), getAdRequest(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
                } else {
                    if (onListener != null) {
                        onListener.onChangeScreen();
                    }
                }
            } else {
                if (onListener != null) {
                    onListener.onChangeScreen();
                }
            }
        } else {
            if (onListener != null) {
                onListener.onChangeScreen();
            }
        }
    }

    public void showAdIfAvailable(Activity activity, callback onListener) {
        if (!FirebaseQuery.getEnableAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                if (!isShowingAd && isAdAvailable()) {
                    FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            appOpenAd = null;
                            isShowingAd = false;
                            try {
                                if (mWhiteResumeDialog != null) {
                                    mWhiteResumeDialog.dismiss();
                                    mWhiteResumeDialog = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (onListener != null) {
                                onListener.onChangeScreen();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            if (onListener != null) {
                                onListener.onChangeScreen();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }

                        @Override
                        public void onAdImpression() {
                        }
                    };
                    appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
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
                                appOpenAd.show(activity);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (mWhiteResumeDialog != null) {
                                    mWhiteResumeDialog.dismiss();
                                    mWhiteResumeDialog = null;
                                }
                            }
                        }
                    }, 500L);

                } else {
                    fetchAd(activity, openAdsListener);
                }
            } else {
                if (onListener != null) {
                    onListener.onChangeScreen();
                }
            }
        } else {
            if (onListener != null) {
                onListener.onChangeScreen();
            }
        }
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void openResumeGAM(Activity activity) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (InternetUtils.checkInternet(activity) && !FirebaseQuery.getEnableAds(activity)) {
                if (FirebaseQuery.getHomeResume(activity)) {
                    if (FirebaseQuery.getResumeAds(activity)) {
                        if (!isShowOpenResume && !IntersInApp.getInstance().isShowing) {
                            loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                                @Override
                                public void onAdLoaded(AppOpenAd ad) {
                                    OpenAdGAM.this.appOpenAd = ad;
                                    OpenAdGAM.this.loadTime = (new Date()).getTime();
                                    FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            isShowOpenResume = false;
                                            appOpenAd = null;
                                            isShowingAd = false;
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
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                                            isShowOpenResume = true;
                                        }

                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            isShowOpenResume = true;
                                            isShowingAd = true;
                                        }
                                    };
                                    appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
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
                                                appOpenAd.show(activity);
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

                                @Override
                                public void onAdFailedToLoad(LoadAdError adError) {
                                    isShowOpenResume = true;
                                }
                            };
                            AppOpenAd.load(activity, FirebaseQuery.getIdOpenAdAdx(activity), getAdRequest(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
                            FirebaseQuery.setHomeResume(activity, false);
                        }
                    }
                }
            }
        }
    }
}
