package com.module.config.utils.utils_controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.module.config.rx.RxBusHelper.sendScreenShot
import java.nio.Buffer

class ScreenShotHelper(
    private val context: Context,
    windowManager: WindowManager,
    metrics: DisplayMetrics
) {
    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mImageReader: ImageReader? = null
    private var mDensity = 0
    private var mDisplay: Display? = null
    private var mHeight = 0
    private var mWidth = 0

    init {
        initDisplay(windowManager, metrics)
    }

    private fun initDisplay(windowManager: WindowManager, metrics: DisplayMetrics) {
        mDensity = metrics.densityDpi
        mDisplay = windowManager.defaultDisplay
    }

    fun captureScreen(resultData: Intent?, resultCode: Int) {
        if (!(resultCode == 0 || resultData == null)) {
            if (mMediaProjection != null) {
                tearDownMediaProjection()
            }
            mMediaProjectionManager =
                context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            if (mMediaProjection == null) {
                mMediaProjection =
                    mMediaProjectionManager!!.getMediaProjection(resultCode, resultData)
            }
            if (mMediaProjection != null) {
                setUpVirtualDisplay()
            }
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setUpVirtualDisplay() {
        val point = Point()
        mDisplay!!.getSize(point)
        mWidth = point.x
        mHeight = point.y
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2)
        mVirtualDisplay = mMediaProjection!!.createVirtualDisplay(
            "Screenrecorder",
            mWidth,
            mHeight,
            mDensity,
            9,
            mImageReader!!.surface,
            null,
            null
        )
        mImageReader!!.setOnImageAvailableListener(ImageAvailableListener(), null)
    }

    inner class ImageAvailableListener :
        ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(imageReader: ImageReader) {
            var filePath: String? = null
            var createBitmap: Bitmap? = null
            var acquireLatestImage: Image?
            try {
                acquireLatestImage = mImageReader!!.acquireLatestImage()
                if (acquireLatestImage != null) {
                    try {
                        val planes = acquireLatestImage.planes
                        val buffer: Buffer = planes[0].buffer
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding = rowStride - pixelStride * mWidth
                        createBitmap = Bitmap.createBitmap(
                            mWidth + rowPadding / pixelStride,
                            mHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        try {
                            createBitmap.copyPixelsFromBuffer(buffer)
                            if (acquireLatestImage != null) {
                                acquireLatestImage.close()
                            }
                            try {
                                filePath = Storage.saveImage(
                                    context,
                                    Toolbox.CropBitmapTransparency(
                                        Bitmap.createBitmap(
                                            createBitmap,
                                            0,
                                            0,
                                            mWidth,
                                            mHeight
                                        )
                                    )
                                )
                                acquireLatestImage.close()
                                stopScreenCapture()
                                tearDownMediaProjection()
                            } catch (e: Exception) {
                                try {
                                    e.printStackTrace()
                                    if (imageReader != null) {
                                        try {
                                            imageReader.close()
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                        }
                                    }
                                    createBitmap?.recycle()
                                    if (acquireLatestImage == null) {
                                        return
                                    }
                                    acquireLatestImage.close()
                                } catch (th2: Throwable) {
                                    createBitmap?.recycle()
                                    if (acquireLatestImage != null) {
                                        acquireLatestImage.close()
                                    }
                                }
                            } catch (th3: Throwable) {
                                createBitmap?.recycle()
                                if (acquireLatestImage != null) {
                                    acquireLatestImage.close()
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            createBitmap?.recycle()
                            if (acquireLatestImage == null) {
                                return
                            }
                            acquireLatestImage.close()
                        }
                    } catch (ex: Exception) {
                        createBitmap = null
                        ex.printStackTrace()
                        if (createBitmap != null) {
                            createBitmap.recycle()
                        }
                        if (acquireLatestImage == null) {
                            return
                        }
                        acquireLatestImage.close()
                    } catch (th4: Throwable) {
                        createBitmap = null
                        if (createBitmap != null) {
                            createBitmap.recycle()
                        }
                        if (acquireLatestImage != null) {
                            acquireLatestImage.close()
                        }
                    }
                }
                createBitmap = null
                if (createBitmap != null) {
                    createBitmap.recycle()
                }
                acquireLatestImage?.close()
            } catch (ex: Exception) {
                acquireLatestImage = null
                ex.printStackTrace()
                createBitmap?.recycle()
                if (acquireLatestImage == null) {
                    return
                }
                acquireLatestImage.close()
            } finally {
                sendScreenShot(filePath)
            }
        }
    }

    private fun stopScreenCapture() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
            if (mImageReader != null) {
                mImageReader!!.setOnImageAvailableListener(null, null)
            }
        }
    }

    private fun tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }
}