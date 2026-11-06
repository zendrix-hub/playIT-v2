package com.playit.app.domain.manager

import com.playit.app.domain.model.SoundCheckResult
import kotlin.random.Random

/**
 * One sound check (user choice, 2026-11-06): each of the chapter's sounds once, in a random order, and for each
 * the chapter's letters as tiles, shuffled again so the answer is not always in the same place. The first tap
 * counts: the check gives no help and no second try.
 */
class SoundCheckRound(private val letters: List<String>, random: Random = Random.Default) {
    val order: List<String> = letters.shuffled(random)
    private val tileOrders: List<List<String>> = order.map { letters.shuffled(random) }
    private val answers = mutableMapOf<String, Boolean>()

    var index: Int = 0
        private set

    val current: String? get() = order.getOrNull(index)
    val tiles: List<String> get() = tileOrders.getOrNull(index).orEmpty()
    val isDone: Boolean get() = index >= order.size
    val total: Int get() = order.size

    /** Records the tap on the current sound and moves to the next one. Returns whether it was the right letter. */
    fun tap(letter: String): Boolean {
        val target = current ?: return false
        val right = letter == target
        answers[target] = right
        index++
        return right
    }

    fun result(groupId: Int, checkedAt: Long): SoundCheckResult = SoundCheckResult(
        groupId = groupId,
        checkedAt = checkedAt,
        known = letters.filter { answers[it] == true },
        notYet = letters.filter { answers[it] != true }
    )
}

/**
 * The check in plain words for parents and teachers, one chapter at a time: what the latest check found and, once
 * there are two, how the first one compares ("First time 2 of 4. Latest 4 of 4.").
 */
object SoundCheckSummary {
    fun describe(checks: List<SoundCheckResult>): String {
        val sorted = checks.sortedBy { it.checkedAt }
        val latest = sorted.lastOrNull() ?: return "not yet"
        val first = sorted.first()
        val head = when {
            sorted.size == 1 && latest.notYet.isEmpty() -> "knew all ${latest.total} sounds"
            sorted.size == 1 -> "knew ${latest.known.size} of ${latest.total} sounds"
            else -> "first time ${first.known.size} of ${first.total}, latest ${latest.known.size} of ${latest.total}"
        }
        val practise = if (latest.notYet.isEmpty()) "" else ". Practise: ${latest.notYet.joinToString(", ")}"
        return head + practise
    }

    /** The same facts one per line, for the narrow Parent Zone card: "first time 3 of 4", "Latest 4 of 4", "Practise: a". */
    fun lines(checks: List<SoundCheckResult>): List<String> {
        val sorted = checks.sortedBy { it.checkedAt }
        val latest = sorted.lastOrNull() ?: return listOf("not yet")
        val first = sorted.first()
        val head = when {
            sorted.size == 1 && latest.notYet.isEmpty() -> listOf("knew all ${latest.total} sounds")
            sorted.size == 1 -> listOf("knew ${latest.known.size} of ${latest.total} sounds")
            else -> listOf("first time ${first.known.size} of ${first.total}", "Latest ${latest.known.size} of ${latest.total}")
        }
        return head + if (latest.notYet.isEmpty()) emptyList() else listOf("Practise: ${latest.notYet.joinToString(", ")}")
    }
}
