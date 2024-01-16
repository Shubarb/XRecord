package com.module.config.service.service_floating.base_window

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import com.module.config.R
import com.module.config.utils.utils_controller.PreferencesHelper

abstract class BaseLayoutWindowManager(var context: Context) {
    var windowManager: WindowManager? = null
    var mParams: WindowManager.LayoutParams? = null
    var inflate: LayoutInflater? = null
    var rootView: View? = null

    init {
        init()
        initLayout()
    }

    protected fun init() {
        val ctx: ContextThemeWrapper = ContextThemeWrapper(
            context, R.style.Theme_AppCompat
        )
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        inflate = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = inflate!!.inflate(getRootViewId(), null)
        mParams = WindowManager.LayoutParams()
        mParams!!.type =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_PHONE else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        mParams!!.format = PixelFormat.TRANSLUCENT
    }

    open fun removeLayout() {
        if (windowManager != null) {
            windowManager!!.removeView(rootView)
            mParams = WindowManager.LayoutParams()
            windowManager = null
        }
    }

    fun addLayout() {
        if (windowManager != null && rootView != null) {
            windowManager!!.addView(rootView, mParams)
        }
    }

    fun vibrateWhenClick() {
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_VIBRATE, false)) {
            val vibrate = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrate.vibrate(50)
        }
    }

    protected abstract fun getRootViewId(): Int

    protected abstract fun initLayout()
}