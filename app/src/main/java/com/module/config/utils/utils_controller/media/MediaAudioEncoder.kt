package com.module.config.utils.utils_controller.media

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.media.projection.MediaProjection
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.PreferencesHelper
import java.io.IOException
import java.nio.ByteBuffer

class MediaAudioEncoder(
    muxer: MediaMuxerWrapper?,
    private val mMediaProjection: MediaProjection,
    listener: MediaEncoderListener?
) :
    MediaEncoder(muxer, listener) {
    private var mAudioThread: AudioThread? = null
    private var mAudioThread2: AudioThread2? = null

    @Throws(IOException::class)
    override fun prepare() {
        if (DEBUG) Log.v(TAG, "prepare:")
        mTrackIndex = -1
        mIsEOS = false
        mMuxerStarted = mIsEOS
        // prepare MediaCodec for AAC encoding of audio data from inernal mic.
        val audioCodecInfo = selectAudioCodec(MIME_TYPE)
            ?: return
        val audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1)
        audioFormat.setInteger(
            MediaFormat.KEY_AAC_PROFILE,
            MediaCodecInfo.CodecProfileLevel.AACObjectLC
        )
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO)
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        //		audioFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, inputFile.length());
//      audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );
        if (DEBUG) Log.i(
            TAG,
            "format: $audioFormat"
        )
        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE)
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mMediaCodec.start()
        if (DEBUG) Log.i(TAG, "prepare finishing")
        if (mListener != null) {
            try {
                mListener.onPrepared(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun startRecording() {
        super.startRecording()
        // create and execute audio capturing thread using internal mic
        if (mAudioThread == null) {
            mAudioThread = AudioThread()
            mAudioThread!!.start()
        }
        if (mAudioThread2 == null && PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE) == Config.AUDIO_MIC_AND_INTERNAL
        ) {
            mAudioThread2 = AudioThread2()
            mAudioThread2!!.start()
        }
    }

    override fun release() {
        mAudioThread = null
        mAudioThread2 = null
        super.release()
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun createAudioRecordPlayback(bufferSize: Int): AudioRecord {
        val config =
            AudioPlaybackCaptureConfiguration.Builder(mMediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .build()

        /**
         * Using hardcoded values for the audio format, Mono PCM samples with a sample rate of 8000Hz
         * These can be changed according to your application's needs
         */
        val chanelMask = if (PreferencesHelper.getString(
                PreferencesHelper.KEY_RECORD_AUDIO,
                Config.AUDIO_NONE
            ) == Config.AUDIO_MIC_AND_INTERNAL
        ) AudioFormat.CHANNEL_IN_DEFAULT else AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(chanelMask)
            .build()

        return AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .setAudioPlaybackCaptureConfig(config)
            .build()
    }

    private fun createAudioRecordMic(bufferSize: Int): AudioRecord? {
        var audioRecord: AudioRecord? = null
        val chanelMask = if (PreferencesHelper.getString(
                PreferencesHelper.KEY_RECORD_AUDIO,
                Config.AUDIO_NONE
            ) == Config.AUDIO_MIC_AND_INTERNAL
        ) AudioFormat.CHANNEL_IN_DEFAULT else AudioFormat.CHANNEL_IN_MONO
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                chanelMask, AudioFormat.ENCODING_PCM_16BIT, bufferSize
            )
            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord.release()
                audioRecord = null
            }
        } catch (e: Exception) {
            audioRecord = null
        }
        return audioRecord
    }

    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private inner class AudioThread : Thread() {
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            val audioType =
                PreferencesHelper.getString(PreferencesHelper.KEY_RECORD_AUDIO, Config.AUDIO_NONE)
            if (audioType != Config.AUDIO_NONE) {
                try {
                    val min_buffer_size = AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                    )
                    var buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER
                    if (buffer_size < min_buffer_size) buffer_size =
                        (min_buffer_size / SAMPLES_PER_FRAME + 1) * SAMPLES_PER_FRAME * 2
                    var audioRecord: AudioRecord? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        when (audioType) {
                            Config.AUDIO_INTERNAL -> audioRecord = createAudioRecordPlayback(buffer_size)
                            Config.AUDIO_MIC_AND_INTERNAL -> audioRecord = createAudioRecordPlayback(buffer_size)
                            Config.AUDIO_MIC -> audioRecord = createAudioRecordMic(buffer_size)
                        }
                    } else {
                        if (audioType == Config.AUDIO_MIC) {
                            audioRecord = createAudioRecordMic(buffer_size)
                        } else {
                            frameAvailableSoon()
                        }
                    }
                    if (audioRecord != null) {
                        try {
                            if (mIsCapturing) {
                                if (DEBUG) Log.v(TAG, "AudioThread:start audio recording")
                                val buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME)
                                var readBytes: Int
                                audioRecord.startRecording()
                                try {
                                    while (mIsCapturing && !mRequestStop && !mIsEOS) {

                                        // read audio data from internal mic
                                        buf.clear()
                                        readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME)
                                        if (readBytes > 0) {
                                            // set audio data to encoder
                                            buf.position(readBytes)
                                            buf.flip()
                                            encode(buf, readBytes, ptsUs)
                                            frameAvailableSoon()
                                        }
                                    }
                                    frameAvailableSoon()
                                } finally {
                                    audioRecord.stop()
                                }
                            }
                        } finally {
                            audioRecord.release()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                frameAvailableSoon()
            }
            if (DEBUG) Log.v(TAG, "AudioThread:finished")
        }
    }

    private inner class AudioThread2 : Thread() {
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            try {
                val min_buffer_size = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                var buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER
                if (buffer_size < min_buffer_size) buffer_size =
                    (min_buffer_size / SAMPLES_PER_FRAME + 1) * SAMPLES_PER_FRAME * 2
                val audioRecord = createAudioRecordMic(buffer_size)
                if (audioRecord != null) {
                    try {
                        if (mIsCapturing) {
                            val buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME)
                            var readBytes: Int
                            audioRecord.startRecording()
                            try {
                                while (mIsCapturing && !mRequestStop && !mIsEOS) {
                                    // read audio data from internal mic
                                    buf.clear()
                                    readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME)
                                    if (readBytes > 0) {
                                        // set audio data to encoder
                                        buf.position(readBytes)
                                        buf.flip()
                                        encode(buf, readBytes, ptsUs)
                                        frameAvailableSoon()
                                    }
                                }
                                frameAvailableSoon()
                            } finally {
                                audioRecord.stop()
                            }
                        }
                    } finally {
                        audioRecord.release()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val DEBUG = false // TODO set false on release
        private val TAG = MediaAudioEncoder::class.java.simpleName
        private const val MIME_TYPE = "audio/mp4a-latm"
        private const val SAMPLE_RATE =
            44100 // 44.1[KHz] is only setting guaranteed to be available on all devices.
        private const val BIT_RATE = 64000
        const val SAMPLES_PER_FRAME = 1024 // AAC, bytes/frame/channel
        const val FRAMES_PER_BUFFER = 25 // AAC, frame/buffer/sec
        private val AUDIO_SOURCES = intArrayOf(
            MediaRecorder.AudioSource.MIC
        )

        /**
         * select the first codec that match a specific MIME type
         *
         * @param mimeType
         * @return
         */
        private fun selectAudioCodec(mimeType: String): MediaCodecInfo? {
            if (DEBUG) Log.v(TAG, "selectAudioCodec:")
            var result: MediaCodecInfo? = null
            // get the list of available codecs
            val numCodecs = MediaCodecList.getCodecCount()
            LOOP@ for (i in 0 until numCodecs) {
                val codecInfo = MediaCodecList.getCodecInfoAt(i)
                if (!codecInfo.isEncoder) {    // skipp decoder
                    continue
                }
                val types = codecInfo.supportedTypes
                for (j in types.indices) {
                    if (DEBUG) Log.i(TAG, "supportedType:" + codecInfo.name + ",MIME=" + types[j])
                    if (types[j].equals(mimeType, ignoreCase = true)) {
                        if (result == null) {
                            result = codecInfo
                            break@LOOP
                        }
                    }
                }
            }
            return result
        }
    }
}