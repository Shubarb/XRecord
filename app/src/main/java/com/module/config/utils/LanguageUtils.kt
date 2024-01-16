package com.module.config.utils

import android.content.Context
import android.content.res.Configuration
import com.orhanobut.hawk.Hawk
import com.module.config.app.AppConstants
import java.util.*

object LanguageUtils {
    // Set Locale for Context
    fun setLocale(context: Context) {
        val language = Hawk.get(AppConstants.KEY_LANGUAGE_CODE, "en")
        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language, context)
        }
    }

    private fun changeLang(lang: String, context: Context) {
        if (lang.equals("", ignoreCase = true)) return
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}