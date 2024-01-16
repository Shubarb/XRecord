package com.module.config.views.dialogs

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.module.config.R
import com.module.config.databinding.DialogNoInternetBinding
import com.module.config.views.bases.BaseDialog
import com.module.config.views.bases.ext.OnCustomClickListener
import com.module.config.views.bases.ext.setOnCustomTouchViewAlphaNotOther

class NoInternetDialog(context: Context, val onClickRetry: () -> Unit) :
    BaseDialog<DialogNoInternetBinding>(context) {

    override fun getLayoutDialog() = R.layout.dialog_no_internet

    override fun initViews() {
        super.initViews()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvRetry.setOnCustomTouchViewAlphaNotOther(object : OnCustomClickListener {
            override fun onCustomClick(view: View, event: MotionEvent) {
                dismiss()
                onClickRetry()
            }
        })
    }
}