package com.module.config.views.dialogs

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.module.config.databinding.ItemSelectedBinding
import com.module.config.models.ItemSelected
import com.module.config.views.bases.BaseRecyclerAdapter
import com.module.config.views.bases.BaseViewHolder3
import kotlin.reflect.KFunction

class SelectedAdapter(list: List<ItemSelected>, context: Context?, var selected: String) :
    BaseRecyclerAdapter<ItemSelected?>(list, context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder3<*> {
        return ViewHolder(ItemSelectedBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: BaseViewHolder3<*>, position: Int) {
        if (holder is ViewHolder) {
            binData(list[position], holder as ViewHolder)
        }
    }

    override fun setCallBackAdapter(it: KFunction<Unit>) {

    }

    fun binData(item: ItemSelected?, viewHolder: ViewHolder) {
        if (item == null) return
        if (!TextUtils.isEmpty(context.resources.getString(item.entry))) {
            viewHolder.mBinding?.tvTitle?.text = context.resources.getString(item.entry)
        }
        if (TextUtils.isEmpty(selected)) {
            selected = item.value
        }
        if (item.description !== 0) {
            viewHolder.mBinding?.tvDescription?.visibility = View.VISIBLE
            viewHolder.mBinding?.tvDescription?.text = context.resources.getString(item.description)
        }
        viewHolder.mBinding?.rdSelect?.isChecked = item.value == selected
        viewHolder.mBinding?.root?.setOnClickListener { v ->
            selected = item.value
            notifyDataSetChanged()
//            MoreAppAdapter.onClickItem(item)
        }
    }

    inner class ViewHolder(binding: ItemSelectedBinding?) :
        BaseViewHolder3<ItemSelectedBinding?>(binding)
}
