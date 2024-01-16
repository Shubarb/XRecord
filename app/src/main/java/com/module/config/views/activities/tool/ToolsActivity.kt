package com.module.config.views.activities.tool

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.module.config.service.RecorderService
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.views.activities.GuidePermissionActivity

class ToolsActivity : AppCompatActivity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        askPermissionOverlay()
    }

    private fun askPermissionOverlay() {
        if (isSystemAlertPermissionGranted(this)) {
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
            showFloatingTools()
        } else {
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + applicationContext.packageName)
                ), REQUEST_SETTING_OVERLAY_PERMISSION
            )
            startActivity(Intent(this, GuidePermissionActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SETTING_OVERLAY_PERMISSION) {
            Handler().postDelayed({
                if (isSystemAlertPermissionGranted(this@ToolsActivity)) {
                    PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
                    showFloatingTools()
                } else {
                    finish()
                }
            }, 200)
        }
    }

    private fun showFloatingTools() {
        val intent = Intent(this, RecorderService::class.java)
        intent.action = Config.ACTION_SHOW_TOOLS
        startService(intent)
        finish()
    }

    companion object {
        private const val REQUEST_SETTING_OVERLAY_PERMISSION = 830
        fun isSystemAlertPermissionGranted(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT < 23) {
                true
            } else Settings.canDrawOverlays(context)
        }
    }
}
