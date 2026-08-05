package com.playit.app.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isTtsReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun playAssetAudio(assetPath: String, fallbackText: String? = null, onComplete: (() -> Unit)? = null) {
        stop()
        try {
            val afd = context.assets.openFd(assetPath)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                setOnCompletionListener {
                    onComplete?.invoke()
                }
                start()
            }
        } catch (e: Exception) {
            // Asset file not present; use offline TTS fallback if provided or extract word from path
            val textToSpeak = fallbackText ?: extractTextFromPath(assetPath)
            if (isTtsReady && textToSpeak.isNotBlank()) {
                tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "phoneme_tts")
            }
            onComplete?.invoke()
        }
    }

    private fun extractTextFromPath(assetPath: String): String {
        val fileName = assetPath.substringAfterLast("/").substringBeforeLast(".")
        return fileName.replace("_", " ")
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        tts?.stop()
    }
}

