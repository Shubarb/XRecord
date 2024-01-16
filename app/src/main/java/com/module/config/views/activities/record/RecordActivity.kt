package com.module.config.views.activities.record

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.module.config.service.RecorderService
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.views.activities.GuidePermissionActivity

class RecordActivity : AppCompatActivity() {
    private var mProjectionManager: MediaProjectionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        askPermissionOverlay()
    }

    fun askPermissionOverlay() {
        if (isSystemAlertPermissionGranted(this)) {
            PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
            askPermissionStorage()
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

    private fun askPermissionStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQUEST_STORAGE
                )
            } else {
                activeScreenRecord()
            }
        } else {
            activeScreenRecord()
        }
    }

    private fun activeScreenRecord() {
        startActivityForResult(
            mProjectionManager!!.createScreenCaptureIntent(),
            SCREEN_RECORD_REQUEST_CODE
        )
    }

    private fun startRecordScreen(resultcode: Int, intentData: Intent?) {
        val intent = Intent(this, RecorderService::class.java)
        intent.action = Config.ACTION_SCREEN_RECORDING_START
        intent.putExtra(Config.KEY_SCREEN_RECORD_INTENT, intentData)
        intent.putExtra(Config.KEY_SCREEN_RECORD_RESULT_CODE, resultcode)
        startService(intent)
        Handler().postDelayed({ finishAffinity() }, 2000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) activeScreenRecord()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SCREEN_RECORD_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                startRecordScreen(resultCode, data)
            } else {
                finish()
            }
            REQUEST_SETTING_OVERLAY_PERMISSION -> Handler().postDelayed(
                {
                    if (isSystemAlertPermissionGranted(this@RecordActivity)) {
                        PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
                        askPermissionStorage()
                    } else {
                        finish()
                    }
                }, 200
            )
        }
    }

    companion object {
        private const val SCREEN_RECORD_REQUEST_CODE = 832
        private const val REQUEST_STORAGE = 827
        private const val REQUEST_SETTING_OVERLAY_PERMISSION = 829
        fun isSystemAlertPermissionGranted(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT < 23) {
                true
            } else Settings.canDrawOverlays(context)
        }
    }
}