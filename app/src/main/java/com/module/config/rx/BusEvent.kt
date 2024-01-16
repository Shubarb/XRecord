package com.module.config.rx

class BusEvent(type: RxBusType, data: Any?) {
    private var type: RxBusType
    var data: Any? = null

    init {
        this.type = type
        if (data != null) {
            this.data = data
        }
    }

    fun getType(): RxBusType {
        return type
    }

    fun setType(type: RxBusType) {
        this.type = type
    }
}