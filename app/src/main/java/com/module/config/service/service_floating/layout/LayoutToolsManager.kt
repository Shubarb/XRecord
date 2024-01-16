package com.module.config.service.service_floating.layout

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.rx.RxBusType
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager
import com.module.config.utils.utils_controller.PreferencesHelper

class LayoutToolsManager(context: Context?) :
    BaseLayoutWindowManager(context!!) {
    init {
        initParams()
        addLayout()
    }

    private fun initParams() {
        mParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getRootViewId() = R.layout.layout_permission_landscap
    override fun initLayout() {
        val imvClose: ImageView? = rootView?.findViewById(R.id.imv_close)
        val swScreenShot: SwitchCompat? = rootView?.findViewById(R.id.sw_screen_shot)
        val swCamera: SwitchCompat? = rootView?.findViewById(R.id.sw_camera)
        val swBrush: SwitchCompat? = rootView?.findViewById(R.id.sw_brush)
        imvClose?.setOnClickListener { v: View? -> removeLayout() }
        swScreenShot?.isChecked =
            PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false)
        swCamera?.isChecked =
            PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false)
        swBrush?.isChecked =
            PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false)
        swScreenShot?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, isChecked)
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_SCREEN_SHOT, isChecked)
        }
        swCamera?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, isChecked)
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_CAMERA, isChecked)
        }
        swBrush?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, isChecked)
            RxBusHelper.sendCheckedTools(RxBusType.TOOLS_BRUSH, isChecked)
        }
    }
}