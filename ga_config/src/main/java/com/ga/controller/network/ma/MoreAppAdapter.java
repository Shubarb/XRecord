package com.ga.controller.network.ma;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ga.controller.R;
import com.ga.controller.network.model.MoreAppObj;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class MoreAppAdapter extends RecyclerView.Adapter {

    private ArrayList<MoreAppObj> mListDocument;
    private Activity mActivity;
    private MoreAppAdapter.onClickItem mOnClickItem;
    private RecyclerView.ViewHolder viewHolder;

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    private void setViewHolder(RecyclerView.ViewHolder myViewHolder) {
        this.viewHolder = myViewHolder;
    }

    public void setOnClickItem(MoreAppAdapter.onClickItem mOnClickItem) {
        this.mOnClickItem = mOnClickItem;
    }

    public MoreAppAdapter(ArrayList<MoreAppObj> mListDocument, Activity activity) {
        this.mListDocument = mListDocument;
        this.mActivity = activity;
    }

    @Override
    public int getItemCount() {
        return mListDocument != null ? mListDocument.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ItemHolder(viewHolder, position);
    }

    private void ItemHolder(@NonNull RecyclerView.ViewHolder holder1, final int position) {
        setViewHolder(holder1);
        MoreAppHolder holder = (MoreAppHolder) holder1;
        holder.tvTitleApp.setText(mListDocument.get(position).getTitleApp());
        holder.tvDesApp.setText(mListDocument.get(position).getDesApp());

        PackageManager pm = mActivity.getPackageManager();
        if (isPackageInstalled(mListDocument.get(position).getPackageName(), pm)) {
            holder.btnGoCH.setText("Open App");
        } else {
            holder.btnGoCH.setText("Install App");
        }

        Glide.with(mActivity)
                .load(mListDocument.get(position).getLinkCover())
                .error(R.color.mbridge_black_66)
                .into(holder.imgCover);

        Glide.with(mActivity)
                .load(mListDocument.get(position).getLinkLogo())
                .error(R.color.mbridge_black_66)
                .into(holder.imgLogo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickItem != null)
                    mOnClickItem.onClickItem(position);
            }
        });

        if (!isPackageInstalled(mListDocument.get(position).getPackageName(), pm)) {
            holder.btnGoCH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItem != null)
                        mOnClickItem.onClickItem(position);
                }
            });
        } else {
            holder.btnGoCH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(mActivity, mListDocument.get(position).getPackageName());
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View cell = inflater.inflate(R.layout.item_more_app, container, false);
        return new MoreAppHolder(cell);
    }

    static class MoreAppHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        ImageView imgLogo;
        TextView tvTitleApp;
        TextView tvDesApp;
        Button btnGoCH;

        MoreAppHolder(View itemView) {
            super(itemView);

            imgCover = itemView.findViewById(R.id.img_cover);
            imgLogo = itemView.findViewById(R.id.img_logo);
            tvTitleApp = itemView.findViewById(R.id.tv_title_app);
            tvDesApp = itemView.findViewById(R.id.tv_des_app);
            btnGoCH = itemView.findViewById(R.id.btn_go_ch);
        }
    }

    public interface onClickItem {
        void onClickItem(int position);
    }

    private void openApp(Activity mContext, String packageName) {
        try {
            Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                mContext.startActivity(launchIntent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
