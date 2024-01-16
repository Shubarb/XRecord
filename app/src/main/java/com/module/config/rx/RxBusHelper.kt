package com.module.config.rx

import com.module.config.utils.utils_controller.ScreenRecordHelper

object RxBusHelper {
    fun sendTrimVideoSuccess() {
        RxBus.instance?.send(BusEvent(RxBusType.TRIM_VIDEO, null))
    }

    fun sendPermissionOverlayGranted() {
        RxBus.instance?.send(BusEvent(RxBusType.PERMISSION_GRANTED, null))
    }

    fun sendNotiMediaChange() {
        RxBus.instance?.send(BusEvent(RxBusType.NOTI_MEDIA_CHANGE, null))
    }

    fun sendScreenRecordSuccess(dstPath: String?) {
        RxBus.instance?.send(BusEvent(RxBusType.SCREEN_RECORD_SUCCESS, dstPath!!))
    }

    fun sendStartScreenShot() {
        RxBus.instance?.send(BusEvent(RxBusType.START_SCREEN_SHOT, null))
    }

    fun sendScreenShot(dstPath: String?) {
        RxBus.instance?.send(BusEvent(RxBusType.SCREEN_SHOT, dstPath!!))
    }

    fun sendCheckedTools(type: RxBusType?, isChecked: Boolean) {
        RxBus.instance?.send(BusEvent(type!!, isChecked))
    }

    fun sendClickBrushScreenShot() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_SCREEN_SHOT_BRUSH, null))
    }

    fun sendClickNotificationScreenShot() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_SCREEN_SHOT, null))
    }

    fun sendClickNotificationScreenRecord() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_SCREEN_RECORD, null))
    }

    fun sendClickNotificationTools() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_TOOLS, null))
    }

    fun sendClickNotificationHome() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_HOME, null))
    }

    fun sendClickNotificationExit() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_EXIT, null))
    }

    fun sendClickNotificationPause() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_PAUSE, null))
    }

    fun sendClickNotificationResume() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_RESUME, null))
    }

    fun sendClickNotificationStop() {
        RxBus.instance?.send(BusEvent(RxBusType.CLICK_NOTIFICATION_STOP, null))
    }

    fun sendUpdateSateNotificationRecord(state: ScreenRecordHelper.State?) {
        RxBus.instance?.send(BusEvent(RxBusType.STATE_PAUSE_OR_PLAY, state))
    }

    fun sendLoadTargetAppFinished() {
        RxBus.instance?.send(BusEvent(RxBusType.LOAD_TARGET_APP_FINISHED, null))
    }

    fun sendStartRecord() {
        RxBus.instance?.send(BusEvent(RxBusType.RECORD, null))
    }
}
