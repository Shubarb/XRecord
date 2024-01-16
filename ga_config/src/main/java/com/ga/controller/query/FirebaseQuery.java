package com.ga.controller.query;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ga.controller.R;
import com.ga.controller.controller.callback;
import com.ga.controller.network.ga.InterstitialSplash;
import com.ga.controller.network.ga.OpenAdsUtils;
import com.ga.controller.network.model.MoreAppObj;
import com.ga.controller.network.model.UpdateObj;
import com.ga.controller.utils.EndCodeUtils;
import com.ga.controller.utils.InternetUtils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FirebaseQuery {

    public static final String COUNT_SHOW_INTERS = "COUNT_SHOW_INTERS";
    public static final String TESTING_OPEN = "testing_open";
    public static final String CHECK_COUNTRY = "check_country";
    public static final String ENABLE_ADS = "premium";
    public static final String RESUME_ADS = "resume_ads";
    public static final String BANNER_COLLAPSIBLE = "banner_collapsible";
    public static final String RATING_POPUP = "rating_popup";
    public static final String ENABLE_OPEN_ADS = "enable_open_ads";
    public static final String TIME_SHOW_INTERS = "time_show_inters";
    public static final String ID_INTERSTITIAL_SPLASH = "id_inters_splash";
    public static final String ID_NATIVE_FIRST = "id_native_first";
    public static final String ID_NATIVE_ON_BOARD = "id_native_on_board";
    public static final String ID_START_OPEN = "start_open";
    public static final String SHOW_ON_BOARD = "isShowOnBoard";
    public static final String ID_RESUME_OPEN = "resume_open";
    public static final String ID_BANNER_ADS = "banner_in_app";
    public static final String ID_NATIVE_ADS = "native_in_app";
    public static final String ID_INTERSTITIAL_ADS = "inters_in_app";
    public static final String ID_REWARD_ADS = "id_reward";
    public static final String ID_HOME_RESUME = "home_click";

    public static final String ENABLE_BANNER = "enable_banner_ga";
    public static final String ENABLE_NATIVE = "enable_native_ga";
    public static final String ENABLE_INTERS = "enable_inters_ga";

    public static final String CONSTANTS_MORE_APP = "ma";
    public static final String CONSTANTS_UPDATE = "update";

    public static final String ID_BANNER_ADX = "banner_gam";
    public static final String ID_NATIVE_ADX = "native_gam";
    public static final String ID_OPEN_AD_ADX = "open_ads_gam";
    public static final String ID_INTERS_ADX = "inters_gam";

    private FirebaseRemoteConfig remoteConfig;

    private static FirebaseQuery configController;

    public ArrayList<MoreAppObj> listMoreApp = new ArrayList<>();
    public ArrayList<UpdateObj> mListUpdateApp = new ArrayList<>();

    public static FirebaseQuery getConfigController() {
        if (configController == null) {
            configController = new FirebaseQuery();
        }
        return configController;
    }

    private FirebaseQuery() {
    }

    public void initFirebase(Activity activity, callback callback) {
        if (InternetUtils.checkInternet(activity)) {
            remoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings
                    .Builder()
                    .build();
            remoteConfig.setConfigSettingsAsync(settings);
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            remoteConfig.fetchAndActivate()
                    .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                boolean isUK = remoteConfig.getBoolean(CHECK_COUNTRY);
                                if (isUK) {
                                    startShowForm(remoteConfig, activity, callback);
                                } else {
                                    queryData(remoteConfig, activity, callback);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            if (callback != null) {
                                callback.onChangeScreen();
                            }
                            e.printStackTrace();
                        }
                    });
        } else {
            if (callback != null) {
                callback.onChangeScreen();
            }
        }
    }

    private ConsentInformation consentInformation;

    private String getDeviceID() {
        ArrayList<String> listDevice = new ArrayList<>();
//        listDevice.add("b5f96baa-e858-4132-bf4e-8bb8ce2a0b7e");
        listDevice.add("C5B630DE9D99C4CB265CAF4FD6D1AFD6");
        return listDevice.get(0);
    }

    private void startShowForm(FirebaseRemoteConfig firebaseRemoteConfig, Activity activity, callback callback) {
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(getDeviceID())
                .build();

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//                .setConsentDebugSettings(debugSettings) // Lên store comment dòng này
                .setAdMobAppId("ca-app-pub-1919652342336147~3899695480") // Lên Store thay lại app_id
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            activity,
                            loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                }
                                if (consentInformation.canRequestAds()) {
                                    queryData(firebaseRemoteConfig, activity, callback);
                                }
                            }
                    );
                },
                requestConsentError -> {
                    FirebaseQuery.setEnableAds(activity, true);
                    if (callback != null) {
                        callback.onChangeScreen();
                    }
                });
    }

    private void queryData(FirebaseRemoteConfig firebaseRemoteConfig, Activity activity, callback callback) {
        boolean isTestingOpen = firebaseRemoteConfig.getBoolean(TESTING_OPEN);

        boolean isPremium = firebaseRemoteConfig.getBoolean(ENABLE_ADS);
        setEnableAds(activity, isPremium);

        boolean enableOpenAds = firebaseRemoteConfig.getBoolean(ENABLE_OPEN_ADS);
        setEnableOpenAds(activity, enableOpenAds);

        boolean enableBanner = firebaseRemoteConfig.getBoolean(ENABLE_BANNER);
        setEnableBanner(activity, enableBanner);

        boolean enableNative = firebaseRemoteConfig.getBoolean(ENABLE_NATIVE);
        setEnableNative(activity, enableNative);

        boolean isShowOnBoard = firebaseRemoteConfig.getBoolean(SHOW_ON_BOARD);
        setShowOnBoard(activity, isShowOnBoard);

        boolean enableInters = firebaseRemoteConfig.getBoolean(ENABLE_INTERS);
        setEnableInters(activity, enableInters);

        boolean resumeAds = firebaseRemoteConfig.getBoolean(RESUME_ADS);
        setEnableResume(activity, resumeAds);

        boolean banner_collapsible = firebaseRemoteConfig.getBoolean(BANNER_COLLAPSIBLE);
        setBannerCollapsible(activity, banner_collapsible);

        boolean isRatingPopup = firebaseRemoteConfig.getBoolean(RATING_POPUP);
        setRatingPopup(activity, isRatingPopup);

        String idOpenStartID = firebaseRemoteConfig.getString(ID_START_OPEN);
        setIdStartOpenAds(activity, idOpenStartID);

        String idOpenResumeID = firebaseRemoteConfig.getString(ID_RESUME_OPEN);
        setIdOpenResume(activity, idOpenResumeID);

        String idIntersSplash = firebaseRemoteConfig.getString(ID_INTERSTITIAL_SPLASH);
        setIdInterstitialSplash(activity, idIntersSplash);

        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if (isTestingOpen) {
                    OpenAdsUtils.getOpenAdsUtils().showOpenAds(activity, callback);
                } else {
                    InterstitialSplash.getInstance().loadAnsShowAM(activity, callback);
                }
            }
        });

        String timeShowInters = firebaseRemoteConfig.getString(TIME_SHOW_INTERS);
        setTimeShowInters(activity, timeShowInters);

        String idNativeFirst = firebaseRemoteConfig.getString(ID_NATIVE_FIRST);
        setIdNativeFirst(activity, idNativeFirst);

        String idNativeOnBoard = firebaseRemoteConfig.getString(ID_NATIVE_ON_BOARD);
        setIdNativeOnBoard(activity, idNativeOnBoard);

        String idBannerAds = firebaseRemoteConfig.getString(ID_BANNER_ADS);
        setIdBannerGA(activity, idBannerAds);

        String idNativeAds = firebaseRemoteConfig.getString(ID_NATIVE_ADS);
        setIdNativeGA(activity, idNativeAds);

        String idInterAds = firebaseRemoteConfig.getString(ID_INTERSTITIAL_ADS);
        setIdIntersGA(activity, idInterAds);

        String idReward = firebaseRemoteConfig.getString(ID_REWARD_ADS);
        setIdReward(activity, idReward);

        String idOpenAdx = firebaseRemoteConfig.getString(ID_OPEN_AD_ADX);
        setIdOpenAdAdx(activity, idOpenAdx);

        String idInterAdx = firebaseRemoteConfig.getString(ID_INTERS_ADX);
        setIdIntersAdx(activity, idInterAdx);

        String idNativeAdx = firebaseRemoteConfig.getString(ID_NATIVE_ADX);
        setIdNativeAdx(activity, idNativeAdx);

        String idBannerAdx = firebaseRemoteConfig.getString(ID_BANNER_ADX);
        setIdBannerAdx(activity, idBannerAdx);

        String ma = firebaseRemoteConfig.getString(CONSTANTS_MORE_APP);
        String update = firebaseRemoteConfig.getString(CONSTANTS_UPDATE);
        parseMoreApp(ma, update);
    }

    private void parseMoreApp(String moreApp, String updateApp) {
        try {
            listMoreApp.clear();
            String decodeMoreApp = EndCodeUtils.decode(moreApp);
            JSONArray arrayMoreApp = new JSONObject(decodeMoreApp).getJSONArray("apps");
            int size = arrayMoreApp.length();
            int i = 0;
            while (i < size) {
                JSONObject jsonObject = arrayMoreApp.getJSONObject(i);
                MoreAppObj moreAppML = new MoreAppObj(jsonObject.getString("pkm"),
                        jsonObject.getString("title"),
                        jsonObject.getString("des"),
                        jsonObject.getString("cover"),
                        jsonObject.getString("logo"));
                listMoreApp.add(moreAppML);
                i++;
            }

            String decodeUpdate = EndCodeUtils.decode(updateApp);
            JSONObject jsonUpdate = new JSONObject(decodeUpdate);
            UpdateObj updateApps = new UpdateObj();
            updateApps.setTitle(jsonUpdate.getString("title"));
            updateApps.setDes(jsonUpdate.getString("des"));
            updateApps.setStatus(jsonUpdate.getInt("status"));
            updateApps.setUrl(jsonUpdate.getString("url"));
            updateApps.setForce(jsonUpdate.getBoolean("force"));
            updateApps.setVersionCode(jsonUpdate.getInt("version_code"));
            updateApps.setVersionName(jsonUpdate.getString("version_name"));
            updateApps.setNumberShow(jsonUpdate.getInt("number_show"));
            updateApps.setLinkBanner(jsonUpdate.getString("banner_update"));
            mListUpdateApp.add(updateApps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setEnableAds(Context activity, boolean position) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(ENABLE_ADS, position);
        edit.apply();
    }

    public static boolean getEnableAds(Context activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(ENABLE_ADS, false);
    }

    public static void setTimeShowInters(Activity activity, String timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putString(TIME_SHOW_INTERS, timeShow);
        edit.apply();
    }

    public static String getTimeShowInters(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getString(TIME_SHOW_INTERS, "10000");
    }

    private static void setIdInterstitialSplash(Activity mActivity, String idAds) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_INTERSTITIAL_SPLASH, idAds);
        editor.apply();
    }

    public static String getIdInterstitialSplash(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_INTERSTITIAL_SPLASH, "");
    }

    public static void setIdNativeFirst(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_NATIVE_FIRST, enabelIS);
        editor.apply();
    }

    public static String getIdNativeFirst(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_NATIVE_FIRST, "");
    }

    public static void setIdNativeOnBoard(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_NATIVE_ON_BOARD, enabelIS);
        editor.apply();
    }

    public static String getIdNativeOnBoard(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_NATIVE_ON_BOARD, "");
    }

    public static void setIdStartOpenAds(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_START_OPEN, enabelIS);
        editor.apply();
    }

    public static String getIdStartOpenAds(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_START_OPEN, "");
    }

    private static void setShowOnBoard(Activity mActivity, boolean showOb) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putBoolean(SHOW_ON_BOARD, showOb);
        editor.apply();
    }

    public static boolean getShowOnBoard(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getBoolean(SHOW_ON_BOARD, false);
    }

    public static void setIdOpenResume(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_RESUME_OPEN, enabelIS);
        editor.apply();
    }

    public static String getIdOpenResume(Context mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_RESUME_OPEN, "ca-app-pub-3940256099942544/3419835294");
    }

    public static void setIdBannerGA(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_BANNER_ADS, enabelIS);
        editor.apply();
    }

    public static String getIdBannerGA(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_BANNER_ADS, "");
    }

    public static void setIdNativeGA(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_NATIVE_ADS, enabelIS);
        editor.apply();
    }

    public static String getIdNativeGA(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_NATIVE_ADS, "");
    }

    public static void setIdIntersGA(Activity mActivity, String enabelIS) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putString(ID_INTERSTITIAL_ADS, enabelIS);
        editor.apply();
    }

    public static String getIdIntersGA(Activity mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getString(ID_INTERSTITIAL_ADS, "");
    }

    public static void setHomeResume(Context mActivity, boolean isHomeClick) {
        SharedPreferences.Editor editor = mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).edit();
        editor.putBoolean(ID_HOME_RESUME, isHomeClick);
        editor.apply();
    }

    public static boolean getHomeResume(Context mActivity) {
        return mActivity.getSharedPreferences(mActivity.getPackageName(), Context.MODE_PRIVATE).getBoolean(ID_HOME_RESUME, false);
    }

    public static void setEnableInters(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(ENABLE_INTERS, timeShow);
        edit.apply();
    }

    public static boolean getEnableInters(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(ENABLE_INTERS, true);
    }

    public static void setIdReward(Activity activity, String timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putString(ID_REWARD_ADS, timeShow);
        edit.apply();
    }

    public static String getIdReward(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getString(ID_REWARD_ADS, "");
    }

    // Enable
    public static void setEnableBanner(Activity activity, boolean isShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(ENABLE_BANNER, isShow);
        edit.apply();
    }

    public static boolean getEnableBanner(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(ENABLE_BANNER, true);
    }

    public static void setEnableNative(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(ENABLE_NATIVE, timeShow);
        edit.apply();
    }

    public static boolean getEnableNative(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(ENABLE_NATIVE, true);
    }

    public static void setEnableResume(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(RESUME_ADS, timeShow);
        edit.apply();
    }

    public static boolean getResumeAds(Context activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(RESUME_ADS, true);
    }

    public static void setBannerCollapsible(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(BANNER_COLLAPSIBLE, timeShow);
        edit.apply();
    }

    public static boolean getRatingPopup(Context activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(RATING_POPUP, true);
    }

    public static void setRatingPopup(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(RATING_POPUP, timeShow);
        edit.apply();
    }

    public static boolean getBannerCollapsible(Context activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(BANNER_COLLAPSIBLE, true);
    }

    public static void setEnableOpenAds(Activity activity, boolean timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putBoolean(ENABLE_OPEN_ADS, timeShow);
        edit.apply();
    }

    public static boolean getEnableOpenAds(Context activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getBoolean(ENABLE_OPEN_ADS, true);
    }

    // Adx
    public static void setIdBannerAdx(Activity activity, String timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putString(ID_BANNER_ADX, timeShow);
        edit.apply();
    }

    public static String getIdBannerAdx(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getString(ID_BANNER_ADX, "");
    }

    public static void setIdNativeAdx(Activity activity, String timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putString(ID_NATIVE_ADX, timeShow);
        edit.apply();
    }

    public static String getIdNativeAdx(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getString(ID_NATIVE_ADX, "");
    }

    public static void setIdOpenAdAdx(Activity activity, String timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putString(ID_OPEN_AD_ADX, timeShow);
        edit.apply();
    }

    public static String getIdOpenAdAdx(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getString(ID_OPEN_AD_ADX, "");
    }

    public static void setIdIntersAdx(Activity activity, String timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putString(ID_INTERS_ADX, timeShow);
        edit.apply();
    }

    public static String getIdIntersAdAdx(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getString(ID_INTERS_ADX, "");
    }

    public static int getCountShow(Activity activity) {
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).getInt(COUNT_SHOW_INTERS, 0);
    }

    public static void setCountShow(Activity activity, int timeShow) {
        SharedPreferences.Editor edit = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE).edit();
        edit.putInt(COUNT_SHOW_INTERS, timeShow);
        edit.apply();
    }
}
