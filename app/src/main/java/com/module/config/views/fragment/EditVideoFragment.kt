package com.module.config.views.fragment

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import com.module.config.R
import com.module.config.databinding.FragmentEditVideoBinding
import com.module.config.models.EnumItem
import com.module.config.views.bases.BaseFragment

class EditVideoFragment : BaseFragment<FragmentEditVideoBinding>() {
    private var editAdapter: EditAdapter? = null

    override fun getLayoutFragment() = R.layout.fragment_edit_video

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity?.let { act ->
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = data!!.data
                    var path: String?
                    path = getPathFromUri(selectedImageUri, act)
                    if (TextUtils.isEmpty(path)) {
                        path = selectedImageUri!!.path
                    }
                    if (path != null) {
//                        startActivityForResult(
//                            Intent(
//                                act,
//                                VideoTrimActivity::class.java
//                            ).putExtra(Config.EXTRA_PATH, path), VIDEO_TRIM
//                        )
                    } else {

                    }
                }
            }
        }
    }

    private fun getPathFromUri(uri: Uri?, activity: Activity): String? {
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(activity, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(activity, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                activity,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents" == uri!!.authority
    }

    private fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents" == uri!!.authority
    }

    private fun isMediaDocument(uri: Uri?): Boolean {
        return "com.android.providers.media.documents" == uri!!.authority
    }

    private fun isGooglePhotosUri(uri: Uri?): Boolean {
        return "com.google.android.apps.photos.content" == uri!!.authority
    }

    override fun initViews() {
        super.initViews()
        activity?.let { act ->
            editAdapter = EditAdapter(arrayListOf<EnumItem>(), context)
            editAdapter?.setCallBackAdapter { item ->
                when (item) {
                    EnumItem.TRIM -> funcTrim()
                    else -> {}
                }
            }
            mBinding.rcvEdit.adapter = editAdapter
        }
    }

    private fun funcTrim() {
        try {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                REQUEST_TAKE_GALLERY_VIDEO
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isNeedRefresh = false

    companion object {
        private const val REQUEST_TAKE_GALLERY_VIDEO = 100
        private const val VIDEO_TRIM = 101
    }
}