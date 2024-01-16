package com.module.config.views.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.Window
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.module.config.R

class DialogInput(
    context: Context,
    title: String?,
    input: String
) :
    Dialog(context, R.style.Theme_Dialog) {

    private var callBackDialog: DialogSingleSelected.CallBackDialog? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_input)
        (findViewById<View>(R.id.tv_title) as AppCompatTextView).text =
            title
        (findViewById<View>(R.id.edt_input) as AppCompatEditText).setText(input)
        (findViewById<View>(R.id.edt_input) as AppCompatEditText).setSelection(input.length)
        findViewById<View>(R.id.tv_cancel).setOnClickListener { v: View? -> dismiss() }
        findViewById<View>(R.id.tv_ok).setOnClickListener { v: View? ->
            if (callBackDialog != null) {
                if (!TextUtils.isEmpty(
                        (findViewById<View>(R.id.edt_input) as AppCompatEditText).text
                            .toString()
                    )
                ) {
                    callBackDialog?.onOK(
                        (findViewById<View>(R.id.edt_input) as AppCompatEditText).text
                            .toString()
                    )
                    dismiss()
                }
            }
        }
    }

    fun callback(callBackDialogs: DialogSingleSelected.CallBackDialog){
        this.callBackDialog = callBackDialogs
    }
}