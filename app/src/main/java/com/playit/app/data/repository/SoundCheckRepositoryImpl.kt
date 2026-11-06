package com.playit.app.data.repository

import android.content.Context
import com.playit.app.domain.model.SoundCheckResult
import com.playit.app.domain.repository.SoundCheckRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sound checks in the app's private preferences, one line per check, so the Room schema (and the children's data
 * with it) stays as it is. At most [MAX_CHECKS] per child; the oldest go first, except each chapter's first check,
 * which the record compares with the latest.
 */
@Singleton
class SoundCheckRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SoundCheckRepository {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun save(profileId: Long, result: SoundCheckResult) = withContext(Dispatchers.IO) {
        synchronized(this@SoundCheckRepositoryImpl) {
            val all = decode(prefs.getString(key(profileId), null)) + result
            prefs.edit().putString(key(profileId), encode(trim(all))).apply()
        }
    }

    override suspend fun resultsFor(profileId: Long): List<SoundCheckResult> = withContext(Dispatchers.IO) {
        decode(prefs.getString(key(profileId), null)).sortedBy { it.checkedAt }
    }

    override suspend fun deleteFor(profileId: Long) = withContext(Dispatchers.IO) {
        prefs.edit().remove(key(profileId)).apply()
    }

    companion object {
        const val PREFS_NAME = "playit_sound_checks"
        const val MAX_CHECKS = 100

        private fun key(profileId: Long) = "checks_$profileId"

        internal fun encode(checks: List<SoundCheckResult>): String = checks.joinToString("\n") {
            "${it.groupId}|${it.checkedAt}|${it.known.joinToString(",")}|${it.notYet.joinToString(",")}"
        }

        /** Lines that do not parse are skipped, so one bad line never hides the others. */
        internal fun decode(text: String?): List<SoundCheckResult> = text.orEmpty().lines().mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size != 4) return@mapNotNull null
            val groupId = parts[0].toIntOrNull() ?: return@mapNotNull null
            val at = parts[1].toLongOrNull() ?: return@mapNotNull null
            fun letters(s: String) = s.split(",").filter { it.isNotBlank() }
            SoundCheckResult(groupId, at, letters(parts[2]), letters(parts[3]))
        }

        internal fun trim(checks: List<SoundCheckResult>): List<SoundCheckResult> {
            val sorted = checks.sortedBy { it.checkedAt }
            if (sorted.size <= MAX_CHECKS) return sorted
            val firsts = sorted.groupBy { it.groupId }.values.map { it.first() }.toSet()
            val dropping = sorted.size - MAX_CHECKS
            val dropped = sorted.filter { it !in firsts }.take(dropping).toSet()
            return sorted.filter { it !in dropped }
        }
    }
}
