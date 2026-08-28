package com.playit.app.domain.model

sealed class MapNode {
    abstract val id: String
    abstract val orderIndex: Int
    abstract val isUnlocked: Boolean
    abstract val groupNumber: Int

    data class LetterNode(
        override val id: String,
        override val orderIndex: Int,
        override val isUnlocked: Boolean,
        override val groupNumber: Int = 1,
        val symbol: String,
        val starsEarned: Int
    ) : MapNode()

    data class BlendItNode(
        override val id: String,
        override val orderIndex: Int,
        override val isUnlocked: Boolean,
        override val groupNumber: Int = 1,
        val groupId: String,
        val starsEarned: Int
    ) : MapNode()
}
