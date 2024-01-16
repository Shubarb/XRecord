package com.module.config.service.service_floating.layout

import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.module.config.R
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager
import com.module.config.utils.utils_controller.ScreenRecordHelper
import com.module.config.utils.utils_controller.ViewUtils
import kotlin.math.cos

class LayoutRecordLeftManager(
    context: Context?,
    params: WindowManager.LayoutParams,
    time: String?,
    private val callBack: Callback
) :
    BaseLayoutWindowManager(context!!) {
    private var tvTime: TextView? = null
    private val handler: Handler

    init {
        initParams(params)
        addLayout()
        setTime(time)
        handler = Handler()
        handler.postDelayed({ callBack.onClickLayout() }, 3000)
    }

    private fun initParams(params: WindowManager.LayoutParams) {
        mParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        mParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mParams?.gravity = Gravity.LEFT or Gravity.BOTTOM
        mParams?.x = params.x
        mParams?.y = (params.y - (context.resources
            .getDimension(R.dimen.margin) as Int - cos(30.0) * context.resources
            .getDimension(R.dimen.margin) as Int)).toInt()
    }

    fun setTime(time: String?) {
        tvTime!!.text = time
    }

    fun setPauseOrResume(state: ScreenRecordHelper.State) {
        if (state === ScreenRecordHelper.State.RECORDING) {
            (rootView?.findViewById(R.id.imv_pause) as ImageView).setImageResource(R.drawable.ic_pause)
        } else if (state === ScreenRecordHelper.State.PAUSED) {
            (rootView?.findViewById(R.id.imv_pause) as ImageView).setImageResource(R.drawable.ic_play)
        }
    }

    override fun initLayout() {
        tvTime = rootView?.findViewById(R.id.tv_time)
        if (ScreenRecordHelper.STATE === ScreenRecordHelper.State.PAUSED) {
            (rootView?.findViewById(R.id.imv_pause) as ImageView).setImageResource(R.drawable.ic_play)
        } else if (ScreenRecordHelper.STATE === ScreenRecordHelper.State.RECORDING) {
            (rootView?.findViewById(R.id.imv_pause) as ImageView).setImageResource(R.drawable.ic_pause)
        }
        (rootView?.findViewById(R.id.container_main) as FrameLayout).setOnClickListener { v ->
            callBack.onClickLayout()
            vibrateWhenClick()
        }
        (rootView?.findViewById(R.id.imv_pause)as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickPauseOrPlay()
            vibrateWhenClick()
        }
            (rootView?.findViewById(R.id.imv_stop)as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickStop()
            vibrateWhenClick()
        }
            (rootView?.findViewById(R.id.imv_tools)as AppCompatImageView).setOnClickListener { v ->
            callBack.onClickTools()
            vibrateWhenClick()
        }
        ViewUtils.scaleSelected(
            rootView?.findViewById(R.id.container_main),
            rootView?.findViewById(R.id.imv_pause),
            rootView?.findViewById(R.id.imv_stop),
            rootView?.findViewById(R.id.imv_tools)
        )
    }

    override fun removeLayout() {
        super.removeLayout()
        handler.removeCallbacksAndMessages(null)
    }

    override fun getRootViewId() = R.layout.layout_floating_recording_left

    interface Callback {
        fun onClickLayout()
        fun onClickPauseOrPlay()
        fun onClickTools()
        fun onClickStop()
    }
}