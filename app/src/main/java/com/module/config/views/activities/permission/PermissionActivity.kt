package com.module.config.views.activities.permission

import android.content.Intent
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.ga.controller.network.ga.NativeInApp
import com.ga.controller.query.FirebaseQuery
import com.module.config.R
import com.module.config.databinding.ActivityPermissionBinding
import com.module.config.utils.PermissionUtils
import com.module.config.utils.REQUEST_CODE_STORAGE_PERMISSION
import com.module.config.views.activities.on_boarding.OnBoardingActivity
import com.module.config.views.bases.BaseActivity
import com.module.config.views.bases.ext.canTouch
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class PermissionActivity : BaseActivity<ActivityPermissionBinding>(),
    EasyPermissions.PermissionCallbacks {

    override fun getLayoutActivity() = R.layout.activity_permission

    override fun initViews() {
        super.initViews()
        // load ads
        NativeInApp.getInstance()
            .loadNativeGA(this, mBinding.lnNative, 2, FirebaseQuery.getIdNativeGA(this))

        mBinding.swStorage.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                onClickContinue()
            }
        }
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvContinue.setOnClickListener {
            if (!canTouch()) return@setOnClickListener
            onClickContinue()
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.swStorage.isChecked = PermissionUtils.hasStorage(this)
    }

    private fun onClickContinue() {
        if (PermissionUtils.hasStorage(this)) {
            nextActivity()
        } else {
            PermissionUtils.requestStorage(
                this,
                resources.getString(R.string.text_permission_content)
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                mBinding.swStorage.isChecked = false
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    SettingsDialog.Builder(this).build().show()
                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        val colorInt: Int = ContextCompat.getColor(this, R.color.color_icon)
        val csl = ColorStateList.valueOf(colorInt)

        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                mBinding.swStorage.isChecked = true
            }
        }
    }

    private fun nextActivity() {
        val intent = Intent(this, OnBoardingActivity::class.java)
        startActivity(intent)
        finish()
    }
}