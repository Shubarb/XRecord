package com.module.config.views.fragment

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.module.config.R
import com.module.config.models.VideoFile
import com.module.config.utils.utils_controller.Toolbox
import wseemann.media.FFmpegMediaMetadataRetriever

class VideoAdapter(list: List<VideoFile?>, private val mContext: Activity, handler: Handler) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var callBackVideo: CallBackVideo? = null
    private val handler: Handler
    private val mmr: FFmpegMediaMetadataRetriever = FFmpegMediaMetadataRetriever()
    private val mList: ArrayList<VideoFile>
    fun setCallBackVideo(callBackVideo: CallBackVideo?) {
        this.callBackVideo = callBackVideo
    }

    init {
        mList = list as ArrayList<VideoFile>
        this.handler = handler
        AD = if (list.size > 2) {
            2
        } else {
            1
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_videos, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        ItemHolder(holder, position)
    }

    private fun ItemHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        val holder = holder1 as VideoViewHolder
        val item: VideoFile = mList[position]
        holder.tvDuration.text = Toolbox.convertDuration(item.duration)
        holder.tvNameVideo.text = item.name
        holder.tvResolution.text = item.resolution
        holder.tvSizeVideo.text = Toolbox.formatSize(item.size)
        Glide.with(mContext).load(item.path).into(holder.imgAvatar)
        holder.lnItemVideo.setOnClickListener { v: View? ->
            callBackVideo!!.onClickItem(
                item
            )
        }
        holder.imgMore.setOnClickListener { v: View? ->
            callBackVideo!!.onCLickMore(
                item,
                holder.adapterPosition,
                v
            )
        }
        holder.tvDateVideo.text = Toolbox.getDateString(item.lastModified)
        holder.imgEditVideo.setOnClickListener {
            if (callBackVideo != null) {
                callBackVideo!!.onClickEditVideo(item)
            }
        }
        holder.imgShareVideo.setOnClickListener {
            if (callBackVideo != null) {
                callBackVideo!!.onClickShareVideo(item)
            }
        }
        holder.imgDeleteVideo.setOnClickListener {
            if (callBackVideo != null) {
                callBackVideo!!.onClickDeleteVideo(item, position)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return CONTENT
    }

    val list: ArrayList<VideoFile>
        get() = mList

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgAvatar: ImageView
        var imgMore: ImageView
        var imgDeleteVideo: ImageView
        var imgEditVideo: ImageView
        var imgShareVideo: ImageView
        var tvDuration: TextView
        var tvNameVideo: TextView
        var tvSizeVideo: TextView
        var tvDateVideo: TextView
        var tvResolution: TextView
        var lnItemVideo: LinearLayout

        init {
            lnItemVideo = itemView.findViewById<LinearLayout>(R.id.ln_item_video)
            imgAvatar = itemView.findViewById<ImageView>(R.id.imv_video)
            imgDeleteVideo = itemView.findViewById<ImageView>(R.id.img_delete_video)
            imgMore = itemView.findViewById<ImageView>(R.id.imv_more)
            imgEditVideo = itemView.findViewById<ImageView>(R.id.img_edit_video)
            imgShareVideo = itemView.findViewById<ImageView>(R.id.img_share_video)
            tvDuration = itemView.findViewById<TextView>(R.id.tv_duration)
            tvNameVideo = itemView.findViewById<TextView>(R.id.tv_name)
            tvSizeVideo = itemView.findViewById<TextView>(R.id.tv_size)
            tvDateVideo = itemView.findViewById<TextView>(R.id.tv_date)
            tvResolution = itemView.findViewById<TextView>(R.id.tv_resolution)
        }
    }

    interface CallBackVideo {
        fun onClickItem(item: VideoFile?)
        fun onCLickMore(item: VideoFile?, pos: Int, view: View?)
        fun onClickEditVideo(item: VideoFile?)
        fun onClickShareVideo(item: VideoFile?)
        fun onClickDeleteVideo(item: VideoFile?, position: Int)
    }

    companion object {
        private const val CONTENT = 0
        private var AD = 1
    }
}
