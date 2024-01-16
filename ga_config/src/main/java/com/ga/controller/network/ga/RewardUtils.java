package com.ga.controller.network.ga;

import android.app.Activity;

import com.ga.controller.controller.callback;
import com.ga.controller.query.FirebaseQuery;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class RewardUtils {

    private static RewardUtils rewardUtils;
    private RewardedAd rewardedAd;
    private callback myCallback;
    public boolean isShowing = false;

    public static RewardUtils getInstance() {
        if (rewardUtils == null) {
            rewardUtils = new RewardUtils();
        }
        return rewardUtils;
    }

    private RewardUtils() {
    }

    public void loadReward(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, FirebaseQuery.getIdReward(activity),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(RewardedAd ad) {
                        rewardedAd = ad;
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                isShowing = false;
                                loadReward(activity);
                                if (myCallback != null) {
                                    myCallback.onChangeScreen();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                            }

                            @Override
                            public void onAdImpression() {
                                isShowing = true;
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isShowing = true;
                            }
                        });
                    }
                });
    }

    public void showReward(Activity activity, callback callback) {
        if (rewardedAd != null) {
            rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(RewardItem rewardItem) {
                    myCallback = callback;
                }
            });
        } else {
            loadReward(activity);
        }
    }
}
