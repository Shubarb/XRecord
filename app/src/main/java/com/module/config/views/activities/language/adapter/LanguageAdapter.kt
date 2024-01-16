package com.module.config.views.activities.language.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.module.config.R
import com.module.config.databinding.ItemLanguageBinding
import com.module.config.models.LanguageModel
import com.module.config.views.bases.BaseRecyclerView

class LanguageAdapter(val onClickItemLanguage: (LanguageModel, Int) -> Unit) :
    BaseRecyclerView<LanguageModel>() {

    private var currentSelected = 0

    override fun getItemLayout() = R.layout.item_language

    override fun submitData(newData: List<LanguageModel>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun setData(binding: ViewDataBinding, item: LanguageModel, layoutPosition: Int) {
        if (binding is ItemLanguageBinding) {
            context?.let { ctx ->
                binding.imgLanguage.setImageDrawable(ContextCompat.getDrawable(ctx, item.image))
                binding.tvTitleLanguage.text = item.languageName
                binding.checkboxLanguage.isChecked = currentSelected == layoutPosition
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
        if (binding is ItemLanguageBinding) {
            binding.root.setOnClickListener { v: View? ->
                if (currentSelected != layoutPosition) {
                    setSelectedItem(layoutPosition)
                    onClickItemLanguage(obj, layoutPosition)
                }
            }
            binding.checkboxLanguage.setOnClickListener { v: View? ->
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