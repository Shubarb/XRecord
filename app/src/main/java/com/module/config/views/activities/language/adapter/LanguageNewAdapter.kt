package com.module.config.views.activities.language.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.module.config.R
import com.module.config.databinding.ItemLanguageNewBinding
import com.module.config.models.LanguageModel
import com.module.config.views.bases.BaseRecyclerView
import com.module.config.views.bases.ext.invisibleView
import com.module.config.views.bases.ext.visibleView

class LanguageNewAdapter(val onClickItemLanguage: (LanguageModel, Int) -> Unit) :
    BaseRecyclerView<LanguageModel>() {

    private var currentSelected = 0

    override fun getItemLayout() = R.layout.item_language_new

    override fun submitData(newData: List<LanguageModel>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun setData(binding: ViewDataBinding, item: LanguageModel, layoutPosition: Int) {
        if (binding is ItemLanguageNewBinding) {
            context?.let { ctx ->
                item.isCheck = currentSelected == layoutPosition
                binding.ivLanguage.setImageDrawable(ContextCompat.getDrawable(ctx, item.image))
                binding.tvLanguage.text = item.languageName

                if (item.isCheck) {
                    binding.ivDone.visibleView()
                    binding.clRoot.setBackgroundResource(R.drawable.bg_item_language_selected)
                } else {
                    binding.ivDone.invisibleView()
                    binding.clRoot.setBackgroundResource(R.drawable.bg_item_language)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        if (list.isNotEmpty()) {
            return list.size
        }
        return 0
    }

    override fun onClickViews(binding: ViewDataBinding, obj: LanguageModel, layoutPosition: Int) {
        super.onClickViews(binding, obj, layoutPosition)
        if (binding is ItemLanguageNewBinding) {
            binding.root.setOnClickListener { v: View? ->
                if (currentSelected != layoutPosition) {
                    setSelectedItem(layoutPosition)
                    onClickItemLanguage(obj, layoutPosition)
                }
            }

        }
    }

    fun setSelectedItem(position: Int) {
        notifyItemChanged(currentSelected)
        notifyItemChanged(position)
        currentSelected = position
    }
}