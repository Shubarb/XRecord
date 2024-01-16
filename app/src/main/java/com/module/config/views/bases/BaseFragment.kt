package com.module.config.views.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.ga.controller.utils.FBTracking
import com.module.config.rx.CallbackEventView

abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), CallbackEventView {
    lateinit var mBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val layoutView = getLayoutFragment()
        mBinding = DataBindingUtil.inflate(inflater, layoutView, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        onResizeViews()
        onClickViews()
        observerData()
    }

    abstract fun getLayoutFragment(): Int

    open fun initViews() {}

    open fun onClickViews() {}

    open fun onResizeViews() {}

    open fun observerData() {}

    fun logEvents(event: String) {
        activity?.let { act ->
            FBTracking.funcTrackingFunction(act, FBTracking.EVENT_FEATURE, event)
        }
    }
}