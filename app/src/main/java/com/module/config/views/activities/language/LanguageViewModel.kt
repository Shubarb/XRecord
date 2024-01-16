package com.module.config.views.activities.language

import android.content.res.Resources
import android.os.Build
import com.module.config.R
import com.module.config.models.LanguageModel
import com.module.config.views.bases.BaseViewModel

class LanguageViewModel : BaseViewModel() {
    val listLanguageFull = arrayListOf(
        LanguageModel("English", "en", R.drawable.ic_language_english),
        LanguageModel("Czech", "cs", R.drawable.ic_language_czech_republic),
        LanguageModel("German", "de", R.drawable.ic_language_german),
        LanguageModel("Spanish", "es", R.drawable.ic_language_spanish),
        LanguageModel("Filipino", "fil", R.drawable.ic_language_filipino),
        LanguageModel("French", "fr", R.drawable.ic_language_france),
        LanguageModel("Hindi", "hi", R.drawable.ic_language_hindi),
        LanguageModel("Croatian", "hr", R.drawable.ic_language_croatia),
        LanguageModel("Indonesian", "in", R.drawable.ic_language_indonesian),
        LanguageModel("Italian", "it", R.drawable.ic_language_italian),
        LanguageModel("Japan", "ja", R.drawable.ic_language_japan),
        LanguageModel("Korean", "ko", R.drawable.ic_language_korean),
        LanguageModel("Malay", "ms", R.drawable.ic_language_malay),
        LanguageModel("Dutch", "nl", R.drawable.ic_language_dutch),
        LanguageModel("Polish", "pl", R.drawable.ic_language_polish),
        LanguageModel("Portuguese", "pt", R.drawable.ic_language_portugal),
        LanguageModel("Russian", "ru", R.drawable.ic_language_russian),
        LanguageModel("Serbian", "sr", R.drawable.ic_language_serbian),
        LanguageModel("Swedish", "sv", R.drawable.ic_language_swedish),
        LanguageModel("Turkish", "tr", R.drawable.ic_language_turkish),
        LanguageModel("Vietnamese", "vi", R.drawable.ic_language_vietnamese)
    )

    val listLanguage = arrayListOf(
        LanguageModel("English", "en", R.drawable.ic_language_english),
        LanguageModel("Spanish", "es", R.drawable.ic_language_spanish),
        LanguageModel("French", "fr", R.drawable.ic_language_france),
        LanguageModel("Hindi", "hi", R.drawable.ic_language_hindi),
        LanguageModel("Indonesian", "in", R.drawable.ic_language_indonesian),
        LanguageModel("Portuguese", "pt", R.drawable.ic_language_portugal),
        LanguageModel("Turkish", "tr", R.drawable.ic_language_turkish),
        LanguageModel("Vietnamese", "vi", R.drawable.ic_language_vietnamese)
    )

    fun getLanguageUser(): LanguageModel? {
        val lang: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }
        val key: String = if (!getLanguageApp().contains(lang)) {
            ""
        } else {
            lang
        }
        for (model in listLanguageFull) {
            if (key == model.isoLanguage) {
                return model
            }
        }
        return null
    }

    private fun getLanguageApp() = arrayListOf(
        "en",
        "cs",
        "de",
        "es",
        "fil",
        "fr",
        "hi",
        "hr",
        "it",
        "ja",
        "ko",
        "ml",
        "ms",
        "nl",
        "pl",
        "pt",
        "ru",
        "sr",
        "sv",
        "tr",
        "vi"
    )
}