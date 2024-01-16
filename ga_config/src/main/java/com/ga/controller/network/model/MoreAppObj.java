package com.ga.controller.network.model;

public class MoreAppObj {

    private String packageName;
    private String titleApp;
    private String desApp;
    private String linkCover;
    private String mLinkLogo;

    public MoreAppObj(String packageName, String titleApp, String desApp, String linkCover, String linkLogo) {
        this.packageName = packageName;
        this.titleApp = titleApp;
        this.desApp = desApp;
        this.linkCover = linkCover;
        this.mLinkLogo = linkLogo;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTitleApp() {
        return titleApp;
    }

    public String getDesApp() {
        return desApp;
    }

    public String getLinkCover() {
        return linkCover;
    }

    public String getLinkLogo() {
        return mLinkLogo;
    }

    public void setLinkLogo(String linkLogo) {
        this.mLinkLogo = linkLogo;
    }
}
