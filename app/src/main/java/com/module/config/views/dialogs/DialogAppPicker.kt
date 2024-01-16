package com.module.config.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.module.config.R
import com.module.config.app.GlobalApp
import com.module.config.databinding.DialogListAppBinding
import com.module.config.models.AppsModel
import com.module.config.rx.CallBackRxBus
import com.module.config.rx.CallbackEventView
import com.module.config.rx.RxBus
import com.module.config.rx.RxBusType
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.views.bases.BaseDialog

class DialogAppPicker(context: Context) :
    BaseDialog<DialogListAppBinding>(context) {
    private var listAppAdapter: ListAppAdapter? = null

    override fun getLayoutDialog() = R.layout.dialog_list_app

    override fun initViews() {
        super.initViews()
        listAppAdapter = ListAppAdapter(ArrayList<AppsModel>(), context)
        listAppAdapter?.setCallBackAdapter { item ->
            PreferencesHelper.putString(PreferencesHelper.KEY_APP_SELECTED, item?.packageName)
            dismiss()
        }
        mBinding.rcvData.adapter = listAppAdapter
        mBinding.tvCancel.setOnClickListener { v -> dismiss() }
        RxBus.instance?.subscribe(CallBackRxBus(object : CallbackEventView {
            override fun onReceivedEvent(type: RxBusType?, data: Any?) {
                when (type) {
                    RxBusType.LOAD_TARGET_APP_FINISHED -> fetchTargetApp()
                    else -> {}
                }
            }
        }))
        fetchTargetApp()
    }

    private fun fetchTargetApp() {
        if (GlobalApp.listTargetApp != null) {
            mBinding.rcvData.visibility = View.VISIBLE
            mBinding.progress.visibility = (View.GONE)
            listAppAdapter?.addDatas(GlobalApp.listTargetApp)
        } else {
            mBinding.rcvData.visibility = (View.VISIBLE)
            mBinding.progress.visibility = (View.GONE)
        }
    }
}
