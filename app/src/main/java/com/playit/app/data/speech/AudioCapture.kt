package com.playit.app.data.speech

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioCapture @Inject constructor() {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    @SuppressLint("MissingPermission")
    suspend fun startRecording(onAudioData: (ByteArray, Int) -> Unit) = withContext(Dispatchers.IO) {
        if (isRecording) return@withContext

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize * 2
            )

            audioRecord?.startRecording()
            isRecording = true

            val buffer = ByteArray(minBufferSize)
            while (isRecording) {
                val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readSize > 0) {
                    onAudioData(buffer, readSize)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stopRecording()
        }
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.let {
            if (it.state == AudioRecord.STATE_INITIALIZED) {
                try {
                    it.stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            it.release()
        }
        audioRecord = null
    }
}
