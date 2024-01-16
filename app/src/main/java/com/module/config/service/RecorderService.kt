package com.module.config.service

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.akexorcist.localizationactivity.ui.LocalizationService
import com.module.config.rx.CallBackRxBus
import com.module.config.rx.CallbackEventView
import com.module.config.rx.RxBus
import com.module.config.rx.RxBusType
import com.module.config.rx.RxBusType.*
import com.module.config.service.service_floating.floating.*
import com.module.config.service.service_floating.layout.LayoutPreviewMediaManager
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.utils.utils_controller.ScreenRecordHelper
import com.module.config.utils.utils_controller.Toolbox
import com.module.config.views.activities.camera_view.CameraActivity
import com.module.config.views.activities.record.RecordActivity
import com.module.config.views.activities.splash.SplashActivity
import com.module.config.views.activities.tool.ToolsActivity
import io.reactivex.rxjava3.disposables.Disposable

class RecorderService : LocalizationService(), CallbackEventView {
    private var floatingRecordManager: FloatingRecordManager? = null
    private var floatingCameraViewManager: FloatingCameraViewManager? = null
    private var rxBusDisposable: Disposable? = null
    private var mResultData: Intent? = null
    private var mResultCode = -100
    private var floatingMainManager: FloatingMainManager? = null
    private var serviceNotificationManager: ServiceNotificationManager? = null
    private var floatingScreenShotManager: FloatingScreenShotManager? = null
    private var floatingBrushManager: FloatingBrushManager? = null

    override fun onCreate() {
        super.onCreate()
        initRxBus()
//        floatingMainManager = FloatingMainManager(this)
        serviceNotificationManager = ServiceNotificationManager(this)
        floatingScreenShotManager = FloatingScreenShotManager(this)
        floatingBrushManager = FloatingBrushManager(this)
        serviceNotificationManager?.showMainNotification(this)
        if (PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_SCREEN_SHOT, false)) {
            showHideFloatingViewScreenShot(true)
        }
        if (PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false)) {
            showHideFloatingViewCamera(true)
        }
        if (PreferencesHelper.getBoolean(PreferencesHelper.PREFS_TOOLS_BRUSH, false)) {
            showHideFloatingViewBrush(true)
        }

    }

    private fun initRxBus() {
        rxBusDisposable = RxBus.instance?.subscribe(CallBackRxBus(this))
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                Config.ACTION_SHOW_MAIN_FLOATING -> {
                    Log.e("CHECKFLOAT", "show float")
                    if (PreferencesHelper.getBoolean(
                            PreferencesHelper.KEY_FLOATING_CONTROL,
                            false
                        )
                    ) {
                        Log.e("CHECKFLOAT", "switch: ${PreferencesHelper.getBoolean(
                            PreferencesHelper.KEY_FLOATING_CONTROL,
                            false
                        )}")
                        actionShowMain()
                    }
                    serviceNotificationManager?.showMainNotification(this)
                }
                Config.ACTION_DISABLE_FLOATING -> {
                    Log.e("CHECKFLOAT", "disable float")
                    disableFloating()
                }

                Config.ACTION_SHOW_TOOLS -> {
                    Log.e("CHECKFLOAT", "show tool")
                    floatingMainManager?.showTools()
                }
                Config.ACTION_SHOW_CAMERA -> try {
                    Log.e("CHECKFLOAT", "show camera")
                    floatingCameraViewManager = FloatingCameraViewManager(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Config.ACTION_SCREEN_SHOT_START -> actionScreenShot(intent)
                Config.ACTION_SCREEN_RECORDING_START -> actionRecord(intent)
                Config.ACTION_STOP_SHAKE -> if (floatingRecordManager != null) {
                    stopRecord()
                    floatingRecordManager?.stopShakeManager()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun disableFloating() {
        if (floatingRecordManager != null) {
            floatingRecordManager?.onFinishFloatingView()
        }
        if (floatingCameraViewManager != null) {
            floatingCameraViewManager?.onFinishFloatingView()
        }
        floatingScreenShotManager?.onFinishFloatingView()
        floatingBrushManager?.onFinishFloatingView()
        floatingMainManager?.onFinishFloatingView()
    }

    private fun actionShowMain() {
        try {
            Log.e("CHECKFLOAT", "actionShowMain \n ______________ \n")
            floatingMainManager = FloatingMainManager(this)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun actionScreenShot(intent: Intent) {
        val resultData = intent.getParcelableExtra<Intent>(Config.KEY_SCREEN_SHOT_INTENT)
        val resultCode = intent.getIntExtra(Config.KEY_SCREEN_SHOT_RESULT_CODE, Activity.RESULT_OK)
        floatingScreenShotManager?.captureScreen(resultData, resultCode)
    }

    private fun showHideFloatingViewScreenShot(isShow: Boolean) {
        if (isShow) {
            floatingScreenShotManager?.addFloatingView()
        } else {
            floatingScreenShotManager?.onFinishFloatingView()
        }
    }

    private fun showHideFloatingViewCamera(isShow: Boolean) {
        if (isShow) {
            val intent = Intent(this, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Toolbox.startActivityAllStage(this, intent)
        } else {
            if (floatingCameraViewManager != null) {
                floatingCameraViewManager?.onFinishFloatingView()
            }
        }
    }

    private fun showHideFloatingViewBrush(isShow: Boolean) {
        if (isShow) {
            floatingBrushManager?.addFloatingView()
        } else {
            floatingBrushManager?.onFinishFloatingView()
        }
    }

    private fun actionRecord(intent: Intent) {
        floatingMainManager?.showFloatingView()
        if (mResultData == null) {
            mResultData = intent.getParcelableExtra<Intent>(Config.KEY_SCREEN_RECORD_INTENT)
        }
        if (mResultCode == -100) {
            mResultCode =
                intent.getIntExtra(Config.KEY_SCREEN_RECORD_RESULT_CODE, Activity.RESULT_OK)
        }
        floatingRecordManager = floatingMainManager?.windowParamsFloatingView?.let { param ->
            FloatingRecordManager(
                this,
                mResultData!!,
                mResultCode,
                param.x,
                param.y
            )
        }
    }

    private fun actionRecord() {
        floatingMainManager?.showFloatingView()
        floatingRecordManager = floatingMainManager?.windowParamsFloatingView?.let { param ->
            FloatingRecordManager(
                this,
                mResultData!!,
                mResultCode,
                param.x,
                param.y
            )
        }
    }

    private fun screenRecordSuccess() {
        if (floatingRecordManager != null) {
            serviceNotificationManager?.showMainNotification(this)
            floatingMainManager?.updatePositionFloatingView(
                floatingRecordManager?.windowParamsFloatingView?.x ?: 0,
                floatingRecordManager?.windowParamsFloatingView?.y ?: 0
            )
            floatingMainManager?.showFloatingView()
        }
    }

    private fun pauseOrPlayRecord(data: Any) {
        val state: ScreenRecordHelper.State = data as ScreenRecordHelper.State
        if (state === ScreenRecordHelper.State.PAUSED) {
            serviceNotificationManager?.showPausedNotification()
        } else if (state === ScreenRecordHelper.State.RECORDING) {
            serviceNotificationManager?.showRecordingNotification()
        }
    }

    private fun pauseRecord() {
        if (floatingRecordManager != null) {
            floatingRecordManager?.pauseOrPlay()
        }
        serviceNotificationManager?.showPausedNotification()
    }

    private fun resumeRecord() {
        if (floatingRecordManager != null) {
            floatingRecordManager?.pauseOrPlay()
        }
        serviceNotificationManager?.showRecordingNotification()
    }

    private fun stopRecord() {
        if (floatingRecordManager != null) {
            floatingRecordManager?.stopRecording()
        }
    }

    private fun startScreenshot() {
        floatingMainManager?.hideFloatingView()
        floatingScreenShotManager?.hideFloatingView()
        floatingBrushManager?.hideFloatingView()
    }

    private fun screenshotSuccess() {
        floatingMainManager?.showFloatingView()
        floatingScreenShotManager?.showFloatingView()
        floatingBrushManager?.showFloatingView()
        floatingBrushManager?.onRemoveLayoutBrush()
    }

    private fun showPreviewMedia(path: String) {
        if (TextUtils.isEmpty(path)) return
        if (path.endsWith(".mp4")) {
            serviceNotificationManager?.showScreenRecordSuccessNotification(path)
        } else {
            serviceNotificationManager?.showScreenshotSuccessNotification(path)
        }
        Toolbox.scanFile(this, path)
        LayoutPreviewMediaManager(this, path)
    }

    override fun onReceivedEvent(type: RxBusType?, data: Any?) {
        when (type) {
            STATE_PAUSE_OR_PLAY -> pauseOrPlayRecord(data!!)
            SCREEN_SHOT -> {
                screenshotSuccess()
                showPreviewMedia(data as String)
            }
            START_SCREEN_SHOT -> startScreenshot()
            SCREEN_RECORD_SUCCESS -> {
                if (data != null) {
                    showPreviewMedia(data as String)
                }
                screenRecordSuccess()
            }
            TOOLS_SCREEN_SHOT -> showHideFloatingViewScreenShot(data as Boolean)
            TOOLS_CAMERA -> showHideFloatingViewCamera(data as Boolean)
            TOOLS_BRUSH -> showHideFloatingViewBrush(data as Boolean)
            CLICK_SCREEN_SHOT_BRUSH -> floatingScreenShotManager?.startCaptureScreen()
            CLICK_NOTIFICATION_EXIT -> stopSelf()
            CLICK_NOTIFICATION_HOME -> openHome()
            CLICK_NOTIFICATION_TOOLS -> openToolsActivity()
            CLICK_NOTIFICATION_SCREEN_SHOT -> floatingScreenShotManager?.startCaptureScreen()
            CLICK_NOTIFICATION_SCREEN_RECORD, RECORD -> openRecord()
            CLICK_NOTIFICATION_PAUSE -> pauseRecord()
            CLICK_NOTIFICATION_RESUME -> resumeRecord()
            CLICK_NOTIFICATION_STOP -> stopRecord()
            else -> {}
        }
    }

    fun isOpenApp(activity: Context, myPackageName: String): Boolean {
        val manager = activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo[0].topActivity
        return myPackageName == componentInfo!!.packageName
    }

    private fun openHome() {
        if (!isOpenApp(this, packageName)) {
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Toolbox.startActivityAllStage(this, intent)
        }
    }

    private fun openToolsActivity() {
        val intent = Intent(this, ToolsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        Toolbox.startActivityAllStage(this, intent)
    }

    private fun openRecord() {
        if (mResultData == null) {
            val intent = Intent(this, RecordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Toolbox.startActivityAllStage(this, intent)
        } else {
            actionRecord()
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(serviceNotificationManager?.receiver)
            if (rxBusDisposable != null) {
                rxBusDisposable!!.dispose()
            }
            if (floatingRecordManager != null) {
                floatingRecordManager?.onFinishFloatingView()
            }
            floatingScreenShotManager?.onFinishFloatingView()
            floatingBrushManager?.onFinishFloatingView()
            floatingMainManager?.onFinishFloatingView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
