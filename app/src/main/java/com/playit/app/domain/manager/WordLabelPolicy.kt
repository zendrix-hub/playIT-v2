package com.playit.app.domain.manager

import kotlin.random.Random

/**
 * Which Find It pictures show their word (adviser, 2026-11-05: on harder levels, no labels, so the child picks the
 * picture by its sound alone; user: Chapter 2 mixes cards with and without the word).
 *
 * - Chapter 1: every picture shows its word, on every letter (m, s, a, i) and on every replay. The first chapter is
 *   the easy one, and only it shows every word (user decision 2026-11-07: "in chapter 1, it is supposed to be easy
 *   ... must labels exclusively for chapter 1"; this replaces the one hidden word from its second letter on of
 *   2026-11-06).
 * - Chapter 2: the word stays on 2 of the 5 pictures, picked at random each round.
 * - Chapter 3 on, and every replay of a finished letter from Chapter 2 on: pictures only.
 *
 * A picture a child would likely call by another first sound (a ladybug for "insect") keeps its word until it is
 * replaced: see the review page 2026-11-05-findit-clarity.
 */
object WordLabelPolicy {
    const val WORDS_KEPT_IN_CHAPTER_2 = 2

    /**
     * Pictures that keep their word, from the user's choices on review page 2026-11-05-findit-clarity ("keep the
     * word"), and the redrawn ones (release 2026-11-05-findit-newart) whose subject a child may still call something
     * else. Lamp, Guitar, Horse, Jeep, Octopus and Question replaced Lit, Gap, Hen, Jet, Ox and Quiz, and show
     * without a word.
     */
    val keepsWord: Set<String> = setOf(
        // redrawn, but a child may still call it something else
        "picture_insect", "picture_ink", "picture_up", "picture_uncle", "picture_yak", "picture_quilt",
        // keep the word
        "picture_axe", "picture_igloo", "picture_envelope", "picture_top", "picture_yarn", "picture_nut",
        "blendword_pin", "blendword_fox", "picture_jug", "picture_queen", "picture_vase"
    ).map { "images/pictures/$it.png" }.toSet()

    /**
     * @param positionInChapter the letter's place in its chapter, 0 for the first (`LetterGroupMember.position`).
     *   No chapter's rule depends on it now; FindItViewModel still passes it.
     */
    fun apply(
        items: List<FindItPictureItem>,
        chapter: Int,
        @Suppress("UNUSED_PARAMETER") positionInChapter: Int,
        isReplay: Boolean,
        random: Random = Random.Default
    ): List<FindItPictureItem> {
        val shown: Set<String> = when {
            chapter <= 1 -> items.map { it.id }.toSet()
            isReplay || chapter >= 3 -> emptySet()
            else -> items.shuffled(random).take(WORDS_KEPT_IN_CHAPTER_2).map { it.id }.toSet()
        }
        return items.map { it.copy(showWord = it.id in shown || it.imagePath in keepsWord) }
    }
}
