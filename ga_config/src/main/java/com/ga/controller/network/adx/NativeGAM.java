package com.ga.controller.network.adx;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ga.controller.R;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.InternetUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class NativeGAM {

    private static NativeGAM mNativeGAM;

    public static NativeGAM getInstance() {
        if (mNativeGAM == null) {
            mNativeGAM = new NativeGAM();
        }
        return mNativeGAM;
    }

    private NativeGAM() {
    }

    private int getIdLayout(int position) {
        int layout;
        if (position == 0) {
            layout = R.layout.native_small_ga;
        } else if (position == 1) {
            layout = R.layout.native_big_ga;
        } else {
            layout = R.layout.native_center;
        }
        return layout;
    }

    public void showNativeGAM(Activity activity, LinearLayout lnNative, int position) {
        if (!FirebaseQuery.getEnableAds(activity)) {
            if (InternetUtils.checkInternet(activity)) {
                AdLoader.Builder builder = new AdLoader.Builder(activity, FirebaseQuery.getIdNativeAdx(activity));
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(getIdLayout(position), null);
                        populateNativeAdView(nativeAd, adView);
                        lnNative.removeAllViews();
                        lnNative.addView(adView);
                    }
                });

                builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        lnNative.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdOpened() {
                    }

                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdClicked() {
                    }

                    @Override
                    public void onAdImpression() {
                    }
                });

                VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                builder.withNativeAdOptions(adOptions);
                AdLoader adLoader = builder.withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                lnNative.setVisibility(View.GONE);
                            }
                        })
                        .build();

                adLoader.loadAd(new AdManagerAdRequest.Builder().build());
            }
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
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
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
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }
}
