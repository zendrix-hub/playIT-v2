package com.playit.app.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "AudioPlayer"

/**
 * Unified Audio Player for PlayIT.
 *
 * Enforces:
 * 1. Single audio playback pipeline to prevent overlapping audio streams.
 * 2. Real-time [isAudioPlaying] state tracking so UI buttons and transitions can be gated.
 * 3. Strict sequential playback in [playSequence] and [playSequenceAwait] where each audio asset
 *    plays completely before the next one starts, with an organic acoustic pause between assets.
 * 4. Cancellation-safe coroutine extensions ([playAssetAudioAwait], [playSequenceAwait]).
 */
@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioResolver: AudioResolver
) {

    private var mediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var activeTempFile: java.io.File? = null

    // Session token to invalidate obsolete callbacks from superseded/stopped playback
    private var currentSessionId: Long = 0L

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    val isPlaying: StateFlow<Boolean> get() = isAudioPlaying

    /**
     * When set to true, audio playback falls back to audible dev placeholders
     * in `assets/audio/_dev_placeholder/` while emitting loud debug logs.
     */
    var useDevPlaceholders: Boolean = false

    /**
     * Plays an SFX event through the managed single-pipeline audio player.
     */
    @Synchronized
    fun playSfx(event: SfxEvent, onComplete: (() -> Unit)? = null) {
        val path = audioResolver.getSfxPath(event)
        playAssetAudio(path, onComplete)
    }

    /**
     * Plays a single asset file safely.
     * Guaranteed to stop any existing playback and track completion accurately.
     */
    @Synchronized
    fun playAssetAudio(assetPath: String, onComplete: (() -> Unit)? = null) {
        if (assetPath.isBlank()) {
            onComplete?.invoke()
            return
        }

        val targetPath = if (useDevPlaceholders && !assetPath.startsWith("audio/_dev_placeholder/")) {
            val devPath = audioResolver.getDevPlaceholderForAsset(assetPath)
            Log.w(TAG, "DEV PLACEHOLDER AUDIO ACTIVE: Playing dev placeholder '$devPath' for production asset '$assetPath'")
            devPath
        } else {
            assetPath
        }

        val sessionId = ++currentSessionId
        mainHandler.removeCallbacksAndMessages(null)
        stopInternal()
        _isAudioPlaying.value = true

        mainHandler.post {
            executePlay(targetPath, sessionId) {
                _isAudioPlaying.value = false
                onComplete?.invoke()
            }
        }
    }

    private fun executePlay(targetPath: String, sessionId: Long, onComplete: (() -> Unit)?) {
        if (sessionId != currentSessionId) return
        stopInternal()
        _isAudioPlaying.value = true

        try {
            val player = mediaPlayer ?: MediaPlayer().also {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                it.setAudioAttributes(audioAttributes)
                mediaPlayer = it
            }
            player.reset()

            try {
                val afd = context.assets.openFd(targetPath)
                player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } catch (e: Exception) {
                // If openFd fails (e.g. compressed asset), copy asset to cache temp file
                Log.w(TAG, "openFd failed for $targetPath, falling back to cache file stream", e)
                val tempFile = java.io.File(context.cacheDir, "temp_audio_${System.currentTimeMillis()}.mp3")
                context.assets.open(targetPath).use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                activeTempFile = tempFile
                player.setDataSource(tempFile.absolutePath)
            }

            player.setOnPreparedListener { mp ->
                if (sessionId != currentSessionId) {
                    try { mp.reset() } catch (_: Exception) {}
                    return@setOnPreparedListener
                }
                try {
                    mp.start()
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting MediaPlayer onPrepared", e)
                    cleanupActiveTempFile()
                    onComplete?.invoke()
                }
            }

            player.setOnCompletionListener {
                if (sessionId != currentSessionId) return@setOnCompletionListener
                cleanupActiveTempFile()
                onComplete?.invoke()
            }

            player.setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "MediaPlayer error occurred for asset $targetPath: what=$what extra=$extra")
                if (sessionId != currentSessionId) return@setOnErrorListener true
                cleanupActiveTempFile()
                try { mp.reset() } catch (_: Exception) {}
                onComplete?.invoke()
                true
            }

            player.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio asset: $targetPath", e)
            cleanupActiveTempFile()
            onComplete?.invoke()
        }
    }

    /**
     * Plays multiple audio assets sequentially (e.g. SFX chime followed by mascot VO).
     * Strictly waits for each asset to finish playing completely before starting the next.
     */
    @Synchronized
    fun playSequence(assetPaths: List<String>, onComplete: (() -> Unit)? = null) {
        val validPaths = assetPaths.filter { it.isNotBlank() }
        if (validPaths.isEmpty()) {
            _isAudioPlaying.value = false
            onComplete?.invoke()
            return
        }

        val sessionId = ++currentSessionId
        mainHandler.removeCallbacksAndMessages(null)
        stopInternal()
        _isAudioPlaying.value = true

        mainHandler.post {
            playNextInSequence(validPaths, index = 0, sessionId = sessionId, onComplete = onComplete)
        }
    }

    private fun playNextInSequence(paths: List<String>, index: Int, sessionId: Long, onComplete: (() -> Unit)?) {
        if (sessionId != currentSessionId) return

        if (index >= paths.size) {
            _isAudioPlaying.value = false
            onComplete?.invoke()
            return
        }

        val currentPath = paths[index]
        val targetPath = if (useDevPlaceholders && !currentPath.startsWith("audio/_dev_placeholder/")) {
            val devPath = audioResolver.getDevPlaceholderForAsset(currentPath)
            Log.w(TAG, "DEV PLACEHOLDER AUDIO ACTIVE: Playing dev placeholder '$devPath' for production asset '$currentPath'")
            devPath
        } else {
            currentPath
        }

        executePlay(targetPath, sessionId) {
            if (sessionId != currentSessionId) return@executePlay

            if (index + 1 < paths.size) {
                // Natural acoustic gap between sequential clips (120ms)
                _isAudioPlaying.value = true
                mainHandler.postDelayed({
                    if (sessionId == currentSessionId) {
                        playNextInSequence(paths, index + 1, sessionId, onComplete)
                    }
                }, 120L)
            } else {
                _isAudioPlaying.value = false
                onComplete?.invoke()
            }
        }
    }

    /**
     * Coroutine-friendly suspension function to play an audio asset and await its full completion.
     */
    suspend fun playAssetAudioAwait(assetPath: String, timeoutMillis: Long = 8000L) {
        withTimeoutOrNull(timeoutMillis) {
            suspendCancellableCoroutine<Unit> { continuation ->
                playAssetAudio(assetPath) {
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
                continuation.invokeOnCancellation {
                    stop()
                }
            }
        }
    }

    /**
     * Coroutine-friendly suspension function to play an audio sequence and await its full completion.
     */
    suspend fun playSequenceAwait(assetPaths: List<String>, timeoutMillis: Long = 10000L) {
        withTimeoutOrNull(timeoutMillis) {
            suspendCancellableCoroutine<Unit> { continuation ->
                playSequence(assetPaths) {
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
                continuation.invokeOnCancellation {
                    stop()
                }
            }
        }
    }

    /**
     * Plays an audible dev placeholder tone explicitly for a given DevAudioCategory.
     */
    @Synchronized
    fun playDevPlaceholder(category: DevAudioCategory, onComplete: (() -> Unit)? = null) {
        val devPath = audioResolver.getDevPlaceholderPath(category)
        Log.w(TAG, "DEV PLACEHOLDER AUDIO ACTIVE: Playing category dev placeholder '$devPath' (${category.name})")
        playAssetAudio(devPath, onComplete)
    }

    /**
     * Stops current playback and clears sequence state and timers.
     */
    @Synchronized
    fun stop() {
        ++currentSessionId
        mainHandler.removeCallbacksAndMessages(null)
        stopInternal()
        cleanupActiveTempFile()
        _isAudioPlaying.value = false
    }

    private fun stopInternal() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping MediaPlayer", e)
            try {
                mediaPlayer?.release()
            } catch (_: Exception) {}
            mediaPlayer = null
        }
    }

    private fun cleanupActiveTempFile() {
        activeTempFile?.let { file ->
            try {
                if (file.exists()) file.delete()
            } catch (_: Exception) {}
            activeTempFile = null
        }
    }

    @Synchronized
    fun release() {
        stop()
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
