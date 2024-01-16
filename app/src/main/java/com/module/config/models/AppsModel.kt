package com.module.config.models

import android.graphics.drawable.Drawable

class AppsModel(var appName: String, var packageName: String, var appIcon: Drawable) :
    Comparable<AppsModel?> {
    var isSelectedApp = false

    override fun compareTo(other: AppsModel?): Int {
        return appName.compareTo(other!!.appName)
    }
}