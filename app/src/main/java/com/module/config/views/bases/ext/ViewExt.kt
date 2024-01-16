package com.module.config.views.bases.ext

import android.view.MotionEvent
import android.view.View
import androidx.databinding.ViewDataBinding

internal const val DISPLAY = 1080

fun View.goneView() {
    visibility = View.GONE
}

fun View.visibleView() {
    visibility = View.VISIBLE
}

fun View.invisibleView() {
    visibility = View.INVISIBLE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

fun ViewDataBinding.goneView() {
    this.root.goneView()
}

fun ViewDataBinding.visibleView() {
    this.root.visibleView()
}

fun ViewDataBinding.invisibleView() {
    this.root.invisibleView()
}

fun ViewDataBinding.isVisible() = this.root.visibility == View.VISIBLE

fun ViewDataBinding.isInvisible() = this.root.visibility == View.INVISIBLE

fun ViewDataBinding.isGone() = this.root.visibility == View.GONE

/*Resize View*/
fun View.resizeView(width: Int, height: Int = 0) {
    val pW = context.getWidthScreenPx() * width / DISPLAY
    val pH = if (height == 0) pW else pW * height / width
    val params = layoutParams
    params.let {
        it.width = pW
        it.height = pH
    }
}

fun View.onClick(action: (view: View?) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View) {
            if (!context.canTouch()) return
            action(v)
        }
    })
}

fun View.onClickAlpha(action: (view: View?) -> Unit) {
    this.setOnCustomTouchViewAlphaNotOther(object : OnCustomClickListener {
        override fun onCustomClick(view: View, event: MotionEvent) {
            if (!context.canTouch()) return
            action(view)
        }
    })
}