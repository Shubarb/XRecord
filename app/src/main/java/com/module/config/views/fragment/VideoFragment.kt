package com.module.config.views.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.module.config.R
import com.module.config.databinding.FragmentVideoBinding
import com.module.config.models.VideoFile
import com.module.config.rx.RxBusType
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.utils.utils_controller.Storage
import com.module.config.utils.utils_controller.Toolbox
import com.module.config.utils.utils_controller.encoder.Mp4toGIFConverter
import com.module.config.views.activities.main.MainActivity
import com.module.config.views.activities.preview_media.PreviewVideoActivity
import com.module.config.views.activities.splash.SplashActivity
import com.module.config.views.bases.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoFragment : BaseFragment<FragmentVideoBinding>(), View.OnClickListener {
    private var videoAdapter: VideoAdapter? = null
    private var pos: Int = 0
    private var handler: Handler? = null
    var videoFile: VideoFile? = null

    override fun getLayoutFragment() = R.layout.fragment_video

    override fun initViews() {
        super.initViews()
        mBinding.swipeRefresh.setColorSchemeResources(
            R.color.color_accent,
            R.color.holo_green_dark,
            R.color.holo_orange_dark,
            R.color.holo_blue_dark
        )
        activity?.let { act ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(
                        act,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(
                        act,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED)
                ) {
                    data
                }
            } else {
                data
            }
        }
    }
    private val data: Unit
        @SuppressLint("CheckResult") private get() {
//            initPurchase()
            mBinding.progress.visibility = View.VISIBLE
            activity?.let { act ->
                Handler().postDelayed({
                    allFilesInTrim
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap<List<VideoFile?>?> { listFile: Array<File> ->
                            mapFiles(
                                listFile
                            )
                        }
                        .subscribe(
                            { datas: List<VideoFile?>? ->
                                if (datas!!.isEmpty()) {
                                    mBinding.rcvVideo.visibility = View.GONE
                                    mBinding.groupNoData.visibility = View.VISIBLE
                                } else {
                                    mBinding.rcvVideo.visibility = View.VISIBLE
                                    mBinding.groupNoData.visibility = View.GONE
                                    handler = Handler()
                                    videoAdapter = VideoAdapter(datas, act, handler!!)
                                    mBinding.rcvVideo.layoutManager = LinearLayoutManager(act)
                                    videoAdapter?.setCallBackVideo(object :
                                        VideoAdapter.CallBackVideo {
                                        override fun onClickItem(item: VideoFile?) {
                                            videoFile = item
                                            openMedia(item!!)
                                        }

                                        override fun onCLickMore(
                                            item: VideoFile?,
                                            pos: Int,
                                            view: View?
                                        ) {
                                            showPopupMenuMore(item!!, pos, view!!)
                                        }

                                        override fun onClickEditVideo(item: VideoFile?) {
                                            /*if (MainActivity.getInstance() != null)
                                                IntersControl.showInterstitialAdMob(MainActivity.getInstance(), new IntersControl.callBacksInterstitialAds() {
                                                    @Override
                                                    public void onDismiss() {
                                                        openEditVideo(item.path);
                                                    }
                                                });*/
                                            openEditVideo(item?.path!!)
                                        }

                                        override fun onClickShareVideo(item: VideoFile?) {
                                            shareMedia(item?.path!!)
                                        }

                                        override fun onClickDeleteVideo(item: VideoFile?, position: Int) {
//                                            val deleteVideoDialog: DeleteVideoDialog =
//                                                DeleteVideoDialog(act)
//                                            deleteVideoDialog.setOnCLickDeleteDialog(object :
//                                                onCLickDeleteDialog() {
//                                                fun onCLickCancel() {
//                                                    deleteVideoDialog.dismiss()
//                                                }
//
//                                                fun onCLickDelete() {
//                                                    deleteMedia(item!!, position)
//                                                    deleteVideoDialog.dismiss()
//                                                }
//                                            })
//                                            deleteVideoDialog.show()
                                        }
                                    })
                                    mBinding.rcvVideo.adapter = videoAdapter
                                    mBinding.swipeRefresh.setOnRefreshListener {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if ((ContextCompat.checkSelfPermission(
                                                    act,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                                ) == PackageManager.PERMISSION_GRANTED
                                                        || ContextCompat.checkSelfPermission(
                                                    act,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                ) == PackageManager.PERMISSION_GRANTED)
                                            ) {
                                                data
                                            } else {
                                                if (activity is MainActivity) {
                                                    (activity as MainActivity).askPermissionStorageMain()
                                                }
                                            }
                                        } else {
                                            data
                                        }
                                        mBinding.swipeRefresh.isRefreshing = false
                                    }
                                }
                                mBinding.progress.visibility = View.GONE
                            },
                            {
                                mBinding.progress.visibility = View.GONE
                            })
                }, 1000)
            }
        }
    private val allFilesInTrim: Single<Array<File>>
        get() = Single.create<Array<File>> { sub: SingleEmitter<Array<File>> ->
            val file: File =
                File(Storage.getDirectoryFileVideoInStorage(requireContext()))
            val listFile: Array<File>? = file.listFiles()
            if (listFile != null) {
                sub.onSuccess(listFile)
            } else {
                sub.onSuccess(arrayOf())
            }
        }

    private fun mapFiles(listFile: Array<File>): Single<List<VideoFile?>?> {
        return Single.create<List<VideoFile?>?> { sub: SingleEmitter<List<VideoFile?>?> ->
            activity?.let { act ->
                Arrays.sort(listFile,
                    Comparator { f1: File, f2: File ->
                        f1.lastModified().compareTo(f2.lastModified())
                    })
                val videoFiles: MutableList<VideoFile?> =
                    ArrayList<VideoFile?>()
                var previousDate: Long = 0
                for (i in listFile.indices.reversed()) {
                    val videoFile = VideoFile()
                    val duration: String? =
                        Toolbox.getDurationVideoFile(act, listFile[i])
                    if (duration != null) {
                        videoFile.path = listFile[i].absolutePath
                        videoFile.name = listFile[i].name
                        videoFile.size = listFile[i].length()
                        videoFile.duration = duration
                        videoFile.resolution =
                            Toolbox.getResolutionVideo(
                                act,
                                listFile[i]

                            )
                        videoFile.lastModified = listFile[i].lastModified()
                        videoFiles.add(videoFile)
                        previousDate = listFile[i].lastModified()
                    }
                }
                sub.onSuccess(videoFiles)
            }
        }
    }

    private fun requestDeletePermission(uriList: List<Uri>) {
        activity?.let { act ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val pi: PendingIntent =
                        MediaStore.createDeleteRequest(act.contentResolver, uriList)
                    startIntentSenderForResult(
                        pi.intentSender, REQUEST_PERMISSION_DELETE, null, 0, 0,
                        0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showPopupMenuMore(videoFile: VideoFile, pos: Int, view: View) {
        activity?.let { act ->
            val popupMenu: PopupMenu = PopupMenu(act, view)
            popupMenu.inflate(R.menu.popup_menu_more)
            popupMenu.show()
            popupMenu.menu.getItem(3).isEnabled =
                PreferencesHelper.getBoolean(PreferencesHelper.KEY_SAVE_AS_GIF, true)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.share -> shareMedia(videoFile.path!!)
                    R.id.delete -> {
                        this.pos = pos
                        deleteMedia(videoFile, pos)
                    }
                    R.id.edit -> openEditVideo(videoFile.path!!)
                    R.id.savegif -> convertToGif(videoFile.path!!)
                }
                true
            }
        }
    }

    private fun convertToGif(filePath: String) {
        activity?.let { act ->
            val fileUri: Uri = FileProvider.getUriForFile(
                act, act.getPackageName() + ".provider",
                File(filePath)
            )
            val gif: Mp4toGIFConverter = Mp4toGIFConverter(act)
            gif.setVideoUri(fileUri)
            gif.convertToGif()
        }
    }

    private fun openEditVideo(filePath: String) {
        activity?.let { act ->
//            val intent: Intent = Intent(
//                act,
//                VideoTrimActivity::class.java
//            )
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra(Config.EXTRA_PATH, filePath)
//            act.startActivity(intent)
        }
    }

    private fun shareMedia(filePath: String) {
        activity?.let { act ->
            val fileUri: Uri = FileProvider.getUriForFile(
                act, act.packageName + ".provider",
                File(filePath)
            )
            val shareIntent: Intent = Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, fileUri)
                .setType(if (filePath.endsWith(".mp4")) "video/mp4" else "image/*")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            act.startActivity(shareIntent)
        }
    }

    fun openMedia(item: VideoFile) {
        activity?.let { act ->
            val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy--MM-yy")
            val file: File = File(item.path)
            val dateFile: Date = Date(file.lastModified())
            try {
                val intent: Intent = Intent(
                    act,
                    PreviewVideoActivity::class.java
                )
                intent.putExtra("Uri_video", item.path)
                intent.putExtra("Name_video", item.name)
                intent.putExtra("Date_video", simpleDateFormat.format(dateFile))
                intent.putExtra("Size_video", item.size)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(act, "Open Error", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun deleteMedia(videoFile: VideoFile, pos: Int) {
        activity?.let { act ->
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                File(videoFile.path).delete()
                removeItem(pos)
                MediaScannerConnection.scanFile(
                    act,
                    arrayOf<String>(videoFile.path!!),
                    arrayOf<String>("video/mp4")
                ) { _: String?, _: Uri? -> }
            } else {
                MediaScannerConnection.scanFile(
                    act,
                    arrayOf<String>(videoFile.path!!),
                    arrayOf<String>("video/mp4")
                ) { _: String?, uri: Uri? ->
                    if (uri != null) {
                        try {
                            if (act.contentResolver.delete(uri, null, null) !== -1) {
                                removeItem(pos)
                            }
                        } catch (e: SecurityException) {
                            val uris: MutableList<Uri> =
                                ArrayList()
                            uris.add(uri)
                            requestDeletePermission(uris)
                        }
                    }
                }
            }
        }
    }

    private fun removeItem(pos: Int) {
        activity?.let { act ->
            act.runOnUiThread {
                videoAdapter?.list?.removeAt(pos)
                videoAdapter?.notifyItemRemoved(pos)
                if (videoAdapter?.list?.isEmpty()!!) {
                    mBinding.rcvVideo.visibility = View.GONE
                    mBinding.groupNoData.visibility = View.VISIBLE
                    mBinding.progress.visibility = View.GONE
                } else {
                    mBinding.rcvVideo.visibility = View.VISIBLE
                    mBinding.groupNoData.visibility = View.GONE
                }
            }
        }
    }

    val isNeedRefresh = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                removeItem(pos)
            }
        }
    }

    override fun onReceivedEvent(type: RxBusType?, data: Any?) {
        when (type) {
            RxBusType.SCREEN_RECORD_SUCCESS, RxBusType.TRIM_VIDEO, RxBusType.NOTI_MEDIA_CHANGE -> this.data
            else -> {}
        }
    }

//    private fun initPurchase() {
//        billingProcessor =
//            BillingProcessor(requireContext(), Constants_Sub.LICENSE_KEY, object : IBillingHandler {
//                override fun onProductPurchased(productId: String, details: TransactionDetails?) {
//                    val purchasedRemoveAds: Boolean =
//                        billingProcessor!!.isPurchased(Constants_Sub.getIdRemoveAds())
//                    Constants_Sub.setBuyRMAds(getActivity(), true)
//                    if (purchasedRemoveAds) {
//                        refreshApp()
//                    }
//                }
//
//                override fun onPurchaseHistoryRestored() {
//                }
//
//                override fun onBillingError(errorCode: Int, error: Throwable?) {
//                    DebugLibrary.logContent("TAGGGGGGG", "onBillingError: " + error!!.message)
//                }
//
//                override fun onBillingInitialized() {
//                    try {
//                        if (billingProcessor.loadOwnedPurchasesFromGoogle()) {
//                            if (billingProcessor != null) {
//                                if (billingProcessor!!.isPurchased(Constants_Sub.getIdRemoveAds())) {
//                                    Constants_Sub.setBuyRMAds(
//                                        getActivity(),
//                                        billingProcessor!!.isPurchased(Constants_Sub.getIdRemoveAds())
//                                    )
//                                } else {
//                                    Constants_Sub.setBuyRMAds(
//                                        getActivity(),
//                                        billingProcessor!!.isPurchased(Constants_Sub.getIdRemoveAds())
//                                    )
//                                }
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//        billingProcessor.connect(requireActivity())
//        billingProcessor!!.initialize()
//    }

    private fun refreshApp() {
        activity?.let { act ->
            act.startActivity(
                Intent(
                    act,
                    SplashActivity::class.java
                )
            )
            act.finish()
            act.finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) handler!!.removeCallbacksAndMessages(null)
    }

    override fun onClick(v: View) {
//        if (v === mBinding.lnRemoveAds) {
//            activity?.let {
////                billingProcessor!!.purchase(it, Constants_Sub.getIdRemoveAds())
//            }
//
//        }
    }

    companion object {
        private val REQUEST_PERMISSION_DELETE: Int = 376
        private var billingProcessor: BillingProcessor? = null
    }
}