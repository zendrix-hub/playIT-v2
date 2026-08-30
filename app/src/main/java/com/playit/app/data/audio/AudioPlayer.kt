package com.playit.app.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AudioPlayer"

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioResolver: AudioResolver
) {

    private var mediaPlayer: MediaPlayer? = null
    private var isSequencePlaying = false

    private val soundPool: SoundPool by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    private val sfxSoundIdCache = ConcurrentHashMap<String, Int>()

    init {
        preloadCommonSfx()
    }

    /**
     * Pre-loads all standard SFX into SoundPool memory for 0ms latency hardware playback.
     */
    private fun preloadCommonSfx() {
        try {
            SfxEvent.values().forEach { event ->
                val sfxPath = audioResolver.getSfxPath(event)
                loadSoundIntoPool(sfxPath)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to preload some SFX assets into SoundPool", e)
        }
    }

    private fun loadSoundIntoPool(assetPath: String): Int? {
        sfxSoundIdCache[assetPath]?.let { return it }
        return try {
            val afd = context.assets.openFd(assetPath)
            val soundId = soundPool.load(afd.fileDescriptor, afd.startOffset, afd.length, 1)
            afd.close()
            sfxSoundIdCache[assetPath] = soundId
            soundId
        } catch (e: Exception) {
            try {
                val tempFile = java.io.File(context.cacheDir, "sfx_${assetPath.hashCode()}.mp3")
                if (!tempFile.exists()) {
                    context.assets.open(assetPath).use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                val soundId = soundPool.load(tempFile.absolutePath, 1)
                sfxSoundIdCache[assetPath] = soundId
                soundId
            } catch (ex: Exception) {
                Log.e(TAG, "Error loading SFX into SoundPool: $assetPath", ex)
                null
            }
        }
    }

    /**
     * When set to true, audio playback falls back to audible dev placeholders
     * in `assets/audio/_dev_placeholder/` while emitting loud debug logs.
     */
    var useDevPlaceholders: Boolean = false

    /**
     * Plays an SFX event instantly with zero latency via SoundPool.
     */
    @Synchronized
    fun playSfx(event: SfxEvent, onComplete: (() -> Unit)? = null) {
        val path = audioResolver.getSfxPath(event)
        playSfxInternal(path, onComplete)
    }

    private fun playSfxInternal(assetPath: String, onComplete: (() -> Unit)? = null) {
        val soundId = sfxSoundIdCache[assetPath] ?: loadSoundIntoPool(assetPath)
        if (soundId != null && soundId > 0) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            onComplete?.invoke()
        } else {
            playWithMediaPlayer(assetPath, onComplete)
        }
    }

    /**
     * Plays a single asset file safely.
     * Routes SFX to SoundPool for zero latency, and VO / speech to MediaPlayer.
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

        if (targetPath.contains("/sfx_") || targetPath.startsWith("audio/ui/sfx_")) {
            playSfxInternal(targetPath, onComplete)
            return
        }

        playWithMediaPlayer(targetPath, onComplete)
    }

    private fun playWithMediaPlayer(targetPath: String, onComplete: (() -> Unit)?) {
        stopInternal()

        try {
            val player = mediaPlayer ?: MediaPlayer().also { mediaPlayer = it }
            player.reset()

            try {
                val afd = context.assets.openFd(targetPath)
                player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } catch (e: Exception) {
                // If openFd fails (e.g. compressed asset), copy asset to cache temp file and play
                Log.w(TAG, "openFd failed for $targetPath, falling back to cache file stream", e)
                val tempFile = java.io.File(context.cacheDir, "temp_audio_${System.currentTimeMillis()}.mp3")
                context.assets.open(targetPath).use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                player.setDataSource(tempFile.absolutePath)
                tempFile.deleteOnExit()
            }

            player.setOnPreparedListener { mp ->
                try {
                    mp.start()
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting MediaPlayer onPrepared", e)
                    onComplete?.invoke()
                }
            }

            player.setOnCompletionListener {
                onComplete?.invoke()
            }

            player.setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "MediaPlayer error occurred for asset $targetPath: what=$what extra=$extra")
                mp.reset()
                onComplete?.invoke()
                true
            }

            player.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio asset: $targetPath", e)
            onComplete?.invoke()
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
     * Plays multiple audio assets sequentially (e.g. SFX chime followed by mascot VO).
     */
    @Synchronized
    fun playSequence(assetPaths: List<String>, onComplete: (() -> Unit)? = null) {
        val validPaths = assetPaths.filter { it.isNotBlank() }
        if (validPaths.isEmpty()) {
            onComplete?.invoke()
            return
        }

        isSequencePlaying = true
        playNextInSequence(validPaths, index = 0, onComplete = onComplete)
    }

    private fun playNextInSequence(paths: List<String>, index: Int, onComplete: (() -> Unit)?) {
        if (!isSequencePlaying || index >= paths.size) {
            isSequencePlaying = false
            onComplete?.invoke()
            return
        }

        val currentPath = paths[index]
        val isSfx = currentPath.contains("/sfx_") || currentPath.startsWith("audio/ui/sfx_")

        if (isSfx && index + 1 < paths.size) {
            playSfxInternal(currentPath)
            Handler(Looper.getMainLooper()).postDelayed({
                if (isSequencePlaying) {
                    playNextInSequence(paths, index + 1, onComplete)
                }
            }, 300L)
        } else {
            playAssetAudio(currentPath) {
                playNextInSequence(paths, index + 1, onComplete)
            }
        }
    }

    /**
     * Stops current playback and clears sequence state.
     */
    @Synchronized
    fun stop() {
        isSequencePlaying = false
        stopInternal()
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

    @Synchronized
    fun release() {
        stopInternal()
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        try {
            soundPool.release()
        } catch (_: Exception) {}
        sfxSoundIdCache.clear()
    }
}
