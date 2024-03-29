package com.module.config.utils.utils_controller.encoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.module.config.utils.utils_controller.Storage;

import java.io.ByteArrayOutputStream;

public class Mp4toGIFConverter {
    private Uri videoUri;
    private Context context;
    private long maxDur = 5000;
    private MediaMetadataRetriever mediaMetadataRetriever;

    public Mp4toGIFConverter(Context context) {
        this();
        this.context = context;
    }

    private Mp4toGIFConverter() {
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public void convertToGif() {
        try {
            mediaMetadataRetriever.setDataSource(context, videoUri);
            String DURATION = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            maxDur = (long) (Double.parseDouble(DURATION));
            TaskSaveGIF myTaskSaveGIF = new TaskSaveGIF();
            myTaskSaveGIF.execute();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Toast.makeText(context, "Something Wrong!", Toast.LENGTH_LONG).show();
        }
    }

    public class TaskSaveGIF extends AsyncTask<Void, Integer, String> {
        ProgressDialog dialog = new ProgressDialog(context);

        private String getGifFIleName() {
            String Filename = videoUri.getLastPathSegment();
            return Filename.replace("mp4", "gif");
        }

        @Override
        protected String doInBackground(Void... params) {
            return Storage.saveGif(context, genGIF());
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Please wait. Saving GIF");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            if (dialog != null && dialog.isShowing())
                dialog.cancel();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.setProgress(values[0]);
        }

        private byte[] genGIF() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GifEncoder animatedGifEncoder = new GifEncoder();
            animatedGifEncoder.setDelay(1000);
            animatedGifEncoder.setRepeat(0);
            animatedGifEncoder.setQuality(15);
            animatedGifEncoder.setFrameRate(20.0f);

            Bitmap bmFrame;
            animatedGifEncoder.start(bos);
            for (int i = 0; i < 100; i += 10) {
                long frameTime = maxDur * i / 100;
                bmFrame = mediaMetadataRetriever.getFrameAtTime(frameTime);
                animatedGifEncoder.addFrame(bmFrame);
                publishProgress(i);
            }

            bmFrame = mediaMetadataRetriever.getFrameAtTime(maxDur);
            animatedGifEncoder.addFrame(bmFrame);
            publishProgress(100);

            animatedGifEncoder.finish();
            return bos.toByteArray();
        }
    }
}
