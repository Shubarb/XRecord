package com.module.config.service.service_floating.floating

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.master.camera_app.CameraView
import com.module.config.R
import com.module.config.utils.utils_controller.PreferencesHelper

class FloatingCameraViewManager(private val context: Context) {
    private var mFloatingView: LinearLayout? = null
    private var imvResizeOverlay: ImageView? = null
    private var imvHideCamera: ImageView? = null
    private var imvSwitchCamera: ImageView? = null
    private var cameraView: CameraView? = null
    private var mWindowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private val handler: Handler = Handler()
    private val runnable = Runnable {
        imvResizeOverlay!!.visibility = View.GONE
        imvHideCamera!!.visibility = View.GONE
        imvSwitchCamera!!.visibility = View.GONE
    }

    init {
        initLayout()
    }

    private fun initLayout() {
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mFloatingView = li.inflate(R.layout.floating_camera_view, null) as LinearLayout?
        cameraView = mFloatingView!!.findViewById<CameraView>(R.id.cameraView)
        imvHideCamera = mFloatingView!!.findViewById<ImageView>(R.id.imv_hide_camera)
        imvSwitchCamera = mFloatingView!!.findViewById<ImageView>(R.id.imv_switch_camera)
        imvResizeOverlay = mFloatingView!!.findViewById<ImageView>(R.id.imv_overlay_resize)
        val xPos = xPos
        val yPos = yPos
        imvSwitchCamera?.setOnClickListener(View.OnClickListener { v: View? ->
            if (cameraView?.facing == CameraView.FACING_BACK) {
                cameraView?.facing = CameraView.FACING_FRONT
                cameraView?.autoFocus = true
            } else {
                cameraView?.facing = CameraView.FACING_BACK
                cameraView?.autoFocus = true
            }
        })
        imvHideCamera?.setOnClickListener(View.OnClickListener { v: View? -> onFinishFloatingView() })

        //Add the view to the window.
        params = WindowManager.LayoutParams(
            context.resources.getDimension(R.dimen._136sdp).toInt(),
            context.resources.getDimension(R.dimen._136sdp).toInt(),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_PHONE else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //Specify the view position
        params!!.gravity = Gravity.TOP or Gravity.START
        params!!.x = xPos
        params!!.y = yPos

        //Add the view to the window
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mFloatingView, params)
        cameraView?.start()
        setupDragListener()
    }

    private fun setupDragListener() {
        mFloatingView!!.setOnTouchListener(object : View.OnTouchListener {
            private val paramsF = params
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (imvResizeOverlay!!.isShown) {
                            imvResizeOverlay!!.visibility = View.GONE
                            imvHideCamera!!.visibility = View.GONE
                            imvSwitchCamera!!.visibility = View.GONE
                        } else {
                            imvResizeOverlay!!.visibility = View.VISIBLE
                            imvHideCamera!!.visibility = View.VISIBLE
                            imvSwitchCamera!!.visibility = View.VISIBLE
                            handler.removeCallbacks(runnable)
                        }
                        initialX = paramsF!!.x
                        initialY = paramsF.y
                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> handler.postDelayed(runnable, 3000)
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        val xDiff = (event.rawX - initialTouchX).toInt()
                        val yDiff = (event.rawY - initialTouchY).toInt()
                        paramsF!!.x = initialX + xDiff
                        paramsF.y = initialY + yDiff
                        /* Set an offset of 10 pixels to determine controls moving. Else, normal touches
                         * could react as moving the control window around */if (Math.abs(xDiff) > 10 || Math.abs(
                                yDiff
                            ) > 10
                        ) mWindowManager!!.updateViewLayout(mFloatingView, paramsF)
                        persistCoordinates(initialX + xDiff, initialY + yDiff)
                        return true
                    }
                }
                return false
            }
        })
        imvResizeOverlay!!.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.width
                        initialY = params!!.height
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> handler.postDelayed(runnable, 3000)
                    MotionEvent.ACTION_MOVE -> {
                        if (imvResizeOverlay!!.isShown) {
                            handler.removeCallbacks(runnable)
                        }
                        params!!.width = initialX + (event.rawX - initialTouchX).toInt()
                        params!!.height = initialY + (event.rawY - initialTouchY).toInt()
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    fun onFinishFloatingView() {
        if (cameraView != null) {
            cameraView!!.stop()
            cameraView = null
        }
        if (mFloatingView != null) {
            mWindowManager!!.removeViewImmediate(mFloatingView)
            mFloatingView = null
        }
        PreferencesHelper.putBoolean(PreferencesHelper.PREFS_TOOLS_CAMERA, false)
    }

    private val xPos: Int
        private get() {
            val pos = PreferenceManager.getDefaultSharedPreferences(
                context
            ).getString("POSITION", "0X100")
            return pos!!.split("X".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt()
        }
    private val yPos: Int
        private get() {
            val pos = PreferenceManager.getDefaultSharedPreferences(
                context
            ).getString("POSITION", "0X100")
            return pos!!.split("X".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt()
        }

    private fun persistCoordinates(x: Int, y: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString("POSITION", x.toString() + "X" + y)
            .apply()
    }
}
