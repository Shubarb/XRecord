package com.module.config.views.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.module.config.R

class PictureAdapter(private val mList: List<String>, private val mContext: Context) :
    RecyclerView.Adapter<PictureAdapter.ViewHolder>() {
    private var callBackVideo: CallBackVideo? = null
    fun setCallBackVideo(callBackVideo: CallBackVideo?) {
        this.callBackVideo = callBackVideo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item: View = LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext)
            .load(mList[position])
            .placeholder(R.drawable.bgr_picture)
            .error(R.drawable.bgr_picture)
            .into(holder.imgPicture)
        holder.itemView.setOnClickListener {
            if (callBackVideo != null) {
                callBackVideo!!.onClickItem(mList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        var imgPicture: RoundedImageView

        init {
            imgPicture = binding.findViewById<RoundedImageView>(R.id.imv_picture)
        }
    }

    interface CallBackVideo {
        fun onClickItem(item: String?)
    }
}
