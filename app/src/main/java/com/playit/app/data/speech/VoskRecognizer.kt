package com.playit.app.data.speech

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "VoskRecognizer"

@Singleton
class VoskRecognizer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var isReady = false
    private var lastRecognizedText: String = ""
    private var activeGrammarJson: String? = null

    fun isModelReady(): Boolean = isReady && model != null

    suspend fun initModel(): Boolean = suspendCancellableCoroutine { continuation ->
        if (model != null && isReady) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        try {
            StorageService.unpack(
                context,
                "vosk-model",
                "model",
                { loadedModel ->
                    model = loadedModel
                    isReady = true
                    Log.i(TAG, "Vosk acoustic model successfully unpacked and initialized")
                    continuation.resume(true)
                },
                { exception ->
                    Log.e(TAG, "Vosk model unpacking error: ${exception.message}", exception)
                    isReady = false
                    continuation.resume(false)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error unpacking Vosk model: ${e.message}", e)
            isReady = false
            continuation.resume(false)
        }
    }

    /**
     * Dynamically scopes the recognizer grammar to the target lesson tokens, reducing latency (<0.2s)
     * and boosting child speech accuracy (target ≥75%).
     */
    fun setGrammar(grammarTokens: List<String>) {
        try {
            val jsonArray = JSONArray()
            grammarTokens.forEach { jsonArray.put(it.lowercase().trim()) }
            jsonArray.put("[unk]")
            activeGrammarJson = jsonArray.toString()
        } catch (e: Exception) {
            Log.w(TAG, "Grammar formatting error: ${e.message}")
            activeGrammarJson = null
        }
    }

    /**
     * Starts listening using Android's native SpeechService with RecognitionListener callbacks.
     */
    fun startListening(
        onResult: (String) -> Unit
    ) {
        val currentModel = model ?: run {
            Log.w(TAG, "Cannot start listening: Vosk model not loaded yet")
            return
        }

        try {
            speechService?.stop()
            speechService?.shutdown()
            speechService = null
            lastRecognizedText = ""

            val recognizer = if (!activeGrammarJson.isNullOrBlank()) {
                try {
                    Recognizer(currentModel, 16000.0f, activeGrammarJson)
                } catch (e: Exception) {
                    Log.w(TAG, "Could not initialize with grammar, falling back to open vocabulary", e)
                    Recognizer(currentModel, 16000.0f)
                }
            } else {
                Recognizer(currentModel, 16000.0f)
            }

            val service = SpeechService(recognizer, 16000.0f)
            speechService = service

            service.startListening(object : RecognitionListener {
                override fun onPartialResult(hypothesis: String?) {
                    if (hypothesis == null) return
                    val text = parseResult(hypothesis, "partial")
                    if (text.isNotBlank()) {
                        lastRecognizedText = text
                        onResult(text)
                    }
                }

                override fun onResult(hypothesis: String?) {
                    if (hypothesis == null) return
                    val text = parseResult(hypothesis, "text")
                    if (text.isNotBlank()) {
                        lastRecognizedText = text
                        onResult(text)
                    }
                }

                override fun onFinalResult(hypothesis: String?) {
                    if (hypothesis == null) return
                    val text = parseResult(hypothesis, "text")
                    if (text.isNotBlank()) {
                        lastRecognizedText = text
                        onResult(text)
                    }
                }

                override fun onError(exception: java.lang.Exception?) {
                    Log.e(TAG, "Vosk recognition error: ${exception?.message}")
                }

                override fun onTimeout() {
                    Log.d(TAG, "Vosk recognition timeout")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Vosk SpeechService: ${e.message}", e)
        }
    }

    fun stopListening(): String {
        try {
            speechService?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping SpeechService: ${e.message}")
        }
        val text = lastRecognizedText
        lastRecognizedText = ""
        return text
    }

    fun release() {
        try {
            speechService?.shutdown()
        } catch (_: Exception) {}
        speechService = null
        try {
            model?.close()
        } catch (_: Exception) {}
        model = null
        isReady = false
    }

    private fun parseResult(jsonStr: String, key: String): String {
        return try {
            JSONObject(jsonStr).optString(key, "").trim()
        } catch (e: Exception) {
            ""
        }
    }
}
