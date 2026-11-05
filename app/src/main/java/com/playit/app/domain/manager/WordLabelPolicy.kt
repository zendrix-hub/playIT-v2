package com.playit.app.domain.manager

import kotlin.random.Random

/**
 * Which Find It pictures show their word (adviser, 2026-11-05: on harder levels, no labels, so the child picks the
 * picture by its sound alone; user: Chapter 2 mixes cards with and without the word).
 *
 * - Chapter 1: every picture shows its word, while the child learns the game.
 * - Chapter 2: the word stays on 2 of the 5 pictures, picked at random each round.
 * - Chapter 3 on, and every replay of a finished letter: pictures only.
 *
 * A picture a child would likely call by another first sound (a ladybug for "insect") keeps its word until it is
 * replaced: see the review page 2026-11-05-findit-clarity.
 */
object WordLabelPolicy {
    const val WORDS_KEPT_IN_CHAPTER_2 = 2

    /** Rated "unclear" (or waiting for a swap) on the 2026-11-05 review: they keep their word for now. */
    val keepsWord: Set<String> = setOf(
        "picture_insect", "picture_ink", "picture_ox", "picture_up", "picture_uncle", "blendword_kit",
        "blendword_lit", "picture_yak", "blendword_gap", "blendword_draw", "picture_jet", "blendword_quiz",
        "picture_quilt", "blendword_hen", "blendword_mat", "blendword_web"
    ).map { "images/pictures/$it.png" }.toSet()

    fun apply(
        items: List<FindItPictureItem>,
        chapter: Int,
        isReplay: Boolean,
        random: Random = Random.Default
    ): List<FindItPictureItem> {
        val shown: Set<String> = when {
            isReplay || chapter >= 3 -> emptySet()
            chapter == 2 -> items.shuffled(random).take(WORDS_KEPT_IN_CHAPTER_2).map { it.id }.toSet()
            else -> items.map { it.id }.toSet()
        }
        return items.map { it.copy(showWord = it.id in shown || it.imagePath in keepsWord) }
    }
}
