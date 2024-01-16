package com.module.config.service.service_floating.floating

import android.content.Context
import com.module.config.R
import com.module.config.service.service_floating.base_window.BaseFloatingManager
import com.module.config.service.service_floating.layout.LayoutBrushManager
import com.module.config.utils.utils_controller.PreferencesHelper

class FloatingBrushManager(context: Context) : BaseFloatingManager(context) {
    private var layoutBrushManager: LayoutBrushManager? = null
    override fun setUpOptionsFloating() {
        super.setUpOptionsFloating()
        options?.isEnableAlpha = true
        options?.overMargin = 16
        options?.floatingViewWidth =
            context.resources.getDimension(R.dimen.size_floating_icon).toInt()
        options?.floatingViewHeight =
            context.resources.getDimension(R.dimen.size_floating_icon).toInt()
        options?.floatingViewX = options?.let { metrics.widthPixels.minus(it.floatingViewWidth) }
        options?.floatingViewY =
            ((metrics.heightPixels.div(2)).minus(options?.floatingViewHeight!!))
    }

    override fun initLayout() {
        rootView?.setOnClickListener { v ->
            vibrateWhenClick()
            layoutBrushManager = LayoutBrushManager(context)
        }
    }

    override fun getRootViewId() = R.layout.floating_view_brush

    fun onRemoveLayoutBrush() {
        if (layoutBrushManager != null) {
            layoutBrushManager?.removeLayout()
        }
    }

    override fun onFinishFloatingView() {
        PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false)
        super.onFinishFloatingView()
    }
}