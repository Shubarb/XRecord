package com.module.config.views.activities.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.ga.controller.network.ga.IntersInApp
import com.ga.controller.query.FirebaseQuery
import com.module.config.R
import com.module.config.app.AppConstants
import com.module.config.databinding.ActivitySplashBinding
import com.module.config.views.activities.language.LanguageActivity
import com.module.config.views.activities.main.MainActivity
import com.orhanobut.hawk.Hawk

class SplashActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        initViews()
    }

    private fun initViews() {
        FirebaseQuery.getConfigController().initFirebase(this) {
            IntersInApp.getInstance().loadIntersInScreen(this)
            nextActivity()
        }
    }

    private fun nextActivity() {
        val isChooseLanguage = Hawk.get(AppConstants.KEY_SELECT_LANGUAGE, false)
        if (isChooseLanguage) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intentMain = Intent(this, LanguageActivity::class.java)
            startActivity(intentMain)
        }
        finish()
    }

    private fun hideNavigationBar() {
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

    override fun onBackPressed() {
    }
}