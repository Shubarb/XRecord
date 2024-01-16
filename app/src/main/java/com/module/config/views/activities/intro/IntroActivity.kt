package com.module.config.views.activities.intro

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.module.config.R
import com.module.config.utils.utils_controller.ColorUtils
import com.module.config.utils.utils_controller.SettingUtils

class IntroActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
    View.OnClickListener {
    private var tvPolicy: TextView? = null
    private var tvStartNow: TextView? = null
    private var cbEnable: CheckBox? = null
    private var isClickEnable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        initViews()
        initEvent()
    }

    private fun initViews() {
        tvPolicy = findViewById<TextView>(R.id.tv_privacy_policy)
        tvStartNow = findViewById<TextView>(R.id.tv_start_now)
        cbEnable = findViewById<CheckBox>(R.id.splashLayoutPolicy_cb)
        val spanString = SpannableString(resources.getString(R.string.privacy_policy))
        spanString.setSpan(UnderlineSpan(), 0, spanString.length, 0)
        spanString.setSpan(StyleSpan(Typeface.BOLD), 0, spanString.length, 0)
        spanString.setSpan(StyleSpan(Typeface.ITALIC), 0, spanString.length, 0)
        tvPolicy!!.text = spanString
        ColorUtils.colorStatusBar(this, resources.getColor(R.color.white), true)
    }

    private fun initEvent() {
        cbEnable!!.setOnCheckedChangeListener(this)
        tvStartNow!!.setOnClickListener(this)
        tvPolicy!!.setOnClickListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        isClickEnable = isChecked
        if (isChecked) {
            tvStartNow!!.setBackgroundResource(R.drawable.bg_boner_dialog)
            tvStartNow!!.isEnabled = true
        } else {
            tvStartNow!!.setBackgroundResource(R.drawable.shape_imv_preview)
            tvStartNow!!.isEnabled = false
        }
    }

    override fun onClick(v: View) {
        if (v === tvStartNow) {
            if (isClickEnable) {
                //OpenAM.showFirstOpenAds(this);
            } else {
                Toast.makeText(this, "Enable Policy. Please!", Toast.LENGTH_SHORT).show()
            }
        } else if (v === tvPolicy) {
            SettingUtils.funcPolicy(this, "https://sites.google.com/view/screenrecorded")
        }
    }

    override fun onBackPressed() {
        finish()
    }
}