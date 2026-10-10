package com.playit.app.presentation.map

/** What a tap on a map node does (card 28). */
enum class NodeTapAction { LAUNCH, POPUP, LOCKED }

/**
 * The node the child should play next (the active node) starts on one tap, with no pop-up and no "Tap
 * the big button to start." prompt; other unlocked nodes (finished ones, for replay) open the pop-up;
 * locked nodes shake and explain (user decision 2026-10-10).
 */
fun nodeTapAction(isUnlocked: Boolean, index: Int, activeNodeIndex: Int): NodeTapAction = when {
    !isUnlocked -> NodeTapAction.LOCKED
    index == activeNodeIndex -> NodeTapAction.LAUNCH
    else -> NodeTapAction.POPUP
}

/**
 * Lily's greeting chip, short enough for one line on a 360 dp phone with a 16-character name. Tapping
 * Lily still says "Let's go! Tap a letter to begin our adventure!" (map_tarana).
 */
fun greetingFor(name: String): String = name.trim().let { if (it.isEmpty()) "Let's play!" else "Hi, $it!" }
