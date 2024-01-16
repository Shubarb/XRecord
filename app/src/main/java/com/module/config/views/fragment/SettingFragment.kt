package com.module.config.views.fragment

import android.content.Intent
import android.util.Log
import com.module.config.R
import com.module.config.databinding.FragmentSettingBinding
import com.module.config.rx.RxBusType
import com.module.config.service.RecorderService
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.Configs
import com.module.config.utils.utils_controller.PreferencesHelper
import com.module.config.views.activities.main.MainActivity
import com.module.config.views.bases.BaseFragment
import com.module.config.views.dialogs.DialogAppPicker
import com.module.config.views.dialogs.DialogInput
import com.module.config.views.dialogs.DialogSingleSelected

class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private var dialogBitRate: DialogSingleSelected? = null
    private var dialogFrames: DialogSingleSelected? = null
    private var dialogResolution: DialogSingleSelected? = null
    private var dialogOrientation: DialogSingleSelected? = null
    private var dialogAudio: DialogSingleSelected? = null
    private var dialogLanguge: DialogSingleSelected? = null
    private var dialogTimer: DialogSingleSelected? = null
    private var dialogFileNameFomart: DialogSingleSelected? = null
    private var dialogFileNamePrefix: DialogInput? = null
    private var dialogAppPicker: DialogAppPicker? = null

    override fun getLayoutFragment() = R.layout.fragment_setting

    override fun initViews() {
        super.initViews()
        mBinding.layoutVideoSettings.valueResolution.setText(
            Config.getEntry(
                Config.itemsResolution,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_RESOLUTION,
                    Config.itemsResolution.get(1).value
                )
            )
        )
        mBinding.layoutVideoSettings.valueBitRate.setText(
            Config.getEntry(
                Config.itemsBitRate,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_BIT_RATE,
                    Config.itemsBitRate.get(3).value
                )
            )
        )
        mBinding.layoutVideoSettings.valueFrames.setText(
            Config.getEntry(
                Config.itemsFrame,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_FRAMES,
                    Config.itemsFrame.get(0).value
                )
            )
        )
        mBinding.layoutVideoSettings.valueOrientation.setText(
            Config.getEntry(
                Config.itemsOrientation,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_ORIENTATION,
                    Config.itemsOrientation.get(0).value
                )
            )
        )
        mBinding.layoutAudioSettings.valueAudio.setText(
            Config.getEntry(
                Config.itemsAudio,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_RECORD_AUDIO,
                    Config.itemsAudio.get(0).value
                )
            )
        )
        mBinding.layoutSaveOptions.valueFileName.setText(
            Config.getEntry(
                Config.itemsFileNameFomart,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_FILE_NAME_FOMART,
                    Config.itemsFileNameFomart.get(0).value
                )
            )
        )
        mBinding.layoutSaveOptions.valueFileNamePrefix.text = PreferencesHelper.getString(
            PreferencesHelper.KEY_FILE_NAME_PREFIX,
            "recording"
        )
        mBinding.layoutLanguage.valueLanguage.setText(
            Config.getEntry(
                Config.itemsLanguage,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_LANGUAGE,
                    Config.itemsLanguage.get(0).value
                )
            )
        )
        mBinding.layoutRecordingSetting.valueCountDownTimer.setText(
            Config.getEntry(
                Config.itemsTimer,
                PreferencesHelper.getString(
                    PreferencesHelper.KEY_TIMER,
                    Config.itemsTimer.get(1).value
                )
            )
        )
        mBinding.layoutRecordingSetting.swFloatingControl.isChecked = PreferencesHelper.getBoolean(
            PreferencesHelper.KEY_FLOATING_CONTROL,
            false
        )
        mBinding.layoutRecordingSetting.swVibrate.setChecked(
            PreferencesHelper.getBoolean(
                PreferencesHelper.KEY_VIBRATE,
                false
            )
        )
        mBinding.layoutCustomApp.swTargetApp.setChecked(
            PreferencesHelper.getBoolean(
                PreferencesHelper.KEY_TARGET_APP,
                false
            )
        )
        mBinding.layoutExperimental.swSaveAsGif.setChecked(
            PreferencesHelper.getBoolean(
                PreferencesHelper.KEY_SAVE_AS_GIF,
                true
            )
        )
        mBinding.layoutExperimental.swShake.setChecked(
            PreferencesHelper.getBoolean(
                PreferencesHelper.KEY_SHAKE,
                false
            )
        )
    }

    override fun onClickViews() {
        super.onClickViews()
        activity?.let { act ->
            mBinding.layoutVideoSettings.containerBitRate.setOnClickListener {
                dialogBitRate = DialogSingleSelected(
                    act,
                    getString(R.string.bit_rate),
                    Configs.itemsBitRate,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_BIT_RATE,
                        Config.itemsBitRate.get(3).value
                    )
                )
                dialogBitRate?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(PreferencesHelper.KEY_BIT_RATE, selected!!)
                        mBinding.layoutVideoSettings.valueBitRate.setText(
                            Config.getEntry(
                                Config.itemsBitRate,
                                PreferencesHelper.getString(
                                    PreferencesHelper.KEY_BIT_RATE,
                                    act.resources.getString(Config.itemsBitRate.get(0).entry)
                                )
                            )
                        )
                    }

                })
                dialogBitRate?.show()
            }
            mBinding.layoutVideoSettings.containerFrames.setOnClickListener {
                dialogFrames = DialogSingleSelected(
                    act,
                    getString(R.string.frame_per_second),
                    Configs.itemsFrame,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_FRAMES,
                        Config.itemsFrame.get(0).value
                    )
                )
                dialogFrames?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(PreferencesHelper.KEY_FRAMES, selected)
                        mBinding.layoutVideoSettings.valueFrames.setText(
                            Config.getEntry(
                                Config.itemsFrame,
                                PreferencesHelper.getString(
                                    PreferencesHelper.KEY_FRAMES,
                                    act.resources.getString(Config.itemsFrame.get(0).entry)
                                )
                            )
                        )
                    }
                })
                dialogFrames?.show()
            }
            mBinding.layoutVideoSettings.containerResolution.setOnClickListener {
                dialogResolution = DialogSingleSelected(
                    act,
                    getString(R.string.resolution),
                    Configs.itemsResolution,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_RESOLUTION,
                        Config.itemsResolution[1].value
                    )
                )
                dialogResolution?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(PreferencesHelper.KEY_RESOLUTION, selected)
                        mBinding.layoutVideoSettings.valueResolution.setText(
                            Config.getEntry(
                                Config.itemsResolution,
                                PreferencesHelper.getString(
                                    PreferencesHelper.KEY_RESOLUTION,
                                    act.resources.getString(Config.itemsResolution[1].entry)
                                )
                            )
                        )
                    }
                })
                dialogResolution?.show()
            }
            mBinding.layoutVideoSettings.containerOrientation.setOnClickListener {
                dialogOrientation = DialogSingleSelected(
                    act,
                    getString(R.string.orientation),
                    Configs.itemsOrientation,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_ORIENTATION,
                        Config.itemsOrientation[0].value
                    )
                )
                dialogOrientation?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(PreferencesHelper.KEY_ORIENTATION, selected)
                        mBinding.layoutVideoSettings.valueOrientation.setText(
                            Config.getEntry(
                                Config.itemsOrientation,
                                PreferencesHelper.getString(
                                    PreferencesHelper.KEY_ORIENTATION,
                                    act.resources.getString(Config.itemsOrientation.get(0).entry)
                                )
                            )
                        )
                    }
                })
                dialogOrientation?.show()
            }
            mBinding.layoutAudioSettings.containerRecordAudio.setOnClickListener {
                dialogAudio = DialogSingleSelected(
                    act,
                    getString(R.string.record_audio),
                    Configs.itemsAudio,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_RECORD_AUDIO,
                        Config.itemsAudio.get(0).value
                    )
                )
                dialogAudio?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        if (selected.equals(
                                Config.itemsAudio.get(1).value
                            ) || selected.equals(
                                Config.itemsAudio.get(2).value
                            ) || selected.equals(Config.itemsAudio.get(3).value)
                        ) {
                            mBinding.layoutAudioSettings.valueAudio.setText(
                                Config.getEntry(
                                    Config.itemsAudio,
                                    PreferencesHelper.getString(
                                        PreferencesHelper.KEY_RECORD_AUDIO,
                                        act.resources.getString(Config.itemsAudio[0].entry)
                                    )
                                )
                            )
                        } else {
                            PreferencesHelper.putString(
                                PreferencesHelper.KEY_RECORD_AUDIO,
                                selected
                            )
                            mBinding.layoutAudioSettings.valueAudio.setText(
                                Config.getEntry(
                                    Config.itemsAudio,
                                    PreferencesHelper.getString(
                                        PreferencesHelper.KEY_RECORD_AUDIO,
                                        act.resources.getString(Config.itemsAudio.get(0).entry)
                                    )
                                )
                            )
                        }
                    }
                })
                dialogAudio?.show()
            }
            mBinding.layoutSaveOptions.containerFileName.setOnClickListener {
                dialogFileNameFomart = DialogSingleSelected(
                    act,
                    getString(R.string.file_name_fomart),
                    Configs.itemsFileNameFomart,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_FILE_NAME_FOMART,
                        Config.itemsFileNameFomart[0].value
                    )
                )
                dialogFileNameFomart?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(
                            PreferencesHelper.KEY_FILE_NAME_FOMART,
                            selected
                        )
                        mBinding.layoutSaveOptions.valueFileName.setText(
                            Config.getEntry(
                                Config.itemsFileNameFomart,
                                PreferencesHelper.getString(
                                    PreferencesHelper.KEY_FILE_NAME_FOMART,
                                    act.resources.getString(Config.itemsFileNameFomart[0].entry)
                                )
                            )
                        )
                    }
                })
                dialogFileNameFomart?.show()
            }
            mBinding.layoutSaveOptions.containerFileNamePrefix.setOnClickListener {
                dialogFileNamePrefix = DialogInput(
                    act,
                    getString(R.string.file_name_prefix),
                    PreferencesHelper.getString(PreferencesHelper.KEY_FILE_NAME_PREFIX, "recording")
                )
                dialogFileNamePrefix?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(
                            PreferencesHelper.KEY_FILE_NAME_PREFIX,
                            selected!!
                        )
                        mBinding.layoutSaveOptions.valueFileNamePrefix.setText(
                            PreferencesHelper.getString(
                                PreferencesHelper.KEY_FILE_NAME_PREFIX,
                                "recording"
                            )
                        )
                    }
                })
                dialogFileNamePrefix?.show()
            }
            mBinding.layoutLanguage.containerLanguage.setOnClickListener {
                dialogLanguge = DialogSingleSelected(
                    act,
                    getString(R.string.menu_language),
                    Configs.itemsLanguage,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_LANGUAGE,
                        Config.itemsLanguage[0].value
                    )
                )
                dialogLanguge?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
//                        getBaseActivity().setLanguage(selected)
                    }
                })
                dialogLanguge?.show()
            }
            mBinding.layoutRecordingSetting.containerCountDownTimer.setOnClickListener {
                dialogTimer = DialogSingleSelected(
                    act,
                    getString(R.string.count_down_timer),
                    Configs.itemsTimer,
                    PreferencesHelper.getString(
                        PreferencesHelper.KEY_TIMER,
                        Config.itemsTimer[1].value
                    )
                )
                dialogTimer?.callback(object : DialogSingleSelected.CallBackDialog {
                    override fun onOK(selected: String?) {
                        PreferencesHelper.putString(PreferencesHelper.KEY_TIMER, selected)
                        mBinding.layoutRecordingSetting.valueCountDownTimer.setText(
                            Config.getEntry(
                                Config.itemsTimer,
                                PreferencesHelper.getString(
                                    PreferencesHelper.KEY_TIMER,
                                    act.resources.getString(Config.itemsTimer[0].entry)
                                )
                            )
                        )
                    }
                })
                dialogTimer?.show()
            }
            mBinding.layoutCustomApp.containerTargetApp.setOnClickListener {
                mBinding.layoutCustomApp.swTargetApp.isChecked = !mBinding.layoutCustomApp.swTargetApp.isChecked
                PreferencesHelper.putBoolean(
                    PreferencesHelper.KEY_TARGET_APP,
                    mBinding.layoutCustomApp.swTargetApp.isChecked
                )
            }
            mBinding.layoutCustomApp.containerChosseApp.setOnClickListener {
                dialogAppPicker = DialogAppPicker(act)
                dialogAppPicker?.show()
            }
            mBinding.layoutRecordingSetting.containerFloatingControl.setOnClickListener {
                val currentState =
                    PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false)
                checkFloatingControl(currentState)
            }
            mBinding.layoutRecordingSetting.containerVibrate.setOnClickListener {
                mBinding.layoutRecordingSetting.swVibrate.isChecked = !mBinding.layoutRecordingSetting.swVibrate.isChecked
                PreferencesHelper.putBoolean(
                    PreferencesHelper.KEY_VIBRATE,
                    mBinding.layoutRecordingSetting.swVibrate.isChecked
                )
            }
            mBinding.layoutExperimental.containerSaveAsGif.setOnClickListener {
                mBinding.layoutExperimental.swSaveAsGif.isChecked = !mBinding.layoutExperimental.swSaveAsGif.isChecked
                PreferencesHelper.putBoolean(
                    PreferencesHelper.KEY_SAVE_AS_GIF,
                    mBinding.layoutExperimental.swSaveAsGif.isChecked
                )
            }
            mBinding.layoutExperimental.containerShake.setOnClickListener {
                mBinding.layoutExperimental.swShake.isChecked = !mBinding.layoutExperimental.swShake.isChecked
                PreferencesHelper.putBoolean(
                    PreferencesHelper.KEY_SHAKE,
                    mBinding.layoutExperimental.swShake.isChecked
                )
            }

        }
    }

    private fun checkFloatingControl(currentState: Boolean) {
        activity?.let { act ->
            Log.e("CHECKFLOAT", "float: $currentState")
            if (!currentState) {
                if (act is MainActivity) {
                    mBinding.layoutRecordingSetting.swFloatingControl.isChecked = true
                    PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
                    act.askPermissionOverlay()
                    act.startService()
                } else {

                }
            } else {
                PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false)
                mBinding.layoutRecordingSetting.swFloatingControl.isChecked = false
                val intent = Intent(act, RecorderService::class.java)
                intent.action = Config.ACTION_DISABLE_FLOATING
                act.startService(intent)
            }
        }
    }

    override fun onReceivedEvent(type: RxBusType?, data: Any?) {
        when (type) {
            RxBusType.PERMISSION_GRANTED -> mBinding.layoutRecordingSetting.swFloatingControl.isChecked =
                PreferencesHelper.getBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, false)
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogBitRate != null) {
            dialogBitRate?.dismiss()
        }
        if (dialogFrames != null) {
            dialogFrames?.dismiss()
        }
        if (dialogResolution != null) {
            dialogResolution?.dismiss()
        }
        if (dialogOrientation != null) {
            dialogOrientation?.dismiss()
        }
        if (dialogAudio != null) {
            dialogAudio?.dismiss()
        }
        if (dialogFileNameFomart != null) {
            dialogFileNameFomart?.dismiss()
        }
        if (dialogFileNamePrefix != null) {
            dialogFileNamePrefix?.dismiss()
        }
        if (dialogLanguge != null) {
            dialogLanguge?.dismiss()
        }
        if (dialogTimer != null) {
            dialogTimer?.dismiss()
        }
        if (dialogAppPicker != null) {
            dialogAppPicker?.dismiss()
        }
    }
}
