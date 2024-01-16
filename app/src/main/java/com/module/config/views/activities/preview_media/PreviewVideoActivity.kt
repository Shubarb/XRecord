package com.module.config.views.activities.preview_media

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.module.config.R
import com.module.config.utils.utils_controller.SettingUtils

class PreviewVideoActivity : AppCompatActivity(),
    View.OnClickListener {
    private var vvOpenVideo: PlayerView? = null
    private var tvNameVideo: TextView? = null
    private var imgExitVideoAct: ImageView? = null
    private var imgMenuVideo: ImageView? = null
    private var lnBanner: LinearLayout? = null
//    private var slBanner: ShimmerFrameLayout? = null
    private var pathVideo: String? = null
    private var nameVideo: String? = null
    private var dateVideo: String? = null
    private var sizeVideo: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_video)
        initViews()
        initEvent()
    }

    private fun initViews() {
        if (Build.VERSION.SDK_INT >= 28) {
            window.attributes.layoutInDisplayCutoutMode = 1
        }
        imgExitVideoAct = findViewById<ImageView>(R.id.img_exit_open_image_act)
        vvOpenVideo = findViewById(R.id.video_view_open)
        tvNameVideo = findViewById<TextView>(R.id.tv_name_open_video_act)
        imgMenuVideo = findViewById<ImageView>(R.id.img_menu_video)
//        lnBanner = findViewById<LinearLayout>(R.id.ln_banner_ad)
//        slBanner = findViewById<ShimmerFrameLayout>(R.id.inc_loading_banner)
//        BannerAM.initBannerAdMob(this, lnBanner, slBanner)
        pathVideo = intent.getStringExtra("Uri_video")
        nameVideo = intent.getStringExtra("Name_video")
        dateVideo = intent.getStringExtra("Date_video")
        sizeVideo = intent.getStringExtra("Size_video")
        tvNameVideo?.setText(nameVideo)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(vvOpenVideo)
        startPlayingVideo(pathVideo)
    }

    private fun startPlayingVideo(CONTENT_URL: String?) {
        val trackSelectorDef: TrackSelector = DefaultTrackSelector()
        val absPlayerInternal: SimpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef)
        val userAgent: String = Util.getUserAgent(this, this.packageName)
        val defdataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val uriOfContentUrl = Uri.parse(CONTENT_URL)
        val mediaSource: MediaSource =
            ProgressiveMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl)
        absPlayerInternal.prepare(mediaSource)
        absPlayerInternal.setPlayWhenReady(true)
        vvOpenVideo?.setPlayer(absPlayerInternal)
    }

    private fun initEvent() {
        imgExitVideoAct!!.setOnClickListener(this)
        imgMenuVideo!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.img_exit_open_image_act -> finish()
            R.id.img_menu_video -> showPopupMenu()
            else -> {}
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(
            this,
            imgMenuVideo!!
        )
        popupMenu.menuInflater.inflate(R.menu.menu_detail_video, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_share -> SettingUtils.funcShareFile(this@PreviewVideoActivity, pathVideo)
                R.id.item_delete -> {
//                    val deleteVideoDialog = DeleteVideoDialog(this@PreviewVideoActivity)
//                    deleteVideoDialog.setOnCLickDeleteDialog(object : onCLickDeleteDialog() {
//                        fun onCLickCancel() {
//                            deleteVideoDialog.dismiss()
//                        }
//
//                        fun onCLickDelete() {
//                            SettingUtils.deleteVideo(this@PreviewVideoActivity, pathVideo)
//                            deleteVideoDialog.dismiss()
//                        }
//                    })
//                    deleteVideoDialog.show()
                }
            }
            true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupMenu.gravity = Gravity.CENTER
        }
        popupMenu.show()
    }

    override fun onBackPressed() {
        finish()
    }
}
