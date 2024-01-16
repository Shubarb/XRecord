package com.module.config.views.dialogs

import android.content.Context
import com.module.config.R
import com.module.config.databinding.DialogSingleSelectedBinding
import com.module.config.models.ItemSelected
import com.module.config.views.bases.BaseDialog

class DialogSingleSelected(
    context: Context,
    title: String?,
    itemSelecteds: List<ItemSelected>,
    selected: String?
) :
    BaseDialog<DialogSingleSelectedBinding>(context) {
    private val selectedAdapter: SelectedAdapter
    private var callBackDialog: CallBackDialog? = null
    override fun getLayoutDialog() = R.layout.dialog_single_selected

    init {
        mBinding.tvTitle.text = title
        selectedAdapter = SelectedAdapter(itemSelecteds, getContext(), selected!!)
        mBinding.rcvData.adapter = selectedAdapter
    }

    override fun initViews() {
        super.initViews()
        mBinding.tvCancel.setOnClickListener { v -> dismiss() }
        mBinding.tvOk.setOnClickListener { v ->
            if (callBackDialog != null) {
                callBackDialog?.onOK(selectedAdapter.selected)
                dismiss()
            }
        }
    }

    fun callback (callBackDialogs: CallBackDialog){
        this.callBackDialog = callBackDialogs
    }
    interface CallBackDialog {
        fun onOK(selected: String?)
    }
}
