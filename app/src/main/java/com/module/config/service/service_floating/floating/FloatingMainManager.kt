package com.module.config.service.service_floating.floating

import android.content.Context
import android.content.Intent
import android.util.Log
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.service.service_floating.base_window.BaseFloatingManager
import com.module.config.service.service_floating.layout.LayoutBlurManager
import com.module.config.service.service_floating.layout.LayoutMainLeftManager
import com.module.config.service.service_floating.layout.LayoutMainRightManager
import com.module.config.service.service_floating.layout.LayoutToolsManager
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.Toolbox
import com.module.config.views.activities.main.MainActivity
import com.module.config.views.activities.record.RecordActivity

class FloatingMainManager(context: Context) : BaseFloatingManager(context) {
    private var layoutMainLeftManager: LayoutMainLeftManager? = null
    private var layoutMainRightManager: LayoutMainRightManager? = null
    private var layoutBlurManager: LayoutBlurManager? = null

    init {
        addFloatingView()
        Log.e("CHECKFLOATCLICK", "init")
    }

    override fun initLayout() {
        Log.e("CHECKFLOATCLICK", "initLayout")
        rootView?.setOnClickListener {
            initLayoutMainManager()
        }
    }

    override fun getRootViewId() = R.layout.floating_view_main

    override fun setUpOptionsFloating() {
        super.setUpOptionsFloating()
        options?.apply {
            isEnableAlpha = true
            overMargin = 16
            floatingViewWidth = context.resources.getDimension(R.dimen.size_floating_icon).toInt()
            floatingViewHeight = context.resources.getDimension(R.dimen.size_floating_icon).toInt()
            floatingViewX = 0
            floatingViewY = (metrics.heightPixels) / 2 - (this.floatingViewHeight)
        }
    }

    private fun initLayoutMainManager() {
        if (mFloatingViewManager == null) return
        vibrateWhenClick()
        hideFloatingView()
        showBlur()
        Log.e("CHECKFLOATCLICK", "initLayoutMainManager")
        if (isFloatingViewInLeft) {
            Log.e("CHECKFLOATCLICK", "click float left")
            layoutMainLeftManager = mFloatingViewManager?.windowParamsFloatingView?.let {
                LayoutMainLeftManager(
                    context,
                    it,
                    object : LayoutMainLeftManager.CallBack {
                        override fun onClickLayout() {
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickLayout")
                        }

                        override fun onClickRecord() {
                            openRecord()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickRecord")
                        }

                        override fun onClickTools() {
                            showTools()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickTools")
                        }

                        override fun onClickHome() {
                            openHome()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickHome")
                        }

                        override fun onClickSetting() {
                            openSetting()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickSetting")
                        }
                    })
            }
        } else {
            Log.e("CHECKFLOATCLICK", "click float right")
            layoutMainRightManager = mFloatingViewManager?.windowParamsFloatingView?.let {
                LayoutMainRightManager(
                    context,
                    it,
                    object : LayoutMainRightManager.Callback {
                        override fun onClickLayout() {
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickLayout")
                        }

                        override fun onClickRecord() {
                            openRecord()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickRecord")
                        }

                        override fun onClickTools() {
                            showTools()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickTools")
                        }

                        override fun onClickHome() {
                            openHome()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickHome")
                        }

                        override fun onClickSetting() {
                            openSetting()
                            clearStateShowMainLayout()
                            Log.e("CHECKFLOATCLICK", "onClickSetting")
                        }
                    })
            }
        }
    }

    private fun openHome() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.action = Config.ACTION_OPEN_MAIN
        Toolbox.startActivityAllStage(context, intent)
    }

    private fun openSetting() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.action = Config.ACTION_OPEN_SETTING
        Toolbox.startActivityAllStage(context, intent)
    }

    private fun openRecord() {
        RxBusHelper.sendStartRecord()
    }

    private fun showBlur() {
        layoutBlurManager = LayoutBlurManager(context) { clearStateShowMainLayout() }
    }

    fun showTools() {
        LayoutToolsManager(context)
    }

    private fun clearStateShowMainLayout() {
        showFloatingView()
        layoutBlurManager?.removeLayout()
        layoutMainRightManager?.removeLayout()
        layoutMainLeftManager?.removeLayout()
    }

    private val isFloatingViewInLeft: Boolean
        get() = if (mFloatingViewManager == null) false else mFloatingViewManager?.xFloatingView!! < metrics.widthPixels / 2

    override fun onFinishFloatingView() {
        clearStateShowMainLayout()
        super.onFinishFloatingView()
    }
}