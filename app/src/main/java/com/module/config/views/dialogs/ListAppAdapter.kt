package com.module.config.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.module.config.databinding.ItemAppBinding
import com.module.config.models.AppsModel
import com.module.config.views.bases.BaseRecyclerAdapter
import com.module.config.views.bases.BaseViewHolder3
import kotlin.reflect.KFunction

class ListAppAdapter(list: List<AppsModel?>?, context: Context?) :
    BaseRecyclerAdapter<AppsModel?>(list, context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder3<*> {
        return ViewHolder(ItemAppBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: BaseViewHolder3<*>, position: Int) {
        val app: AppsModel = list[position]!!
        if (holder is ViewHolder) {
            (holder as ViewHolder).mBinding?.root?.setOnClickListener { v ->
                callBackAdapter.onClickItem(
                    list[position]
                )
            }
            (holder as ViewHolder).mBinding?.tvName?.text = app.appName
            (holder as ViewHolder).mBinding?.imvIcon?.let {
                Glide.with(context).load(app.appIcon)
                    .into(it)
            }
            if (app.isSelectedApp) (holder as ViewHolder).mBinding?.imvCheck?.visibility = View.VISIBLE else (holder as ViewHolder).mBinding?.imvCheck?.visibility =
                View.INVISIBLE
        }
    }

    override fun setCallBackAdapter(it: KFunction<Unit>) {

    }

    internal inner class ViewHolder(binding: ItemAppBinding?) :
        BaseViewHolder3<ItemAppBinding?>(binding)
}
