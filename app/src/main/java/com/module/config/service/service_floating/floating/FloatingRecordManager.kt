package com.module.config.service.service_floating.floating

import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.service.service_floating.base_window.BaseFloatingManager
import com.module.config.service.service_floating.layout.*
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.utils.utils_controller.ScreenRecordHelper
import com.module.config.utils.utils_controller.ShakeEventManager
import com.module.config.utils.utils_controller.Toolbox
import com.module.config.utils.utils_controller.notification.ServiceNotificationManager
import jp.com.lifestyle.floating.view.FloatingViewManager
import jp.com.lifestyle.floating.view.FloatingViewManager.CallBackStateFloating

class FloatingRecordManager(
    context: Context?,
    private val mResultData: Intent,
    private val mResultCode: Int,
    private val initX: Int,
    private val initY: Int
) :
    BaseFloatingManager(context!!) {
    private var layoutRecordRightManager: LayoutRecordRightManager? = null
    private var layoutRecordLeftManager: LayoutRecordLeftManager? = null
    private var layoutBlurManager: LayoutBlurManager? = null
    private var screenRecordHelper: ScreenRecordHelper? = null
    private var tvTime: TextView? = null
    private var imvTime: ImageView? = null

    init {
        setUpOptionsFloating()
        initData()
    }

    override fun initLayout() {
        mFloatingViewManager?.isTrashViewEnabled = false
        tvTime = rootView?.findViewById(R.id.tv_time)
        imvTime = rootView?.findViewById(R.id.imv_time)
        rootView?.setOnClickListener { v -> initLayoutMainManager() }
    }

    override fun getRootViewId() =  R.layout.floating_view_record

    override fun setUpOptionsFloating() {
        super.setUpOptionsFloating()
        options?.isEnableAlpha = true
        options?.overMargin = 16
        options?.floatingViewWidth = context.resources.getDimension(R.dimen.size_floating_icon).toInt()
        options?.floatingViewHeight = context.resources.getDimension(R.dimen.size_floating_icon).toInt()
        options?.floatingViewX = initX
        options?.floatingViewY = initY
        mFloatingViewManager?.setCallBackStateFloating(object : CallBackStateFloating {
            override fun onCollap() {
                setCollapsedIcon()
            }

            override fun onNormal() {
                imvTime!!.setImageResource(R.drawable.img_shape_floating)
                tvTime!!.visibility = View.VISIBLE
            }
        })
    }

    private fun initData() {
        screenRecordHelper = ScreenRecordHelper(context, metrics, object :
            ScreenRecordHelper.CallBackRecordHelper {
            override fun onTimeRun(time: Int) {
                setTime(time)
            }

            override fun onStopScreenRecording(dstPath: String?) {
                RxBusHelper.sendScreenRecordSuccess(dstPath)
                removeAllView()
            }

            override fun onError() {
                RxBusHelper.sendScreenRecordSuccess(null)
                removeAllView()
            }
        })
        // shake device to start/stop record
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_SHAKE, false)) {
            ServiceNotificationManager.getInstance(context)?.showShakeNotification()
            ShakeEventManager.getInstance(context).setListener {
                if (ScreenRecordHelper.STATE !== ScreenRecordHelper.State.RECORDING) {
                    ServiceNotificationManager.getInstance(context)?.hideShakeNotification()
                    showTimer()
                } else {
                    stopRecording()
                    ShakeEventManager.getInstance(context).stop()
                }
            }
        } else {
            showTimer()
        }
    }

    fun showTimer() {
        LayoutTimerManager(context).callBackLayoutTimer(object :LayoutTimerManager.CallBack{
            override fun onTimeEnd() {
                startRecording()
            }

        })
    }

    fun stopShakeManager() {
        ShakeEventManager.getInstance(context).stop()
        removeAllView()
    }

    private fun vibrateWhenRecord() {
        val vibrate = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrate.vibrate(100)
    }

    private fun initLayoutMainManager() {
        if (mFloatingViewManager == null) return
        if (screenRecordHelper == null) return
        vibrateWhenClick()
        hideFloatingView()
        showBlur()
        mFloatingViewManager?.setStateWhenHideFloating()
        if (isFloatingViewInLeft) {
            layoutRecordLeftManager = mFloatingViewManager?.windowParamsFloatingView?.let {
                LayoutRecordLeftManager(
                    context,
                    it,
                    Toolbox.convertTime("${screenRecordHelper?.time}"),
                    object : LayoutRecordLeftManager.Callback {
                        override fun onClickLayout() {
                            clearStateShowMainLayout()
                        }

                        override fun onClickPauseOrPlay() {
                            pauseOrPlay()
                        }

                        override fun onClickTools() {
                            clearStateShowMainLayout()
                            showTools()
                        }

                        override fun onClickStop() {
                            stopRecording()
                        }
                    })
            }
        } else {
            layoutRecordRightManager = mFloatingViewManager?.windowParamsFloatingView?.let {
                LayoutRecordRightManager(
                    context,
                    it,
                    Toolbox.convertTime("${screenRecordHelper?.time}"),
                    object : LayoutRecordRightManager.Callback {
                        override fun onClickLayout() {
                            clearStateShowMainLayout()
                        }

                        override fun onClickPauseOrPlay() {
                            pauseOrPlay()
                        }

                        override fun onClickTools() {
                            clearStateShowMainLayout()
                            showTools()
                        }

                        override fun onClickStop() {
                            stopRecording()
                        }
                    })
            }
        }
    }

    fun pauseOrPlay() {
        if (screenRecordHelper != null) {
            val state: ScreenRecordHelper.State = screenRecordHelper?.togglePausePlay()!!
            RxBusHelper.sendUpdateSateNotificationRecord(state)
            layoutRecordRightManager?.setPauseOrResume(state)
            layoutRecordLeftManager?.setPauseOrResume(state)
        }
    }

    fun stopRecording() {
        if (screenRecordHelper != null) {
            screenRecordHelper?.stopScreenRecording()
        } else {
            RxBusHelper.sendScreenRecordSuccess(null)
            removeAllView()
        }
    }

    private fun showBlur() {
        layoutBlurManager = LayoutBlurManager(context) { clearStateShowMainLayout() }
    }

    fun showTools() {
        LayoutToolsManager(context)
    }

    private fun setTime(time: Int) {
        if (screenRecordHelper == null) return
        val timeString: String = Toolbox.convertTime(time.toString())
        tvTime!!.text = timeString
        layoutRecordRightManager?.setTime(timeString)
        layoutRecordLeftManager?.setTime(timeString)
    }

    fun removeAllView() {
        clearStateShowMainLayout()
        removeFloatingView()
    }

    private fun clearStateShowMainLayout() {
        showFloatingView()
        if (mFloatingViewManager != null) {
            mFloatingViewManager?.goToAlpha()
        }
        layoutBlurManager?.removeLayout()
        layoutRecordRightManager?.removeLayout()
        layoutRecordLeftManager?.removeLayout()
    }

    private val isFloatingViewInLeft: Boolean
        private get() = if (mFloatingViewManager == null) false else mFloatingViewManager?.xFloatingView!! < metrics?.widthPixels!! / 2

    private fun startRecording() {
        ServiceNotificationManager.getInstance(context)?.showRecordingNotification()
        addFloatingView()
        FloatingMainManager(context).hideFloatingView()
        if (PreferencesHelper.getBoolean(PreferencesHelper.KEY_TARGET_APP, false)) {
            startAppBeforeRecording()
        }
        vibrateWhenRecord()
        screenRecordHelper?.startRecording(mResultCode, mResultData)
    }

    private fun startAppBeforeRecording() {
        val packagename: String =
            PreferencesHelper.getString(PreferencesHelper.KEY_APP_SELECTED, "")
        if (!TextUtils.isEmpty(packagename)) {
            val startAppIntent: Intent =
                context.packageManager.getLaunchIntentForPackage(packagename)!!
            context.startActivity(startAppIntent)
        }
    }

    fun setCollapsedIcon() {
        tvTime!!.visibility = View.GONE
        imvTime!!.visibility = View.VISIBLE
        val layoutParams = imvTime!!.layoutParams as FrameLayout.LayoutParams
        if (isFloatingViewInLeft) {
            layoutParams.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            imvTime!!.layoutParams = layoutParams
            imvTime!!.setImageResource(R.drawable.shape_dot_left)
        } else {
            layoutParams.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            imvTime!!.layoutParams = layoutParams
            imvTime!!.setImageResource(R.drawable.shape_dot_right)
        }
        mFloatingViewManager?.setCurrentState(FloatingViewManager.STATE.COLLAPSED)
    }
}
