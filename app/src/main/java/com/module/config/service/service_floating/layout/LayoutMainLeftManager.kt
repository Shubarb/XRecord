package com.module.config.service.service_floating.layout

import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.module.config.R
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager
import com.module.config.utils.utils_controller.ViewUtils

class LayoutMainLeftManager(
    context: Context?,
    mParams: WindowManager.LayoutParams,
    private val callBack: CallBack
) :
    BaseLayoutWindowManager(context!!) {
    private val handler: Handler

    init {
        initParams(mParams)
        addLayout()
        handler = Handler()
        handler.postDelayed({ callBack.onClickLayout() }, 3000)
    }

    private fun initParams(params: WindowManager.LayoutParams) {
        mParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        mParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mParams?.gravity = Gravity.START or Gravity.BOTTOM
        mParams?.x = params.x
        mParams?.y = params.y - (context.resources.getDimension(R.dimen.margin).toInt())
    }

    override fun initLayout() {
        (rootView?.findViewById(R.id.container_main) as FrameLayout).setOnClickListener { v ->
            vibrateWhenClick()
            callBack.onClickLayout()
        }
        (rootView?.findViewById(R.id.imv_record) as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickRecord()
            vibrateWhenClick()
        }
        (rootView?.findViewById(R.id.imv_tools) as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickTools()
            vibrateWhenClick()
        }
        (rootView?.findViewById(R.id.imv_home) as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickHome()
            vibrateWhenClick()
        }
        (rootView?.findViewById(R.id.imv_setting) as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickSetting()
            vibrateWhenClick()
        }
        ViewUtils.scaleSelected(
            rootView?.findViewById(R.id.container_main),
            rootView?.findViewById(R.id.imv_record),
            rootView?.findViewById(R.id.imv_tools),
            rootView?.findViewById(R.id.imv_home),
            rootView?.findViewById(R.id.imv_setting)
        )
    }

    override fun removeLayout() {
        super.removeLayout()
        handler.removeCallbacksAndMessages(null)
    }

    override fun getRootViewId() = R.layout.layout_floating_main_left

    interface CallBack {
        fun onClickLayout()
        fun onClickRecord()
        fun onClickTools()
        fun onClickHome()
        fun onClickSetting()
    }
}