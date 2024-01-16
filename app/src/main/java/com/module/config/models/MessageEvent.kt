package com.module.config.models

open class MessageEvent(
    var messageCode: Int = 0,
    var longVal: Long = 0L,
    var stringVal: String = "",
    var booleanVal: Boolean = false,
    var objectVal: Any? = null
) {
    companion object {
        const val CODE_: Int = 1
    }
}