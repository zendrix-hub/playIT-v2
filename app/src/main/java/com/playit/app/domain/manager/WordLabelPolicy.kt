package com.playit.app.domain.manager

import kotlin.random.Random

/**
 * Which Find It pictures show their word (adviser, 2026-11-05: on harder levels, no labels, so the child picks the
 * picture by its sound alone; user: Chapter 2 mixes cards with and without the word).
 *
 * - Chapter 1: on its first letter (m) every picture shows its word, while the child learns the game. From the
 *   second letter on (s, a, i), one picture shows without its word (user, 2026-11-06).
 * - Chapter 2: the word stays on 2 of the 5 pictures, picked at random each round.
 * - Chapter 3 on, and every replay of a finished letter: pictures only.
 *
 * A picture a child would likely call by another first sound (a ladybug for "insect") keeps its word until it is
 * replaced: see the review page 2026-11-05-findit-clarity.
 */
object WordLabelPolicy {
    const val WORDS_KEPT_IN_CHAPTER_2 = 2
    const val WORDS_HIDDEN_IN_CHAPTER_1 = 1

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
     */
    fun apply(
        items: List<FindItPictureItem>,
        chapter: Int,
        positionInChapter: Int,
        isReplay: Boolean,
        random: Random = Random.Default
    ): List<FindItPictureItem> {
        val shown: Set<String> = when {
            isReplay || chapter >= 3 -> emptySet()
            chapter == 2 -> items.shuffled(random).take(WORDS_KEPT_IN_CHAPTER_2).map { it.id }.toSet()
            positionInChapter >= 1 -> {
                // Hide the word on a picture that does not keep it, or every word would still show.
                val hidden = items.filter { it.imagePath !in keepsWord }.shuffled(random)
                    .take(WORDS_HIDDEN_IN_CHAPTER_1).map { it.id }.toSet()
                items.map { it.id }.toSet() - hidden
            }
            else -> items.map { it.id }.toSet()
        }
        return items.map { it.copy(showWord = it.id in shown || it.imagePath in keepsWord) }
    }
}
