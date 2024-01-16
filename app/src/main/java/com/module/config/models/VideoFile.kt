package com.module.config.models

import android.graphics.Bitmap
import android.net.Uri

class VideoFile {
    var name: String? = null
    var duration: String? = null
    var resolution: String? = null
    var path: String? = null
    var contentUri: Uri? = null
    var lastModified: Long = 0
    var size: Long = 0
    var image: Bitmap? = null
}
