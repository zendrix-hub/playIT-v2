package com.playit.app.presentation.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.map.components.calculateNodeXOffsetDp
import com.playit.app.presentation.map.components.generateTerrainProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapPathGeometryTest {

    @Test
    fun calculateNodeXOffsetDp_isDeterministic() {
        val offset0_first = calculateNodeXOffsetDp(0, 50.dp)
        val offset0_second = calculateNodeXOffsetDp(0, 50.dp)
        val offset1_first = calculateNodeXOffsetDp(1, 50.dp)
        val offset1_second = calculateNodeXOffsetDp(1, 50.dp)

        assertEquals("Same index must yield identical offset on recomposition", offset0_first, offset0_second)
        assertEquals("Same index must yield identical offset on recomposition", offset1_first, offset1_second)
    }

    @Test
    fun calculateNodeXOffsetDp_oscillatesLeftAndRight() {
        val offset0 = calculateNodeXOffsetDp(0, 50.dp).value
        val offset1 = calculateNodeXOffsetDp(1, 50.dp).value
        val offset2 = calculateNodeXOffsetDp(2, 50.dp).value

        // Index 0: sin(0) = 0.0dp
        assertEquals(0.0f, offset0, 0.001f)

        // Index 1: sin(0.85) approx 0.75 -> ~37.5dp (positive/right)
        assertTrue("Index 1 should swing right (> 0)", offset1 > 10f)

        // Index 2: sin(1.7) approx 0.99 -> ~49.5dp (further right)
        assertTrue("Index 2 should swing right (> 0)", offset2 > 10f)
    }

    @Test
    fun generateTerrainProps_createsSeededPropsForNodes() {
        val props = generateTerrainProps(
            nodeCount = 10,
            nodeVerticalSpacingDp = 130f,
            canvasWidthDp = 400f,
            topPaddingDp = 24f
        )

        assertEquals("Should generate one prop per node", 10, props.size)

        val firstRunOffsets = props.map { it.offsetDp }
        val secondRunProps = generateTerrainProps(
            nodeCount = 10,
            nodeVerticalSpacingDp = 130f,
            canvasWidthDp = 400f,
            topPaddingDp = 24f
        )
        val secondRunOffsets = secondRunProps.map { it.offsetDp }

        assertEquals("Terrain prop positions must be deterministic", firstRunOffsets, secondRunOffsets)
    }

    @Test
    fun generateCompanionPlacements_placesExplorerLeaderAndFriends() {
        val dummyCenters = (0 until 28).map { i -> Offset(200f + i * 2f, 100f + i * 80f) }
        val placements = com.playit.app.presentation.map.components.generateCompanionPlacements(
            nodeCount = 28,
            nodeCenters = dummyCenters,
            activeNodeIndex = 0,
            activeAvatarId = 2, // Milo the Monkey
            canvasWidthDp = 400f,
            density = 2.0f
        )

        assertTrue("Should generate at least explorer leader plus supporting friends", placements.isNotEmpty())
        val leader = placements.find { it.isExplorerLeader }
        assertTrue("Leader must be present", leader != null)
        assertEquals("Leader animal must match active avatar", com.playit.app.presentation.map.components.CompanionAnimal.MONKEY, leader?.animal)
    }

    @Test
    fun getMarungkoLettersForGroup_returnsExpectedLetters() {
        assertEquals("M • S • A • I • O", com.playit.app.presentation.map.components.getMarungkoLettersForGroup(1))
        assertEquals("B • U • T • K • L", com.playit.app.presentation.map.components.getMarungkoLettersForGroup(2))
        assertEquals("Y • N • G • R • P", com.playit.app.presentation.map.components.getMarungkoLettersForGroup(3))
        assertEquals("D • H • W • C • V", com.playit.app.presentation.map.components.getMarungkoLettersForGroup(4))
        assertEquals("Z • J • F • X • Q", com.playit.app.presentation.map.components.getMarungkoLettersForGroup(5))
        assertEquals("Ñ • NG", com.playit.app.presentation.map.components.getMarungkoLettersForGroup(6))
    }
}
