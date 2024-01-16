package com.module.config.service.service_floating.layout

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.service.service_floating.base_window.BaseLayoutWindowManager
import com.module.config.utils.utils_controller.ViewUtils
import com.module.config.utils.utils_controller.notification.ServiceNotificationManager
import com.module.config.views.activities.preview_media.PreviewVideoActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LayoutPreviewMediaManager(private val mContext: Context, private val filePath: String) :
    BaseLayoutWindowManager(mContext) {
    private var fileUri: Uri? = null
    private var isVideo = false
    private var imgAvatarVideo: ImageView? = null

    init {
        initParams()
        addLayout()
        initData()
    }

    private fun initParams() {
        mParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun getRootViewId() = R.layout.dialog_result_recorder

    override fun initLayout() {
        imgAvatarVideo = rootView?.findViewById(R.id.thumbnail)
        (rootView?.findViewById(R.id.imv_close) as AppCompatImageView).setOnClickListener { v -> removeLayout() }
        (rootView?.findViewById(R.id.imv_play) as AppCompatImageView).setOnClickListener { v -> openMedia() }
        (rootView?.findViewById(R.id.tv_share) as TextView).setOnClickListener { v -> shareMedia() }
        (rootView?.findViewById(R.id.tv_edit) as TextView).setOnClickListener { v -> openEditVideo() }
        (rootView?.findViewById(R.id.tv_delete) as TextView).setOnClickListener { v -> deleteMedia() }
        (rootView?.findViewById(R.id.thumbnail) as RoundedImageView).setOnClickListener { v -> openMedia() }
        ViewUtils.scaleSelected(
            rootView?.findViewById(R.id.imv_close),
            rootView?.findViewById(R.id.imv_play),
            rootView?.findViewById(R.id.tv_share),
            rootView?.findViewById(R.id.tv_edit),
            rootView?.findViewById(R.id.tv_delete)
        )
        Glide.with(mContext)
            .load(filePath)
            .error(R.drawable.shape_imv_preview)
            .into(imgAvatarVideo!!)
    }

    private fun deleteMedia() {
        ServiceNotificationManager.getInstance(mContext)?.hideScreenRecordSuccessNotification()
        ServiceNotificationManager.getInstance(mContext)?.hideScreenshotSuccessNotification()
        RxBusHelper.sendNotiMediaChange()
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        removeLayout()
    }

    private fun openEditVideo() {
//        removeLayout()
//        val intent = Intent(mContext, VideoTrimActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.putExtra(Config.EXTRA_PATH, filePath)
//        mContext.startActivity(intent)
    }

    private fun shareMedia() {
        removeLayout()
        val shareIntent = Intent()
            .setAction(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_STREAM, fileUri)
            .setType(if (isVideo) "video/mp4" else "image/*")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mContext.startActivity(shareIntent)
    }

    private fun openMedia() {
        removeLayout()
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy--MM-yy")
            val file = File(filePath)
            val dateFile = Date(file.lastModified())
            try {
                val intent = Intent(
                    mContext,
                    PreviewVideoActivity::class.java
                )
                intent.putExtra("Uri_video", filePath)
                intent.putExtra("Name_video", file.name)
                intent.putExtra("Date_video", simpleDateFormat.format(dateFile))
                intent.putExtra("Size_video", file.length())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                mContext.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(mContext, "Open Error", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initData() {
        try {
            if (filePath.endsWith(".mp4")) {
                isVideo = true
            } else {
                isVideo = false
                (rootView?.findViewById(R.id.imv_play) as AppCompatImageView).visibility = View.GONE
                (rootView?.findViewById(R.id.tv_edit) as TextView).visibility = View.GONE
                (rootView?.findViewById(R.id.tv_title) as TextView).text =
                    mContext.getString(R.string.screenshot_finished)
            }
            fileUri = FileProvider.getUriForFile(
                mContext, mContext.packageName + ".provider",
                File(filePath)
            )
            Glide.with(mContext).load(filePath)
                .into((rootView?.findViewById(R.id.thumbnail) as ImageView))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}