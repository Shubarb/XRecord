package com.master.camera_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.params.StreamConfigurationMap;

@TargetApi(23)
class CameraTwoApi23 extends CameraTwo {

    CameraTwoApi23(Callback callback, PreviewImpl preview, Context context) {
        super(callback, preview, context);
    }

    @Override
    protected void collectPictureSizes(SizeMap sizes, StreamConfigurationMap map) {
        android.util.Size[] outputSizes = map.getHighResolutionOutputSizes(ImageFormat.JPEG);
        if (outputSizes != null) {
            for (android.util.Size size : map.getHighResolutionOutputSizes(ImageFormat.JPEG)) {
                sizes.add(new Size(size.getWidth(), size.getHeight()));
            }
        }
        if (sizes.isEmpty()) {
            super.collectPictureSizes(sizes, map);
        }
    }

}
