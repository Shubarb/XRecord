package com.ga.controller.network.ma;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ga.controller.R;
import com.ga.controller.query.FirebaseQuery;
import com.ga.controller.utils.InternetUtils;

public class UpdateAppDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ImageView imgBannerUpdate;
    private TextView tvUpdateNow;
    private TextView tvVersionUpdate;
    private TextView tvContentUpdate;

    public UpdateAppDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_app);

        funcStyleDialog();
        initViews();
        initEvent();
    }

    private void initViews() {
        imgBannerUpdate = findViewById(R.id.img_banner_update);
        tvUpdateNow = findViewById(R.id.tv_ok_update);
        tvVersionUpdate = findViewById(R.id.tv_version_update);
        tvContentUpdate = findViewById(R.id.tv_content_new_version);

        try {
            if (FirebaseQuery.getConfigController().mListUpdateApp != null && FirebaseQuery.getConfigController().mListUpdateApp.size() > 0) {
                for (int i = 0; i < FirebaseQuery.getConfigController().mListUpdateApp.size(); i++) {
                    tvContentUpdate.setText(FirebaseQuery.getConfigController().mListUpdateApp.get(i).getDes());
                    tvVersionUpdate.setText(String.valueOf(FirebaseQuery.getConfigController().mListUpdateApp.get(i).getVersionName()));
                    Glide.with(mContext)
                            .load(FirebaseQuery.getConfigController().mListUpdateApp.get(i).getLinkBanner())
                            .into(imgBannerUpdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        tvUpdateNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tvUpdateNow) {
            funcGotoGP(mContext, mContext.getPackageName());
            dismiss();
        }
    }

    private void funcStyleDialog() {
        Window window = getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setLayout(width, height);
        setCancelable(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void funcGotoGP(Context mContext, String packageName) {
        try {
            mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showDialogUpdate(Activity activity) {
        try {
            if (InternetUtils.checkInternet(activity)) {
                if (FirebaseQuery.getConfigController().mListUpdateApp != null && FirebaseQuery.getConfigController().mListUpdateApp.size() > 0) {
                    for (int i = 0; i < FirebaseQuery.getConfigController().mListUpdateApp.size(); i++) {
                        int currentVer = getVersionApp(activity);
                        if (FirebaseQuery.getConfigController().mListUpdateApp.get(i).getVersionCode() >= currentVer) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new UpdateAppDialog(activity).show();
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getVersionApp(Activity activity) {
        PackageInfo pInfo = null;
        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentAppVersionCode = 0;
        if (pInfo != null) {
            currentAppVersionCode = pInfo.versionCode;
        }
        return currentAppVersionCode;
    }
}
