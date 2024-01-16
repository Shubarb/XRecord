package com.module.config.views.activities.on_boarding

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.ga.controller.network.ga.IntersInApp
import com.ga.controller.network.ga.NativeInApp
import com.ga.controller.query.FirebaseQuery
import com.module.config.R
import com.module.config.app.AppConstants
import com.module.config.databinding.ActivityOnBoardingBinding
import com.module.config.models.OnBoardingModel
import com.module.config.views.activities.main.MainActivity
import com.module.config.views.bases.BaseActivity
import com.module.config.views.bases.ext.OnCustomClickListener
import com.module.config.views.bases.ext.setOnCustomTouchViewAlphaNotOther
import com.orhanobut.hawk.Hawk

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(),
    OnCustomClickListener {
    private var onBoardingAdapter: OnBoardingAdapter? = null

    override fun getLayoutActivity() = R.layout.activity_on_boarding

    override fun initViews() {
        super.initViews()
        // load ads
        NativeInApp.getInstance()
            .showNative(this, mBinding.lnNative, 1, FirebaseQuery.getIdNativeOnBoard(this))

        initListTutorial()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvNext.setOnCustomTouchViewAlphaNotOther(this)
    }

    private fun initListTutorial() {
        val listOnBoarding = listOf(
            OnBoardingModel(
                R.drawable.img_on_boarding_1,
                resources.getString(R.string.title_on_boarding_1),
                resources.getString(R.string.content_on_boarding_1)
            ),
            OnBoardingModel(
                R.drawable.img_on_boarding_2,
                resources.getString(R.string.title_on_boarding_2),
                resources.getString(R.string.content_on_boarding_2)
            ),
            OnBoardingModel(
                R.drawable.img_on_boarding_3,
                resources.getString(R.string.title_on_boarding_3),
                resources.getString(R.string.content_on_boarding_3)
            )
        )

        onBoardingAdapter = OnBoardingAdapter()
        onBoardingAdapter?.submitData(listOnBoarding)
        mBinding.vpOnBoarding.apply {
            offscreenPageLimit = listOnBoarding.size
            currentItem = 0
            adapter = onBoardingAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        mBinding.circleIndicator.setViewPager(mBinding.vpOnBoarding)
    }

    override fun onCustomClick(view: View, event: MotionEvent) {
        when (view.id) {
            R.id.tv_next -> {
                onBoardingAdapter?.let {
                    if (mBinding.vpOnBoarding.currentItem + 1 < it.itemCount) {
                        mBinding.vpOnBoarding.currentItem += 1
                    } else {
                        onNextActivity()
                    }
                }
            }
        }
    }

    private fun onNextActivity() {
        IntersInApp.getInstance().showIntersInScreen(this) {
            Hawk.put(AppConstants.KEY_SELECT_LANGUAGE, true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}