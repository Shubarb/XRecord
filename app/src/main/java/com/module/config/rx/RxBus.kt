package com.module.config.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

class RxBus {
    private val bus: PublishSubject<BusEvent> = PublishSubject.create()

    fun send(data: BusEvent) {
        bus.onNext(data)
    }

    private fun toObservable(): Observable<BusEvent> {
        return bus
    }

    fun subscribe(callBackRxBus: CallBackRxBus?): Disposable {
        return instance!!.toObservable().subscribe(callBackRxBus)
    }

    companion object {
        var instance: RxBus? = null
            get() {
                if (field == null) {
                    field = RxBus()
                }
                return field
            }
            private set
    }
}