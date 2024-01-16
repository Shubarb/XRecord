package com.module.config.views.activities.language

import android.content.Intent
import androidx.activity.viewModels
import com.ga.controller.network.ga.NativeLanguage
import com.module.config.R
import com.module.config.app.AppConstants
import com.module.config.databinding.ActivityLanguageBinding
import com.module.config.models.LanguageModel
import com.module.config.utils.LanguageUtils
import com.module.config.views.bases.BaseActivity
import com.module.config.views.activities.language.adapter.LanguageNewAdapter
import com.module.config.views.activities.main.MainActivity
import com.module.config.views.activities.permission.PermissionActivity
import com.orhanobut.hawk.Hawk

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private val mViewModel: LanguageViewModel by viewModels()
    private var languageAdapter: LanguageNewAdapter? = null
    private var languageModel: LanguageModel? = null
    private var isOpenFromSetting = false
    private var listLanguage = arrayListOf<LanguageModel>()

    override fun getLayoutActivity() = R.layout.activity_language

    override fun initViews() {
        super.initViews()
        NativeLanguage.getInstance().showNativeLanguage(this, mBinding.lnNative)
        intent?.let {
            if (it.hasExtra(AppConstants.OPEN_LANGUAGE_FROM_SETTINGS)) {
                isOpenFromSetting =
                    it.getBooleanExtra(AppConstants.OPEN_LANGUAGE_FROM_SETTINGS, false)
            }
        }
        listLanguage.clear()
        listLanguage.addAll(mViewModel.listLanguage)
        mViewModel.getLanguageUser()?.let { language ->
            if (language !in listLanguage) {
                listLanguage.remove(listLanguage[listLanguage.size - 1])
                listLanguage.add(0, language)
            }
        }

        initList()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.tvOk.setOnClickListener {
            Hawk.put(AppConstants.KEY_LANGUAGE_MODEL, languageModel)
            Hawk.put(AppConstants.KEY_LANGUAGE_CODE, languageModel?.isoLanguage)
            LanguageUtils.setLocale(this)
            if (isOpenFromSetting) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initList() {
        setupList()
        languageAdapter = LanguageNewAdapter { languageModel, position ->
            this.languageModel = languageModel
        }
        languageAdapter?.submitData(listLanguage)
        if (isOpenFromSetting) {
            val keyLanguageCode = Hawk.get(AppConstants.KEY_LANGUAGE_CODE, "en")
            val model = listLanguage.findLast {
                it.isoLanguage == keyLanguageCode
            }
            val position = listLanguage.indexOf(model)
            languageAdapter?.setSelectedItem(position)
        }

        mBinding.rvLanguage.apply {
            setHasFixedSize(true)
            adapter = languageAdapter
        }
    }

    private fun setupList() {
        val key: String = Hawk.get(AppConstants.KEY_LANGUAGE_CODE, "en")
        for (i in listLanguage.indices) {
            if (key == listLanguage[i].isoLanguage) {
                val data = listLanguage[i]
                data.isCheck = true
                listLanguage.remove(listLanguage[i])
                listLanguage.add(0, data)
                break
            }
        }
        languageModel = listLanguage[0]
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}