package com.module.config.views.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.module.config.R
import com.module.config.databinding.FragmentPictureBinding
import com.module.config.rx.RxBusType
import com.module.config.utils.utils_controller.Storage
import com.module.config.views.activities.main.MainActivity
import com.module.config.views.activities.preview_media.PreviewImageActivity
import com.module.config.views.bases.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.*

open class PictureFragment : BaseFragment<FragmentPictureBinding>() {
    private var pictureAdapter: PictureAdapter? = null

    override fun getLayoutFragment() = R.layout.fragment_picture

    override fun initViews() {
        super.initViews()
        mBinding.swipeRefresh.setColorSchemeResources(
            R.color.color_accent,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.let { act ->
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
            }
        } else {
            data
        }
    }

    /*if (MainActivity.getInstance() != null) {
                                       IntersControl.showInterstitialAdMob(MainActivity.getInstance(), new IntersControl.callBacksInterstitialAds() {
                                           @Override
                                           public void onDismiss() {
                                               openMedia(item);
                                           }
                                       });
                                   }*/
    private val data: Unit
        @SuppressLint("CheckResult") private get() {
            activity?.let { act ->

                allFilesInPicture
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap<List<String?>> { listFile: Array<File> ->
                        mapFiles(
                            listFile
                        )
                    }
                    .subscribe({ datas: List<String?> ->
                        if (datas.isEmpty()) {
                            mBinding.rcvPicture.visibility = View.GONE
                            mBinding.groupNoData.visibility = View.VISIBLE
                        } else {
                            mBinding.rcvPicture.setVisibility(View.VISIBLE)
                            mBinding.groupNoData.setVisibility(View.GONE)
                            pictureAdapter = PictureAdapter(datas as List<String>, act)
                            pictureAdapter?.setCallBackVideo(object : PictureAdapter.CallBackVideo {
                                override fun onClickItem(item: String?) {
                                    /*if (MainActivity.getInstance() != null) {
                                       IntersControl.showInterstitialAdMob(MainActivity.getInstance(), new IntersControl.callBacksInterstitialAds() {
                                           @Override
                                           public void onDismiss() {
                                               openMedia(item);
                                           }
                                       });
                                   }*/
                                    openMedia(item)
                                }
                            })
                            mBinding.rcvPicture.adapter = pictureAdapter
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
                    }, { })
            }
        }
    private val allFilesInPicture: Single<Array<File>>
        get() = Single.create<Array<File>> { sub: SingleEmitter<Array<File>> ->
            val listFile: Array<File>? =
                Storage.getFilesImageInStorage(requireContext())
            if (listFile != null) {
                sub.onSuccess(listFile)
            } else {
                sub.onSuccess(arrayOf())
            }
        }

    fun mapFiles(listFile: Array<File>): Single<List<String?>> {
        return Single.create { sub: SingleEmitter<List<String?>> ->
            Arrays.sort(listFile,
                Comparator { f1: File, f2: File ->
                    f1.lastModified().compareTo(f2.lastModified())
                })
            val pictureFile: MutableList<String?> =
                ArrayList()
            for (i in listFile.indices.reversed()) {
                pictureFile.add(listFile[i].absolutePath)
            }
            sub.onSuccess(pictureFile)
        }
    }

    fun initControl() {}
    fun openMedia(filePath: String?) {
        activity?.let { act ->
            /*Uri fileUri = FileProvider.getUriForFile(
                    getContext(), getContext().getPackageName() + ".provider",
                    new File(filePath));
            try {
                Intent openVideoIntent = new Intent();
                openVideoIntent.setAction(Intent.ACTION_VIEW)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setDataAndType(
                                fileUri,
                                getContext().getContentResolver().getType(fileUri));
                getContext().startActivity(openVideoIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            try {
                val intent: Intent = Intent(
                    act,
                    PreviewImageActivity::class.java
                )
                intent.putExtra("Uri_image", filePath)
                intent.putExtra("Name_image", filePath)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                act.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(act, "Open Error", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    val isNeedRefresh = true

    fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentPictureBinding {
        return FragmentPictureBinding.inflate(LayoutInflater.from(getContext()))
    }

    override fun onReceivedEvent(type: RxBusType?, data: Any?) {
        when (type) {
            RxBusType.SCREEN_SHOT, RxBusType.NOTI_MEDIA_CHANGE -> this.data
            else -> {}
        }
    }
}
