package com.module.config.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageModel(
    var languageName: String = "",
    var isoLanguage: String = "",
    var image: Int = 0,
    var isCheck: Boolean = false,
) : Parcelable