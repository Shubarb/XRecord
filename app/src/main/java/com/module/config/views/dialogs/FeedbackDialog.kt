package com.module.config.views.dialogs

import android.content.Context
import com.module.config.R
import com.module.config.app.AppConstants
import com.module.config.databinding.DialogFeedbackBinding
import com.module.config.utils.AppUtils
import com.module.config.views.bases.BaseDialog
import com.module.config.views.bases.ext.showToastByString

class FeedbackDialog(context: Context) : BaseDialog<DialogFeedbackBinding>(context) {

    override fun getLayoutDialog() = R.layout.dialog_feedback

    override fun initViews() {
        super.initViews()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnFeedback.setOnClickListener {
            val feedback = mBinding.edtFeedback.text.trim()
            if (feedback == "") {
                context.showToastByString("Please enter your feedback!")
            } else {
                dismiss()
                AppUtils.sendEmailMore(
                    context,
                    arrayOf(AppConstants.EMAIL_FEEDBACK),
                    "",
                    feedback.toString()
                )
            }
        }
    }
}