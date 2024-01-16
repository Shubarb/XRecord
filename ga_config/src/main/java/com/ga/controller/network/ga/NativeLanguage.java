package com.ga.controller.network.ga;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.ga.controller.R;
import com.ga.controller.network.adx.NativeGAM;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.AppFlyerAnalytics;
import com.ga.controller.utils.InternetUtils;
import com.ga.controller.utils.PurchaseUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class NativeLanguage {

    private static NativeLanguage nativeLanguage;

    public static NativeLanguage getInstance() {
        if (nativeLanguage == null) {
            nativeLanguage = new NativeLanguage();
        }
        return nativeLanguage;
    }

    private NativeLanguage() {
    }

    public void showNativeLanguage(final Activity activity, LinearLayout lnNative) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (!FirebaseQuery.getEnableAds(activity)) {
                if (FirebaseQuery.getEnableNative(activity)) {
                    loadAndShow(activity, lnNative);
                } else {
                    NativeGAM.getInstance().showNativeGAM(activity, lnNative, 1);
                }
            } else {
                if (lnNative != null) {
                    lnNative.setVisibility(View.GONE);
                }
            }
        } else {
            if (lnNative != null) {
                lnNative.setVisibility(View.GONE);
            }
        }
    }

    private void loadAndShow(final Activity activity, LinearLayout lnNative) {
        try {
            if (!PurchaseUtils.isNoAds(activity)) {
                if (!FirebaseQuery.getEnableAds(activity)) {
                    if (InternetUtils.checkInternet(activity)) {
                        ShimmerFrameLayout slNative = activity.findViewById(R.id.shimmer_native);
                        AdLoader.Builder builder = new AdLoader.Builder(activity, FirebaseQuery.getIdNativeFirst(activity));
                        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd nativeAd) {
                                AppFlyerAnalytics.appFlyerTracking(activity, AppFlyerAnalytics.AF_INTERS_API_CALLED);
                                slNative.setVisibility(View.GONE);
                                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.native_big_ga, null);
                                populateNativeAdView(nativeAd, nativeAdView);
                                lnNative.removeAllViews();
                                lnNative.addView(nativeAdView);
                            }
                        });

                        AdLoader adLoader = builder.withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                slNative.setVisibility(View.GONE);
                                NativeGAM.getInstance().showNativeGAM(activity, lnNative, 1);
                            }
                        }).build();

                        VideoOptions videoOptions = new VideoOptions.Builder()
                                .build();

                        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                                .setVideoOptions(videoOptions)
                                .build();

                        builder.withNativeAdOptions(adOptions);
                        adLoader.loadAd(new AdRequest.Builder().build());
                    }
                }
            } else {
                if (lnNative != null) {
                    lnNative.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        VideoController vc = nativeAd.getMediaContent().getVideoController();

        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }
}
