package com.module.config.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat


object ServiceUtils {
    fun isRunning(mContext: Context, serviceClass: Class<*>): Boolean {
        val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun stopService(mContext: Context, serviceClass: Class<*>) {
        if (isRunning(mContext, serviceClass)) {
            mContext.stopService(Intent(mContext, serviceClass))
        }
    }

    fun startService(context: Context, serviceClass: Class<*>) {
        if (!isRunning(context, serviceClass)) {
            val intentService = Intent(context, serviceClass)
            ContextCompat.startForegroundService(context, intentService)
        }
    }
}