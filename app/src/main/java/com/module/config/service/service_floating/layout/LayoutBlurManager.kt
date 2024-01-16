package com.module.config.service.service_floating.layout

import android.content.Context
import android.view.ViewGroup
import com.module.config.R
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager

class LayoutBlurManager(context: Context?, private val callback: () -> Unit) :
    BaseLayoutWindowManager(context!!) {
    init {
        initParams()
        addLayout()
    }

    private fun initParams() {
        mParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getRootViewId() = R.layout.layout_main_blur

    override fun initLayout() {
        rootView?.setOnClickListener { _ -> callback() }
    }

    interface Callback {
        fun onClickLayout()
    }
}