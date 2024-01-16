package com.module.config.rx

import io.reactivex.rxjava3.functions.Consumer

class CallBackRxBus(private val callbackEventView: CallbackEventView) :
    Consumer<BusEvent> {
    @Throws(Throwable::class)
    override fun accept(busEvent: BusEvent) {
        callbackEventView.onReceivedEvent(busEvent.getType(), busEvent.data)
    }
}