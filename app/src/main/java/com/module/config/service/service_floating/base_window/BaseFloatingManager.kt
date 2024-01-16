package com.module.config.service.service_floating.base_window

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.module.config.R
import com.module.config.utils.utils_controller.PreferencesHelper
import jp.com.lifestyle.floating.view.FloatingViewListener
import jp.com.lifestyle.floating.view.FloatingViewManager

abstract class BaseFloatingManager(val context: Context) :
    FloatingViewListener {
    var mFloatingViewManager: FloatingViewManager? = null
    var rootView: View? = null
    var options: FloatingViewManager.Options? = null
    var windowManager: WindowManager? = null
    private var inflater: LayoutInflater? = null
    var metrics: DisplayMetrics = DisplayMetrics()

    init {
        init()
    }

    private fun init() {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.defaultDisplay?.getMetrics(metrics)
        inflater = LayoutInflater.from(context)
        rootView = inflater?.inflate(getRootViewId(), null, false)
        mFloatingViewManager = FloatingViewManager(context, this)
        mFloatingViewManager?.setFixedTrashIconImage(R.drawable.ic_trash_fixed)
        mFloatingViewManager?.setActionTrashIconImage(R.drawable.ic_trash_action)
        setUpOptionsFloating()
        initLayout()
    }

    abstract fun initLayout()
    abstract fun getRootViewId(): Int

    protected open fun setUpOptionsFloating() {
        options = FloatingViewManager.Options()
    }

    fun addFloatingView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) return
        if (mFloatingViewManager != null && options != null) {
            mFloatingViewManager!!.addViewToWindow(rootView, options)
        }
    }

    fun removeFloatingView() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager!!.removeAllViewToWindow()
            mFloatingViewManager = null
        }
    }

    fun showFloatingView() {
        Log.e("CHECKFLOAT", "Show float view")
        if (rootView != null) {
            rootView!!.visibility = View.VISIBLE
            rootView!!.isClickable = true
        }
    }

    fun hideFloatingView() {
        Log.e("CHECKFLOAT", "Hide float view")
        if (rootView != null) {
            rootView!!.isClickable = false
            rootView!!.visibility = View.INVISIBLE
        }
    }

    fun vibrateWhenClick() {
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_VIBRATE, false)) {
            val vibrate = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrate.vibrate(50)
        }
    }

    fun updatePositionFloatingView(x: Int, y: Int) {
        if (mFloatingViewManager != null) {
            mFloatingViewManager!!.updatePositionFloatingView(x, y)
        }
    }

    val windowParamsFloatingView: WindowManager.LayoutParams?
        get() = if (mFloatingViewManager != null) {
            mFloatingViewManager!!.windowParamsFloatingView
        } else null

    override fun onFinishFloatingView() {
        removeFloatingView()
    }

    override fun onTouchFinished(isFinishing: Boolean, x: Int, y: Int) {}
}
