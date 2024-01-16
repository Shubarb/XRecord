package com.ga.controller.network.model;

public class UpdateObj {

    private String mTitle;
    private String mDes;
    private String mVersionName;
    private String mLinkBanner;
    private int mStatus;
    private String mUrl;
    private boolean mForce;
    private int mVersionCode;
    private int numberShow;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDes() {
        return mDes;
    }

    public void setDes(String des) {
        mDes = des;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public boolean isForce() {
        return mForce;
    }

    public void setForce(boolean force) {
        mForce = force;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        mVersionCode = versionCode;
    }

    public int getNumberShow() {
        return numberShow;
    }

    public void setNumberShow(int numberShow) {
        this.numberShow = numberShow;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public void setVersionName(String mVersionName) {
        this.mVersionName = mVersionName;
    }

    public String getLinkBanner() {
        return mLinkBanner;
    }

    public void setLinkBanner(String mLinkBanner) {
        this.mLinkBanner = mLinkBanner;
    }
}
