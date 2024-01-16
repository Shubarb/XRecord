package com.module.config.rx

interface CallbackEventView {
    fun onReceivedEvent(type: RxBusType?, data: Any?) {}
}