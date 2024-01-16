package com.ga.controller.network.adx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.InternetUtils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdView;

import java.util.UUID;

public class BannerGAM {

    private static AdManagerAdView adView;

    private static BannerGAM mBannerGAM;

    public static BannerGAM getInstance() {
        if (mBannerGAM == null) {
            mBannerGAM = new BannerGAM();
        }
        return mBannerGAM;
    }

    private BannerGAM() {
    }

    public void showBannerGAM(Activity activity, LinearLayout lnBannerGAM) {
        if (!FirebaseQuery.getEnableAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                adView = new AdManagerAdView(activity);
                adView.setAdSizes(AdSize.BANNER);
                adView.setAdUnitId(FirebaseQuery.getIdBannerAdx(activity));
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        lnBannerGAM.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdOpened() {
                    }

                    @Override
                    public void onAdLoaded() {
                        try {
                            lnBannerGAM.removeAllViews();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        lnBannerGAM.addView(adView);
                    }

                    @Override
                    public void onAdClicked() {
                    }

                    @Override
                    public void onAdImpression() {
                    }
                });
                adView.loadAd(getCollapsibleRequest());
            } else {
                lnBannerGAM.setVisibility(View.GONE);
            }
        } else {
            lnBannerGAM.setVisibility(View.GONE);
        }
    }

    public static AdRequest getCollapsibleRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        extras.putString("collapsible_request_id", UUID.randomUUID().toString());
        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        return builder.build();
    }
}
