package com.playit.app.data.speech

import android.content.Context
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "VoskRecognizer"

@Singleton
class VoskRecognizer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioCapture: AudioCapture
) {
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var isListening = false

    suspend fun initModel(): Boolean = withContext(Dispatchers.IO) {
        if (model != null && recognizer != null) return@withContext true
        try {
            val modelDir = File(context.filesDir, "model")
            if (!modelDir.exists() || modelDir.list().isNullOrEmpty()) {
                copyAssetFolder(context, "model", modelDir)
            }

            if (modelDir.exists() && !modelDir.list().isNullOrEmpty()) {
                model = Model(modelDir.absolutePath)
                recognizer = Recognizer(model, 16000.0f)
                return@withContext true
            }

            val fallbackDir = File(context.filesDir, "vosk-model-small-en-us")
            if (!fallbackDir.exists() || fallbackDir.list().isNullOrEmpty()) {
                copyAssetFolder(context, "audio/vosk-model-small-en-us", fallbackDir)
            }
            if (fallbackDir.exists() && !fallbackDir.list().isNullOrEmpty()) {
                model = Model(fallbackDir.absolutePath)
                recognizer = Recognizer(model, 16000.0f)
                return@withContext true
            }

            Log.w(TAG, "Vosk acoustic model not yet bundled in assets. Speech recognition ready for model asset.")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Vosk model", e)
            false
        }
    }

    private fun copyAssetFolder(context: Context, srcName: String, dstDir: File): Boolean {
        return try {
            val fileList = context.assets.list(srcName) ?: return false
            if (fileList.isEmpty()) return false
            if (!dstDir.exists()) dstDir.mkdirs()
            for (file in fileList) {
                val srcPath = "$srcName/$file"
                val dstFile = File(dstDir, file)
                val subFiles = context.assets.list(srcPath)
                if (!subFiles.isNullOrEmpty()) {
                    copyAssetFolder(context, srcPath, dstFile)
                } else {
                    context.assets.open(srcPath).use { inStream ->
                        FileOutputStream(dstFile).use { outStream ->
                            inStream.copyTo(outStream)
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Dynamically scopes the recognizer grammar to the target lesson tokens, reducing latency (<0.2s)
     * and boosting child speech accuracy (target ≥75%).
     */
    fun setGrammar(grammarTokens: List<String>) {
        try {
            val jsonArray = JSONArray()
            grammarTokens.forEach { jsonArray.put(it) }
            jsonArray.put("[unk]")
            recognizer?.setGrammar(jsonArray.toString())
        } catch (e: Exception) {
            Log.w(TAG, "Grammar restriction failed or unsupported on current model: ${e.message}")
        }
    }

    suspend fun startListening(
        onResult: (String) -> Unit,
        onAmplitude: ((amplitudeDb: Float, normalizedLevel: Float) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        isListening = true
        audioCapture.startRecording(
            onAudioData = { buffer: ByteArray, bytesRead: Int ->
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
            },
            onAmplitude = { db, norm ->
                if (isListening) {
                    onAmplitude?.invoke(db, norm)
                }
            }
        )
    }

    suspend fun startListening(onResult: (String) -> Unit) {
        startListening(onResult, null)
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
