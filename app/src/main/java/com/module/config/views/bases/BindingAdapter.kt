package com.module.config.views.bases

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.module.config.app.GlobalApp
import com.module.config.views.bases.ext.loadImage

@BindingAdapter("bindAdapter:resId")
fun setImage(imageView: ImageView, resId: Int) {
    imageView.loadImage(GlobalApp.context, resId)
}

@BindingAdapter("bindAdapter:path", "bindAdapter:corner")
fun setImageByPath(imageView: ImageView, pathImage: String, corner: Int = 1) {
    imageView.loadImage(GlobalApp.context, pathImage, radius = corner)
}