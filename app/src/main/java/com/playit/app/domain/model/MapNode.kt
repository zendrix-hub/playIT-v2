package com.playit.app.domain.model

sealed class MapNode {
    abstract val id: String
    abstract val orderIndex: Int
    abstract val isUnlocked: Boolean

    data class LetterNode(
        override val id: String,
        override val orderIndex: Int,
        override val isUnlocked: Boolean,
        val symbol: String,
        val starsEarned: Int
    ) : MapNode()

    data class BlendItNode(
        override val id: String,
        override val orderIndex: Int,
        override val isUnlocked: Boolean,
        val groupId: String,
        val starsEarned: Int
    ) : MapNode()
}
