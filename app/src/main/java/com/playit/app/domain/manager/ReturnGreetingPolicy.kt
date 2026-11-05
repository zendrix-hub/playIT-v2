package com.playit.app.domain.manager

import com.playit.app.domain.model.MapNode

/**
 * Lily's welcome back (adviser, 2026-11-05: from the second time a child uses the app she says "Welcome back, I
 * missed you" and asks if they want to continue their journey, on this letter). What she offers follows the map's own
 * choice of where the child is: the first open letter not yet played, else the word game the child is stuck at, else
 * any letter again once all are learned.
 */
object ReturnGreetingPolicy {

    sealed interface Offer {
        val nodeId: String?
    }

    /** The next letter to learn: Lily asks to keep going with its sound. */
    data class KeepGoing(override val nodeId: String, val letter: String) : Offer

    /** Every letter of the chapter is done and its word game is next. */
    data class BuildWords(override val nodeId: String, val chapter: Int) : Offer

    /** Every letter is learned: any letter can be played again. */
    object AllDone : Offer {
        override val nodeId: String? = null
    }

    /**
     * Greet only a child who has played before (a finished letter or a Say It try saved), and only on the first map
     * of this app launch, so coming back from a lesson never greets again.
     */
    fun shouldGreet(hasPlayedBefore: Boolean, firstMapThisLaunch: Boolean): Boolean = hasPlayedBefore && firstMapThisLaunch

    fun offerFor(nodes: List<MapNode>): Offer? {
        val letters = nodes.filterIsInstance<MapNode.LetterNode>()
        if (letters.isEmpty()) return null
        letters.firstOrNull { it.isUnlocked && it.starsEarned == 0 }?.let { return KeepGoing(it.id, it.symbol) }
        if (letters.all { it.starsEarned > 0 }) return AllDone
        val lastDone = nodes.indexOfLast { it is MapNode.LetterNode && it.starsEarned > 0 }
        val blend = nodes.drop(lastDone + 1).firstOrNull { it is MapNode.BlendItNode && it.isUnlocked } as? MapNode.BlendItNode
        return blend?.let { BuildWords(it.id, it.groupNumber) }
    }

    /** Lily's lines for [offer] (clip ids in audio/vo/ui), before the letter's own sound for [KeepGoing]. */
    fun lines(offer: Offer): List<String> = listOf("ret_welcome_back") + when (offer) {
        is KeepGoing -> listOf("ret_keep_going")
        is BuildWords -> listOf("ret_build_words")
        AllDone -> listOf("ret_all_done")
    }

    /** What the card says under the welcome: the same words as Lily. */
    fun question(offer: Offer): String = when (offer) {
        is KeepGoing -> "Do you want to keep going with this sound?"
        is BuildWords -> "Do you want to build some words?"
        AllDone -> "You learned all the sounds! Pick any letter to play again."
    }
}
