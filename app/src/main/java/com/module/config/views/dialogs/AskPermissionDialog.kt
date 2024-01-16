package com.module.config.views.dialogs

import android.Manifest
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.module.config.R
import com.module.config.databinding.DialogAskPermissionBinding

class AskPermissionDialog : DialogFragment() {
    private var mSuccessListener: SuccessListener? = null
    private var permissionName: String? = null
    private lateinit var binding: DialogAskPermissionBinding

    interface SuccessListener {
        fun onSuccess()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val t = javaClass.simpleName
        if (manager.findFragmentByTag(t) == null) {
            super.show(manager, t)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(
            requireActivity()
        )
        binding = DialogAskPermissionBinding.inflate(LayoutInflater.from(context))
        dialogBuilder.setView(binding.getRoot())
        initData()
        return dialogBuilder.create()
    }

    private fun initData() {
        try {
            when (permissionName) {
                Manifest.permission.RECORD_AUDIO -> binding.tvContent.setText(getString(R.string.record_audio_permission))
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> binding.tvContent.setText(getString(R.string.external_storage_permission))
                Manifest.permission.READ_EXTERNAL_STORAGE -> binding.tvContent.setText(getString(R.string.external_storage_permission))
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION -> binding.tvContent.setText(getString(R.string.overlay_permission))
            }
            binding.tvAction1.setOnClickListener { v ->
                dismiss()
                if (mSuccessListener != null) mSuccessListener!!.onSuccess()
            }
        } catch (e: Exception) {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val windowParams = window!!.attributes
            windowParams.dimAmount = 0.7f
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            windowParams.flags =
                windowParams.flags or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            window.attributes = windowParams
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    companion object {
        fun getInstance(
            permissionName: String?,
            mSuccessListener: SuccessListener?
        ): AskPermissionDialog {
            val mAskPermissionDialog = AskPermissionDialog()
            mAskPermissionDialog.mSuccessListener = mSuccessListener
            mAskPermissionDialog.permissionName = permissionName
            return mAskPermissionDialog
        }
    }
}