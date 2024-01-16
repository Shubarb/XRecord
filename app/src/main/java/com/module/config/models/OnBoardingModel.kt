package com.module.config.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnBoardingModel(
    var image: Int = 0,
    var title: String = "",
    var content: String = ""
) : Parcelable