package com.module.config.views.activities.camera_view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.module.config.service.RecorderService
import com.module.config.utils.utils_controller.Config
import com.module.config.views.activities.GuidePermissionActivity

class CameraActivity : AppCompatActivity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        askPermissionOverlay()
    }

    fun askPermissionOverlay() {
        if (isSystemAlertPermissionGranted(this)) {
            askPermissionCamera()
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
                if (isSystemAlertPermissionGranted(this@CameraActivity)) {
                    askPermissionCamera()
                } else {
                    finishAffinity()
                }
            }, 200)
        }
    }

    private fun askPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_VIEW
                )
            } else {
                showCamera()
            }
        } else {
            showCamera()
        }
    }

    private fun showCamera() {
        val intent = Intent(this, RecorderService::class.java)
        intent.action = Config.ACTION_SHOW_CAMERA
        startService(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_VIEW -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) showCamera()
        }
    }

    companion object {
        private const val REQUEST_CAMERA_VIEW = 829
        private const val REQUEST_SETTING_OVERLAY_PERMISSION = 830
        fun isSystemAlertPermissionGranted(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT < 23) {
                true
            } else Settings.canDrawOverlays(context)
        }
    }
}