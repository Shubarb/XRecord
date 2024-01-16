package com.module.config.views.bases;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public abstract class BaseViewHolder3<BD extends ViewBinding> extends RecyclerView.ViewHolder {
    public BD mBinding;

    public BaseViewHolder3(BD binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }
}
