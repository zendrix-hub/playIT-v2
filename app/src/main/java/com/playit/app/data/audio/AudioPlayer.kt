package com.playit.app.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
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

    /**
     * When set to true, audio playback falls back to audible dev placeholders
     * in `assets/audio/_dev_placeholder/` while emitting loud debug logs.
     */
    var useDevPlaceholders: Boolean = false

    /**
     * Plays a single asset file safely.
     * Reuses / resets the MediaPlayer instance cleanly to support rapid replay tapping.
     */
    @Synchronized
    fun playAssetAudio(assetPath: String, onComplete: (() -> Unit)? = null) {
        if (assetPath.isBlank()) {
            onComplete?.invoke()
            return
        }

        val targetPath = if (useDevPlaceholders && !assetPath.startsWith("audio/_dev_placeholder/")) {
            val devPath = audioResolver.getDevPlaceholderForAsset(assetPath)
            Log.w(TAG, "🚨 DEV PLACEHOLDER AUDIO ACTIVE: Playing dev placeholder '$devPath' for production asset '$assetPath'")
            devPath
        } else {
            assetPath
        }

        stopInternal()

        try {
            val afd = context.assets.openFd(targetPath)
            val player = mediaPlayer ?: MediaPlayer().also { mediaPlayer = it }

            player.reset()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            player.setOnCompletionListener {
                onComplete?.invoke()
            }

            player.setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "MediaPlayer error occurred for asset $targetPath: what=$what extra=$extra")
                mp.reset()
                onComplete?.invoke()
                true
            }

            player.prepare()
            player.start()
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
        Log.w(TAG, "🚨 DEV PLACEHOLDER AUDIO ACTIVE: Playing category dev placeholder '$devPath' (${category.name})")
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

        playAssetAudio(paths[index]) {
            playNextInSequence(paths, index + 1, onComplete)
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
    }
}
