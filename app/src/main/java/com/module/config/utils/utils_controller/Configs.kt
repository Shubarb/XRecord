package com.module.config.utils.utils_controller

import com.module.config.R
import com.module.config.models.ItemSelected

object Configs {
    const val ACTION_SHOW_TOOLS = "action.show_tools"
    const val ACTION_SHOW_CAMERA = "action.show_camera"
    const val ACTION_SCREEN_SHOT_START = "action.screens_shot"
    const val ACTION_SCREEN_RECORDING_START = "action.start_recording"
    const val ACTION_SHOW_MAIN_FLOATING = "action.show_main_floating"
    const val ACTION_OPEN_SETTING = "action.open_setting"
    const val ACTION_OPEN_MAIN = "action.open_main"
    const val ACTION_DISABLE_FLOATING = "action.disable_floating"
    const val ACTION_STOP_SHAKE = "action.shake"
    const val KEY_SCREEN_SHOT_RESULT_CODE = "screen shot result"
    const val KEY_SCREEN_SHOT_INTENT = "screen shot intent"
    const val KEY_SCREEN_RECORD_INTENT = "screen record intent"
    const val KEY_SCREEN_RECORD_RESULT_CODE = "screen record result"
    const val EXTRA_PATH = "extra path"
    const val EMAIL = "tranthuhai9559@gmail.com"
    fun getEntry(itemSelecteds: ArrayList<ItemSelected>, value: String): Int {
        for (itemSelected in itemSelecteds) {
            if (itemSelected.value == value) {
                return itemSelected.entry
            }
        }
        return itemSelecteds[0].entry
    }

    val itemsBitRate = arrayListOf<ItemSelected>(
        ItemSelected(R.string.bitrate_0, "1048576"),
        ItemSelected(R.string.bitrate_1, "2621440"),
        ItemSelected(R.string.bitrate_2, "3670016"),
        ItemSelected(R.string.bitrate_3, "4718592"),
        ItemSelected(R.string.bitrate_4, "7130317"),
        ItemSelected(R.string.bitrate_5, "10276045"),
        ItemSelected(R.string.bitrate_6, "12582912"),
        ItemSelected(R.string.bitrate_7, "25165824"),
        ItemSelected(R.string.bitrate_8, "50331648")
    )
    val itemsResolution = arrayListOf<ItemSelected>(
        ItemSelected(R.string.resolution_0, "360"),
        ItemSelected(R.string.resolution_1, "720"),
        ItemSelected(R.string.resolution_2, "1080"),
        ItemSelected(R.string.resolution_3, "1440")
    )
    val itemsFrame = arrayListOf<ItemSelected>(
        ItemSelected(R.string.frame_0, "25"),
        ItemSelected(R.string.frame_1, "30"),
        ItemSelected(R.string.frame_2, "35"),
        ItemSelected(R.string.frame_3, "40"),
        ItemSelected(R.string.frame_4, "50"),
        ItemSelected(R.string.frame_5, "60")
    )
    const val ORIENTATION_AUTO = "auto"
    const val ORIENTATION_PORTRAIT = "portrait"
    const val ORIENTATION_LANDSCAPE = "landscape"
    val itemsOrientation = arrayListOf<ItemSelected>(
        ItemSelected(R.string.orientation_0, ORIENTATION_AUTO),
        ItemSelected(R.string.orientation_1, ORIENTATION_PORTRAIT),
        ItemSelected(R.string.orientation_2, ORIENTATION_LANDSCAPE)
    )
    const val AUDIO_NONE = "none"
    const val AUDIO_MIC = "mic"
    const val AUDIO_INTERNAL = "internal"
    const val AUDIO_MIC_AND_INTERNAL = "mic and internal"
    val itemsAudio = arrayListOf<ItemSelected>(
        ItemSelected(R.string.audio_title_0, AUDIO_NONE, R.string.audio_description_0),
        ItemSelected(R.string.audio_title_1, AUDIO_MIC, R.string.audio_description_1),
        ItemSelected(R.string.audio_title_2, AUDIO_INTERNAL, R.string.audio_description_2),
        ItemSelected(R.string.audio_title_3, AUDIO_MIC_AND_INTERNAL, R.string.audio_description_3)
    )
    val itemsFileNameFomart = arrayListOf<ItemSelected>(
        ItemSelected(R.string.name_format_0, "yyyyMMdd_hhmmss"),
        ItemSelected(R.string.name_format_1, "ddMMyyyy_hhmmss"),
        ItemSelected(R.string.name_format_2, "yyMMdd_hhmmss"),
        ItemSelected(R.string.name_format_3, "ddMMyy_hhmmss"),
        ItemSelected(R.string.name_format_4, "hhMMss_yyyymmdd"),
        ItemSelected(R.string.name_format_5, "hhMMss_ddmmyyyy"),
        ItemSelected(R.string.name_format_6, "hhMMss_yymmdd"),
        ItemSelected(R.string.name_format_7, "hhMMss_ddmmyy")
    )
    val itemsLanguage = arrayListOf<ItemSelected>(
        ItemSelected(R.string.language_0, "en"),
        ItemSelected(R.string.language_1, "pt"),
        ItemSelected(R.string.language_2, "vi"),
        ItemSelected(R.string.language_3, "ru"),
        ItemSelected(R.string.language_4, "hi"),
        ItemSelected(R.string.language_5, "jp"),
        ItemSelected(R.string.language_6, "kr"),
        ItemSelected(R.string.language_7, "tr"),
        ItemSelected(R.string.language_8, "fr"),
        ItemSelected(R.string.language_9, "es"),
        ItemSelected(R.string.language_10, "ar")
    )
    val itemsTimer = arrayListOf<ItemSelected>(
        ItemSelected(R.string.timer_0, "0"),
        ItemSelected(R.string.timer_1, "3"),
        ItemSelected(R.string.timer_2, "5"),
        ItemSelected(R.string.timer_3, "7"),
        ItemSelected(R.string.timer_4, "10")
    )
}