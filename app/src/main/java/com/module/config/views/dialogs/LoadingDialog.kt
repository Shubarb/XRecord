package com.module.config.views.dialogs

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.module.config.databinding.DialogLoadingBinding
import com.module.config.views.bases.ext.onCheckActivityIsFinished

class LoadingDialog(private val activity: Activity) {
    private var alertDialogLoading: AlertDialog? = null

    fun showDialog() {
        if (!activity.onCheckActivityIsFinished()) {
            if (alertDialogLoading == null) {
                val builder = AlertDialog.Builder(activity)
                val inflater = LayoutInflater.from(activity)
                val binding = DialogLoadingBinding.inflate(inflater)
                builder.setView(binding.root)
                builder.setCancelable(false)

                alertDialogLoading = builder.create()
                alertDialogLoading?.show()
            } else {
                alertDialogLoading?.show()
            }
        }
    }

    fun dismissDialog() {
        if (!activity.onCheckActivityIsFinished() && alertDialogLoading?.isShowing == true) {
            alertDialogLoading?.dismiss()
        }
    }
}