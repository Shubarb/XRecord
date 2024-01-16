package com.ga.controller.network.ga;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.ga.controller.R;
import com.ga.controller.network.adx.BannerGAM;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.AppFlyerAnalytics;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.PurchaseUtils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.UUID;

public class BannerInApp {

    private static BannerInApp bannerInApp;

    public static BannerInApp getInstance() {
        if (bannerInApp == null) {
            bannerInApp = new BannerInApp();
        }
        return bannerInApp;
    }

    private BannerInApp() {
    }

    public void initBannerGA(final Activity activity, final LinearLayout lnBannerAdMob, String idBanner) {
        ShimmerFrameLayout slBanner = activity.findViewById(R.id.shimmer_banner);
        if (InternetUtils.checkInternet(activity)) {
            if (!FirebaseQuery.getEnableAds(activity)) {
                AdView mAdView = new AdView(activity);
                if (FirebaseQuery.getBannerCollapsible(activity)) {
                    mAdView.setAdSize(getAdSize(activity, lnBannerAdMob));
                } else {
                    mAdView.setAdSize(AdSize.BANNER);
                }
                mAdView.setAdUnitId(idBanner);
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                        lnBannerAdMob.setVisibility(ViewGroup.VISIBLE);
                        slBanner.setVisibility(ViewGroup.GONE);
                        try {
                            lnBannerAdMob.removeAllViews();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        lnBannerAdMob.addView(mAdView);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        BannerGAM.getInstance().showBannerGAM(activity, lnBannerAdMob);
                        slBanner.setVisibility(ViewGroup.GONE);
                    }
                });
                if (FirebaseQuery.getBannerCollapsible(activity)) {
                    mAdView.loadAd(getCollapsibleRequest());
                } else {
                    mAdView.loadAd(new AdRequest.Builder().build());
                }
            } else {
                slBanner.setVisibility(ViewGroup.GONE);
                lnBannerAdMob.setVisibility(View.GONE);
            }
        } else {
            slBanner.setVisibility(ViewGroup.GONE);
            lnBannerAdMob.setVisibility(View.GONE);
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

    private AdSize getAdSize(Activity activity, LinearLayout lnBanner) {
        try {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float density = outMetrics.density;

            float adWidthPixels = lnBanner.getWidth();

            if (adWidthPixels == 0) {
                adWidthPixels = outMetrics.widthPixels;
            }

            int adWidth = (int) (adWidthPixels / density);
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
        } catch (Exception e) {
            return AdSize.FULL_BANNER;
        }
    }

    public void showBanner(Activity activity, LinearLayout lnBanner, String idBanner) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (!FirebaseQuery.getEnableAds(activity)) {
                if (FirebaseQuery.getEnableBanner(activity)) {
                    initBannerGA(activity, lnBanner, idBanner);
                } else {
                    BannerGAM.getInstance().showBannerGAM(activity, lnBanner);
                }
            } else {
                if (lnBanner != null) {
                    lnBanner.setVisibility(View.GONE);
                }
            }
        } else {
            if (lnBanner != null) {
                lnBanner.setVisibility(View.GONE);
            }
        }
    }
}
