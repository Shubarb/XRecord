package com.module.config.views.fragment

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.module.config.databinding.ItemColorBinding
import com.module.config.views.bases.BaseRecyclerAdapter
import com.module.config.views.bases.BaseViewHolder3
import kotlin.reflect.KFunction

class ColorAdapter(list: List<Int?>?, context: Context?) :
    BaseRecyclerAdapter<Int?>(list, context) {
    private var itemCheck = Color.parseColor("#000000")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder3<*> {
        return ViewHolder(ItemColorBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: BaseViewHolder3<*>, position: Int) {
        if (holder is ViewHolder) {
            (holder as ViewHolder).mBinding?.imvColor?.setBackgroundColor(list[position]!!)
            if (itemCheck == list[position]) {
                (holder as ViewHolder).mBinding?.imvCheck?.visibility = View.VISIBLE
            } else {
                (holder as ViewHolder).mBinding?.imvCheck?.visibility = View.GONE
            }
            (holder as ViewHolder).mBinding?.root?.setOnClickListener { v ->
                itemCheck = list[position]!!
                callBackAdapter.onClickItem(list[position])
                notifyDataSetChanged()
            }
        }
    }

    override fun setCallBackAdapter(it: KFunction<Unit>) {

    }

    inner class ViewHolder(binding: ItemColorBinding?) :
        BaseViewHolder3<ItemColorBinding?>(binding)
}