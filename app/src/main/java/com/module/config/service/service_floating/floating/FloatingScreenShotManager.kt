package com.module.config.service.service_floating.floating

import android.content.Context
import android.content.Intent
import android.os.Build
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.service.service_floating.base_window.BaseFloatingManager
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.utils.utils_controller.ScreenRecordHelper
import com.module.config.utils.utils_controller.ScreenShotHelper
import com.module.config.utils.utils_controller.Toolbox
import com.module.config.views.activities.screenshot.ScreenShotActivity

class FloatingScreenShotManager(context: Context?) :
    BaseFloatingManager(context!!) {
    private val screenShotHelper: ScreenShotHelper

    init {
        screenShotHelper = ScreenShotHelper(context!!, windowManager!!, metrics!!)
    }

    override fun setUpOptionsFloating() {
        super.setUpOptionsFloating()
        options?.isEnableAlpha = true
        options?.overMargin = 16
        options?.floatingViewWidth =
            context.resources.getDimension(R.dimen.size_floating_icon).toInt()
        options?.floatingViewHeight =
            context.resources.getDimension(R.dimen.size_floating_icon).toInt()
        options?.floatingViewX = metrics?.widthPixels!! - options?.floatingViewWidth!!
        options?.floatingViewY = metrics?.heightPixels!! / 2 + options?.floatingViewHeight!!
    }

    override fun initLayout() {
        rootView?.setOnClickListener { _ ->
            vibrateWhenClick()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startCaptureScreen()
            }
        }
    }

    override fun getRootViewId() = R.layout.floating_view_capture

    fun startCaptureScreen() {
        if (ScreenRecordHelper.STATE !== ScreenRecordHelper.State.RECORDING) {
            openScreenShot()
        }
    }

    fun captureScreen(resultData: Intent?, resultCode: Int) {
        RxBusHelper.sendStartScreenShot()
        screenShotHelper.captureScreen(resultData, resultCode)
    }

    fun openScreenShot() {
        val intent: Intent = Intent(context, ScreenShotActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        Toolbox.startActivityAllStage(context, intent)
    }

    override fun onFinishFloatingView() {
        PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false)
        super.onFinishFloatingView()
    }
}


