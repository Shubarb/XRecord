package com.module.config.views.activities.preview_media

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.module.config.R
import com.module.config.utils.utils_controller.SettingUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {
    private var imgBackground: SubsamplingScaleImageView? = null
    private var imgExitOpenFileAct: ImageView? = null
    private var tvNameImage: TextView? = null
    private var lnTop: LinearLayout? = null
    private var lnBottom: LinearLayout? = null
    private var lnShareImage: LinearLayout? = null
    private var lnInfoImage: LinearLayout? = null
    private var lnDeleteImage: LinearLayout? = null
    private var lnBanner: LinearLayout? = null
//    private var slBanner: ShimmerFrameLayout? = null
    private var isClickShowHide = true
    private var uriImage: String? = null
    private var nameImage: String? = null
    private var dateImage: String? = null
    private var sizeImage: String? = null
//    private var deleteFileDialog: DeleteVideoDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_image)
        initViews()
    }

    fun initViews() {
        imgBackground = findViewById<SubsamplingScaleImageView>(R.id.img_background_image)
        imgExitOpenFileAct = findViewById<ImageView>(R.id.img_exit_open_image_act)
        tvNameImage = findViewById<TextView>(R.id.tv_name_open_image_act)
        lnTop = findViewById<LinearLayout>(R.id.ln_image_top)
        lnBottom = findViewById<LinearLayout>(R.id.ln_image_bottom)
        lnShareImage = findViewById<LinearLayout>(R.id.ln_share)
        lnInfoImage = findViewById<LinearLayout>(R.id.ln_property)
        lnDeleteImage = findViewById<LinearLayout>(R.id.ln_delete)
//        lnBanner = findViewById<LinearLayout>(R.id.ln_banner_ad)
//        slBanner = findViewById<ShimmerFrameLayout>(R.id.inc_loading_banner)
        uriImage = intent.getStringExtra("Uri_image")
        nameImage = intent.getStringExtra("Name_image")
        dateImage = intent.getStringExtra("Date_image")
        sizeImage = intent.getStringExtra("Size_image")
        if (uriImage!!.startsWith("content://")) {
            imgBackground?.setImage(ImageSource.uri(Uri.parse(uriImage)))
        } else {
            imgBackground?.setImage(ImageSource.uri(uriImage!!))
        }
        imgBackground?.setOrientation(-1)
        tvNameImage?.setText(nameImage)
        initEvent()
        timeHideTopBottom(false)
//        BannerAM.initBannerAdMob(this, lnBanner, slBanner)
    }

    private fun initEvent() {
        imgExitOpenFileAct!!.setOnClickListener(this)
        imgBackground!!.setOnClickListener(this)
        lnShareImage!!.setOnClickListener(this)
        lnInfoImage!!.setOnClickListener(this)
        lnDeleteImage!!.setOnClickListener(this)
    }

    private fun timeHideTopBottom(isShowHide: Boolean) {
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Long?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(s: Long?) {
                    if (s == 5L) {
                        if (!isFinishing && !isDestroyed) {
                            hideView(lnTop, isShowHide)
                            hideView(lnBottom, isShowHide)
                            isClickShowHide = true
                        }
                    }
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    private fun showTopBottom() {
        isClickShowHide = false
        hideView(lnTop, true)
        hideView(lnBottom, true)
        timeHideTopBottom(false)
    }

    private fun hideTopBottom() {
        hideView(lnTop, false)
        hideView(lnBottom, false)
        isClickShowHide = true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.img_exit_open_image_act -> finish()
            R.id.img_background_image -> if (isClickShowHide) {
                showTopBottom()
            } else {
                hideTopBottom()
            }
            R.id.ln_share -> SettingUtils.funcShareFile(this, uriImage)
            R.id.ln_property -> {
//                val infoDialog = InfoDialog(this, nameImage, dateImage, sizeImage)
//                infoDialog.show()
            }
            R.id.ln_delete -> {
//                deleteFileDialog = DeleteVideoDialog(this)
//                deleteFileDialog.setOnCLickDeleteDialog(object : onCLickDeleteDialog() {
//                    fun onCLickCancel() {
//                        deleteFileDialog.dismiss()
//                    }
//
//                    fun onCLickDelete() {
//                        SettingUtils.deleteVideo(this@PreviewImageActivity, uriImage)
//                        if (deleteFileDialog != null) deleteFileDialog.dismiss()
//                        finish()
//                    }
//                })
//                if (deleteFileDialog != null) deleteFileDialog.show()
            }
            else -> {}
        }
    }

    private fun hideView(view: View?, isShow: Boolean) {
        val f2 = if (isShow) 0.0f else 1.0f
        val ofFloat = ObjectAnimator.ofFloat(view, "alpha", *floatArrayOf(f2, 1.0f - f2))
        val animatorSet = AnimatorSet()
        animatorSet.duration = 500
        animatorSet.play(ofFloat)
        animatorSet.start()
    }

    override fun onBackPressed() {
        finish()
    }
}
