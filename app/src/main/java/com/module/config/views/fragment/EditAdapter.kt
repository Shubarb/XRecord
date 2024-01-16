package com.module.config.views.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.module.config.databinding.ItemEditBinding
import com.module.config.models.EnumItem
import com.module.config.utils.utils_controller.ViewUtils
import com.module.config.views.bases.BaseRecyclerAdapter
import com.module.config.views.bases.BaseViewHolder3
import kotlin.reflect.KFunction

class EditAdapter(list: List<EnumItem>, context: Context?) :
    BaseRecyclerAdapter<EnumItem?>(list, context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder3<*> {
        return ViewHolder(ItemEditBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: BaseViewHolder3<*>, position: Int) {
        if (holder is ViewHolder) {
            ViewUtils.loadImage(
                context,
                list[position]?.icon,
                (holder as ViewHolder).mBinding?.imvIcon
            )
            (holder as ViewHolder).mBinding?.tv?.text = list[position]?.title
            (holder as ViewHolder).mBinding?.container?.setOnClickListener {
                onClickItem(
                    list[position]
                )
            }
        }
    }

    override fun setCallBackAdapter(it: KFunction<Unit>) {

    }

    internal inner class ViewHolder(binding: ItemEditBinding?) :
        BaseViewHolder3<ItemEditBinding?>(binding)
}