package com.module.config.views.bases.ext

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.onLoadMore(callback: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!recyclerView.canScrollVertically(1)) { //1 for down
                callback.invoke()
            }
        }
    })
}