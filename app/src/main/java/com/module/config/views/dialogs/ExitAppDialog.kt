package com.module.config.views.dialogs

import android.content.Context
import com.module.config.R
import com.module.config.databinding.DialogExitAppBinding
import com.module.config.views.bases.BaseDialog

class ExitAppDialog(context: Context, val onClickExit: () -> Unit) :
    BaseDialog<DialogExitAppBinding>(context) {

    override fun getLayoutDialog() = R.layout.dialog_exit_app

    override fun initViews() {
        super.initViews()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvExit.setOnClickListener {
            dismiss()
            onClickExit()
        }
        mBinding.tvContinue.setOnClickListener {
            dismiss()
        }
    }
}