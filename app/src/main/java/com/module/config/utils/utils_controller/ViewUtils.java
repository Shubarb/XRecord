package com.module.config.utils.utils_controller;

import static com.module.config.utils.utils_controller.Toolbox.hideSoftKeyboard;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ViewUtils {

    public static void setupUI(final View view, final Activity context) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(context);
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }

    public static void loadImage(Context context, Object image, ImageView imageView) {
        Glide.with(context)
                .load(image)
                .apply(new RequestOptions().centerCrop())
                .into(imageView);
    }

    public static void scaleSelected(View... views) {
        for (View view : views) {
            view.setOnTouchListener((v, event) -> {
                v.setPivotX(v.getWidth() / 2);
                v.setPivotY(v.getHeight() / 2);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setScaleX(0.9f);
                        v.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setScaleX(1f);
                        v.setScaleY(1f);
                        break;
                }
                return false;
            });
        }
    }
}

