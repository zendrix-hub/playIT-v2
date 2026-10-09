package com.playit.app.presentation.map

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.absoluteValue

class MapLayoutTest {
    @Test fun offsetsStayInsideTheWidth() {
        listOf(360.dp, 411.dp, 800.dp).forEach { w ->
            (0 until 40).forEach { i ->
                val x = MapLayout.nodeXOffset(i, w, 76.dp)
                assertTrue("node $i at width $w: offset $x", x.value.absoluteValue + 38f <= w.value / 2f - 16f)
            }
        }
    }

    @Test fun widerScreen_widerPath() =
        assertTrue(MapLayout.nodeXOffset(2, 800.dp, 88.dp) > MapLayout.nodeXOffset(2, 360.dp, 76.dp))
}
