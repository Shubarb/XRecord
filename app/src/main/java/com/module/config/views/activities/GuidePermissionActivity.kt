package com.module.config.views.activities

import com.module.config.R
import com.module.config.databinding.ActivityTipsPermissionBinding
import com.module.config.views.bases.BaseActivity

class GuidePermissionActivity : BaseActivity<ActivityTipsPermissionBinding>() {

    override fun getLayoutActivity() = R.layout.activity_tips_permission
    override fun initViews() {}
    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvOk.setOnClickListener { v -> finish() }
    }
}