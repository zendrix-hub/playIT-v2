package com.playit.app.domain.model

/**
 * One sound check, played before a chapter's word game (user choice, 2026-11-06, after the teacher asked how
 * anyone can tell the child learned). Lily says each of the chapter's sounds once and the child taps its letter,
 * with no help. [known] holds the sounds the child got right and [notYet] the others, both in chapter order.
 */
data class SoundCheckResult(
    val groupId: Int,
    val checkedAt: Long,
    val known: List<String>,
    val notYet: List<String>
) {
    val total: Int get() = known.size + notYet.size
}
