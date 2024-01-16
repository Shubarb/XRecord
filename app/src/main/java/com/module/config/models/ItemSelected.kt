package com.module.config.models

class ItemSelected {
    var entry: Int
    var value: String
    var description = 0

    constructor(entry: Int, value: String) {
        this.entry = entry
        this.value = value
    }

    constructor(entry: Int, value: String, description: Int) {
        this.entry = entry
        this.value = value
        this.description = description
    }
}
