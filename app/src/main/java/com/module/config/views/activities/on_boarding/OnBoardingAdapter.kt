package com.module.config.views.activities.on_boarding

import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.module.config.R
import com.module.config.databinding.ItemOnBoardingBinding
import com.module.config.models.OnBoardingModel
import com.module.config.views.bases.BaseRecyclerView

class OnBoardingAdapter : BaseRecyclerView<OnBoardingModel>() {

    override fun getItemLayout() = R.layout.item_on_boarding

    override fun submitData(newData: List<OnBoardingModel>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun setData(
        binding: ViewDataBinding,
        item: OnBoardingModel,
        layoutPosition: Int
    ) {
        if (binding is ItemOnBoardingBinding) {
            binding.onBoardingModel = item
            context?.let { ctx ->
                Glide.with(ctx).load(item.image).into(binding.ivTutorial)
            }
        }
    }
}