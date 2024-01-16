package com.module.config.views.bases.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.module.config.R


fun ImageView.loadImage(context: Context, path: Any, radius: Int = 1) {
    val options: RequestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .fitCenter()
        .placeholder(R.drawable.img_placeholder)
        .fallback(R.drawable.img_placeholder)
        .error(R.drawable.img_placeholder)
        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radius)))

    Glide.with(context)
        .load(path)
        .apply(options)
        .into(this)
}

fun ImageView.loadImageNoCache(context: Context, path: Any, radius: Int = 1) {
    val options: RequestOptions = RequestOptions()
        .placeholder(R.drawable.img_placeholder)
        .fallback(R.drawable.img_placeholder)
        .error(R.drawable.img_placeholder)
        .apply(
            RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(radius)
            )
        )

    Glide.with(context)
        .load(path)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .apply(options)
        .into(this)
}

fun ImageView.loadImageProgress(context: Context, path: String, progressBar: ProgressBar) {
    Glide.with(context).load(context)
        .error(R.drawable.img_placeholder)
        .load(path)
        .fitCenter()
        .centerCrop()
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
            ): Boolean {
                progressBar.isGone = true
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.isGone = true
                return false
            }
        }).into(this)
}

fun ImageView.loadImageNoProgress(context: Context, src: String) {
    Glide.with(context).load(src).error(R.drawable.img_placeholder).into(this)
}

fun ImageView.setColorById(context: Context, idColor: Int) {
    setColorFilter(ContextCompat.getColor(context, idColor))
}