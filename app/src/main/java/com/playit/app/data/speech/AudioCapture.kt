package com.playit.app.data.speech

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Captures 16kHz mono 16-bit PCM audio stream for speech recognition and acoustic analysis.
 * Uses VOICE_RECOGNITION audio source for hardware noise suppression and automatic gain control.
 * Computes per-frame RMS decibels (dB SPL estimation) and normalized voice amplitude (0.0 to 1.0)
 * for ambient noise gating and live UI visual feedback.
 */
@Singleton
class AudioCapture @Inject constructor() {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(1024)

    @SuppressLint("MissingPermission")
    suspend fun startRecording(
        onAudioData: (ByteArray, Int) -> Unit,
        onAmplitude: ((amplitudeDb: Float, normalizedLevel: Float) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        if (isRecording) return@withContext

        try {
            // Try VOICE_RECOGNITION first (enables hardware AGC, AEC, and NS), fallback to MIC
            val audioSource = try {
                val testRec = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize * 2
                )
                if (testRec.state == AudioRecord.STATE_INITIALIZED) {
                    testRec.release()
                    MediaRecorder.AudioSource.VOICE_RECOGNITION
                } else {
                    testRec.release()
                    MediaRecorder.AudioSource.MIC
                }
            } catch (e: Exception) {
                MediaRecorder.AudioSource.MIC
            }

            audioRecord = AudioRecord(
                audioSource,
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

                    if (onAmplitude != null) {
                        val (db, normalized) = calculateAmplitude(buffer, readSize)
                        onAmplitude(db, normalized)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stopRecording()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun startRecording(onAudioData: (ByteArray, Int) -> Unit) {
        startRecording(onAudioData, null)
    }

    /**
     * Calculates RMS amplitude in approximate dB SPL and normalized [0.0..1.0] scale.
     * Sensitive to child speech frequencies.
     */
    private fun calculateAmplitude(buffer: ByteArray, bytesRead: Int): Pair<Float, Float> {
        val sampleCount = bytesRead / 2
        if (sampleCount <= 0) return Pair(0f, 0f)

        var sumSquares = 0.0
        for (i in 0 until bytesRead step 2) {
            val sample = ((buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)).toShort()
            sumSquares += (sample * sample).toDouble()
        }

        val rms = sqrt(sumSquares / sampleCount)
        // High-sensitivity pediatric normalization (scales ~300-8000 RMS into 0.0-1.0)
        val normalized = (rms / 8000.0).coerceIn(0.0, 1.0).toFloat()
        val db = if (rms > 1.0) {
            (20.0 * log10(rms / 32767.0) + 90.0).coerceIn(0.0, 100.0).toFloat()
        } else {
            0f
        }

        return Pair(db, normalized)
    }

    fun stopRecording() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
    }
}
