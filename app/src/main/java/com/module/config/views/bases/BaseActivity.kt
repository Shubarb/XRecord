package com.module.config.views.bases

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ga.controller.network.ga.OpenAdsUtils
import com.ga.controller.utils.FBTracking
import com.module.config.helpers.BetterActivityResult
import com.module.config.rx.CallbackEventView
import com.module.config.utils.LanguageUtils

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), CallbackEventView {
    lateinit var mBinding: VB

    protected val activityLauncher: BetterActivityResult<Intent, ActivityResult> =
        BetterActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageUtils.setLocale(this)
        requestWindow()
        val layoutView = getLayoutActivity()
        if (!::mBinding.isInitialized) {
            mBinding = DataBindingUtil.setContentView(this, layoutView)
        }
        mBinding.lifecycleOwner = this

        initViews()
        onResizeViews()
        onClickViews()
        observerData()
    }

    abstract fun getLayoutActivity(): Int

    open fun requestWindow() {}

    open fun initViews() {}

    open fun onResizeViews() {}

    open fun onClickViews() {}

    open fun observerData() {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideNavigationBar()
    }

    fun hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            hideSystemUIBelowR()
        }
    }

    private fun hideSystemUIBelowR() {
        val decorView: View = window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }

    override fun onResume() {
        super.onResume()
        OpenAdsUtils.getOpenAdsUtils().onResume(this)
        OpenAdsUtils.getOpenAdsUtils().loadAndShowResume(this)
    }

    fun logEvents(event: String) {
        FBTracking.funcTrackingFunction(this, FBTracking.EVENT_FEATURE, event)
    }

}