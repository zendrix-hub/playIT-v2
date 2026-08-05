package com.playit.app.data.speech

import android.content.Context
import org.vosk.Model
import org.vosk.Recognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoskRecognizer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioCapture: AudioCapture
) {
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var isListening = false

    suspend fun initModel(): Boolean = withContext(Dispatchers.IO) {
        if (model != null) return@withContext true
        try {
            val modelPath = File(context.filesDir, "vosk-model-small-en-us")
            if (!modelPath.exists()) {
                modelPath.mkdirs()
            }
            model = Model(modelPath.absolutePath)
            recognizer = Recognizer(model, 16000.0f)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun startListening(onResult: (String) -> Unit) = withContext(Dispatchers.IO) {
        isListening = true
        audioCapture.startRecording { buffer: ByteArray, bytesRead: Int ->
            if (!isListening) return@startRecording
            recognizer?.let { rec ->
                if (rec.acceptWaveForm(buffer, bytesRead)) {
                    val resultJson = rec.result
                    val text = parseTextFromJson(resultJson)
                    if (text.isNotBlank()) {
                        onResult(text)
                    }
                }
            }
        }
    }

    fun stopListening(): String {
        isListening = false
        audioCapture.stopRecording()
        val finalResultJson = recognizer?.finalResult ?: ""
        return parseTextFromJson(finalResultJson)
    }

    fun release() {
        isListening = false
        audioCapture.stopRecording()
        try {
            recognizer?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            model?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recognizer = null
        model = null
    }

    private fun parseTextFromJson(jsonStr: String): String {
        return try {
            JSONObject(jsonStr).optString("text", "")
        } catch (e: Exception) {
            ""
        }
    }
}
