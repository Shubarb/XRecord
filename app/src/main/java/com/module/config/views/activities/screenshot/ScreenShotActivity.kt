package com.module.config.views.activities.screenshot

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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.module.config.rx.RxBusHelper
import com.module.config.service.RecorderService
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.views.activities.GuidePermissionActivity

class ScreenShotActivity : AppCompatActivity() {
    private var mMediaProjectionManager: MediaProjectionManager? = null
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager =
                getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun activeScreenCapture() {
        startActivityForResult(
            mMediaProjectionManager!!.createScreenCaptureIntent(),
            REQUEST_MEDIA_PROJECTION
        )
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
                activeScreenCapture()
            }
        } else {
            activeScreenCapture()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) activeScreenCapture()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_MEDIA_PROJECTION -> {
                if (resultCode != RESULT_OK) {
                    RxBusHelper.sendScreenShot("")
                    Handler().postDelayed({ finish() }, 200)
                    return
                }
                Handler().postDelayed({
                    startCaptureScreen(
                        resultCode,
                        intent
                    )
                }, 200)
                finish()
            }
            REQUEST_SETTING_OVERLAY_PERMISSION -> Handler().postDelayed(
                {
                    if (isSystemAlertPermissionGranted(this@ScreenShotActivity)) {
                        PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
                        askPermissionStorage()
                    } else {
                        finish()
                    }
                }, 200
            )
        }
    }

    private fun startCaptureScreen(resultcode: Int, intentData: Intent?) {
        val intent = Intent(this, RecorderService::class.java)
        intent.action = Config.ACTION_SCREEN_SHOT_START
        intent.putExtra(Config.KEY_SCREEN_SHOT_RESULT_CODE, resultcode)
        intent.putExtra(Config.KEY_SCREEN_SHOT_INTENT, intentData)
        startService(intent)
    }

    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 826
        private const val REQUEST_STORAGE = 827
        private const val REQUEST_SETTING_OVERLAY_PERMISSION = 830
        fun isSystemAlertPermissionGranted(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT < 23) {
                true
            } else Settings.canDrawOverlays(context)
        }
    }
}
