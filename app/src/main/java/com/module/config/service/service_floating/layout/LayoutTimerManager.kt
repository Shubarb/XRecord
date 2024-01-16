package com.module.config.service.service_floating.layout

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.module.config.R
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper

class LayoutTimerManager(context: Context?) :
    BaseLayoutWindowManager(context!!) {
    private var tvTimer: TextView? = null
    private var callBack: CallBack? = null
    init {
        initParams()
        addLayout()
        handlerTimer()
    }

    private fun initParams() {
        mParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getRootViewId() = R.layout.layout_timer

    override fun initLayout() {
        tvTimer = rootView?.findViewById(R.id.tv_timer)
    }

    private fun handlerTimer() {
        val animation: Animation = AnimationUtils.loadAnimation(
            context,
            R.anim.scale_tv_time
        )
        val x: Int = PreferencesHelper.getString(
            PreferencesHelper.KEY_TIMER,
            Config.itemsTimer[1].value
        ).toInt()
        if (x == 0) {
            removeLayout()
            callBack?.onTimeEnd()
            return
        }
        object : CountDownTimer(((x + 1) * 1000).toLong(), 1000) {
            override fun onFinish() {
                tvTimer!!.text = ""
                removeLayout()
                Handler().postDelayed({ callBack?.onTimeEnd() }, 500)
            }

            override fun onTick(millisUntilFinished: Long) {
                tvTimer!!.text = (millisUntilFinished / 1000).toString()
                tvTimer!!.startAnimation(animation)
            }
        }.start()
    }

    fun callBackLayoutTimer(calls: CallBack){
        this.callBack = calls
    }

    interface CallBack {
        fun onTimeEnd()
    }
}