package com.module.config.views.bases

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder2<BD : ViewBinding?>(var binding: BD) : RecyclerView.ViewHolder(
    binding!!.root
)