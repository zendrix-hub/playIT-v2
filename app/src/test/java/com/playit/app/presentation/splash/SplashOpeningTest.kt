package com.playit.app.presentation.splash

import androidx.compose.runtime.MonotonicFrameClock
import com.playit.app.data.audio.SfxEvent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The opening (user, 2026-10-29: "a good motion design"): its beats come in order and it ends at rest. */
class SplashOpeningTest {

    private val end = OPENING_END_MS.toFloat()

    @Test fun everything_isAtRestWhenTheOpeningEnds() {
        assertEquals(LilyFrame(1f, 1f, 1f), lilyAt(end))
        assertEquals(DropFrame.REST, pawsAt(end))
        TITLE.indices.forEach { assertEquals(DropFrame.REST, letterAt(it, end)) }
        assertEquals(1f, taglineAt(end), 0f)
        assertEquals(0f, taglineRiseAt(end), 0f)
        assertEquals(PopFrame(1f, 1f), startPopAt(end))
        assertNull(shineAt(end))
        SPARKLES.indices.forEach { assertEquals(0f, sparkleAt(it, end).first, 0f) }
        CLOUDS.indices.forEach { assertEquals(0f, cloudAt(it, end), 0f) }
    }

    @Test fun nothing_showsBeforeItsBeat() {
        assertEquals(0f, lilyAt(0f).shown, 0f)
        assertEquals(0f, pawsAt(0f).alpha, 0f)
        TITLE.indices.forEach { assertEquals(0f, letterAt(it, 0f).alpha, 0f) }
        assertEquals(0f, taglineAt(0f), 0f)
        assertEquals(0f, startPopAt(0f).alpha, 0f)
        CLOUDS.indices.forEach { assertEquals("cloud $it starts off screen", 1f, cloudAt(it, 0f), 0f) }
    }

    @Test fun lily_peeksThenCrouchesThenJumpsAboveHerPlaceAndLands() {
        assertTrue(LILY_PEEK_END_MS < LILY_CROUCH_MS && LILY_CROUCH_MS < LILY_JUMP_MS && LILY_JUMP_MS < LILY_TOP_MS)
        assertTrue(LILY_TOP_MS < LILY_LAND_MS && LILY_LAND_MS < LILY_SETTLED_MS)
        assertEquals("her eyes peek over the edge", LILY_PEEK, lilyAt(LILY_PEEK_END_MS.toFloat()).shown, 0.001f)
        assertTrue("she blinks while peeking", LILY_BLINK_MS in LILY_PEEK_END_MS..LILY_CROUCH_MS)
        assertTrue("she sinks before the jump", lilyAt(LILY_JUMP_MS - 1f).shown < LILY_PEEK)
        val highest = (0..OPENING_END_MS).maxOf { lilyAt(it.toFloat()).shown }
        assertEquals(LILY_TOP, highest, 0.001f)
        assertTrue("stretched as she leaves", lilyAt(LILY_JUMP_MS + 1f).scaleY > 1.05f)
        assertTrue("squashed as she lands", lilyAt((LILY_LAND_MS + LILY_SETTLED_MS) / 2f).scaleY < 0.95f)
        assertTrue("her paws land as she does", PAWS_MS + PAWS_DROP_MS in LILY_LAND_MS..LILY_SETTLED_MS)
    }

    @Test fun letters_fallInOrderStretchedAndSquashAsTheyLand() {
        assertTrue("the title starts as Lily jumps", TITLE_MS in LILY_JUMP_MS..LILY_LAND_MS)
        val falling = TITLE_MS + LETTER_MS * 0.4f
        assertTrue(letterAt(0, falling).scaleY > 1.1f)
        assertTrue("the next letter is still waiting", letterAt(TITLE.length - 1, falling).alpha == 0f)
        val landing = TITLE_MS + LETTER_MS * 0.6f
        assertTrue(letterAt(0, landing).scaleY < 0.9f && letterAt(0, landing).scaleX > 1.1f)
        val lastLands = TITLE_MS + (TITLE.length - 1) * TITLE_STAGGER_MS + LETTER_MS
        assertTrue("the wave starts once every letter has landed", TITLE_WAVE_MS >= lastLands)
        assertTrue("the wave lifts a letter", letterAt(0, TITLE_WAVE_MS + WAVE_LEN_MS / 2f).lift > 0.1f)
    }

    @Test fun lastBeat_followsTheTitleAndTheStartButtonIsReadySoon() {
        assertTrue(TAGLINE_MS > TITLE_MS && START_POP_MS > TAGLINE_MS)
        assertTrue("a child never waits long for Start", START_POP_MS + START_POP_LEN_MS <= 2200)
        assertTrue("the shine follows the pop", SHINE_MS >= START_POP_MS + START_POP_LEN_MS)
        assertTrue(SPARKLE_MS > TITLE_WAVE_MS && LILY_WINK_MS > START_POP_MS)
        assertTrue("about three seconds in all", OPENING_END_MS in 2000..3200)
        assertTrue("it ends with Lily's eyes open", OPENING_END_MS > LILY_WINK_MS + WINK_LEN_MS)
        assertTrue("a sparkle twinkles", sparkleAt(0, SPARKLE_MS + SPARKLE_LEN_MS / 2f).first > 0.9f)
    }

    @Test fun settle_overshootsOnceAndEndsExactlyAtOne() {
        assertEquals(0f, settle(0f), 0f)
        assertEquals(1f, settle(1f), 0f)
        val peak = (0..100).maxOf { settle(it / 100f) }
        assertTrue("$peak", peak in 1.10f..1.16f)
    }

    @Test fun sounds_followTheBeatsInOrderWithinTheOpening() {
        val times = OPENING_CUES.map { it.atMs }
        assertEquals(times.sorted(), times)
        assertTrue(times.all { it in 0 until OPENING_END_MS })
        assertEquals("Lily's chime as she appears", LILY_PEEK_MS, times.first())
        assertTrue(OPENING_CUES.any { it.atMs == START_POP_MS } && OPENING_CUES.any { it.atMs == SPARKLE_MS })
    }

    @Test fun sounds_aNoteForEachLetterAsItLands_goingUp() {
        val notes = OPENING_CUES.filter { it.sfx == SfxEvent.CORRECT_CHIME }
        assertEquals(TITLE.length, notes.size)
        notes.forEachIndexed { i, n -> assertEquals(TITLE_MS + i * TITLE_STAGGER_MS + LETTER_MS / 2, n.atMs) }
        notes.zipWithNext().forEach { (a, b) -> assertTrue(b.rate > a.rate) }
    }

    @Test fun sounds_areCheerfulQuietAndPlayable() {
        val gloomy = setOf(SfxEvent.INCORRECT_POP, SfxEvent.BLENDIT_BUZZ, SfxEvent.HEART_LOSS_WHOOSH)
        (OPENING_CUES + STILL_OPENING_CUE).forEach { cue ->
            assertFalse("${cue.sfx} means a wrong answer", cue.sfx in gloomy)
            assertTrue("well under Lily's voice", cue.volume in 0.1f..0.5f)
            assertTrue("SoundPool plays rates 0.5 to 2", cue.rate in 0.5f..2f)
        }
        assertEquals("with reduced motion, one chime at once", 0, STILL_OPENING_CUE.atMs)
    }

    /** A frame clock that hands out the given frame times, in ms, and counts the frames used. */
    private class ScriptedClock(ms: List<Long>) : MonotonicFrameClock {
        private val times = ms.iterator()
        var used = 0
        override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
            used++
            return onFrame(times.next() * 1_000_000L)
        }
    }

    @Test fun start_waitsOutTheSlowFirstFrames() {
        // Three slow frames after the first, then smooth 16 ms frames.
        val clock = ScriptedClock(listOf(0L, 120L, 260L, 400L) + (1..20).map { 400L + 16L * it })
        runBlocking(clock) { awaitSmoothFrames() }
        assertEquals("the first frame, 3 slow ones and $SMOOTH_FRAMES smooth ones", 4 + SMOOTH_FRAMES, clock.used)
    }

    @Test fun start_givesUpWaitingOnAPhoneThatStaysSlow() {
        val clock = ScriptedClock((0..40).map { it * 100L })
        runBlocking(clock) { awaitSmoothFrames() }
        assertEquals("starts by ${OPENING_MAX_WAIT_MS} ms", (OPENING_MAX_WAIT_MS / 100L + 1).toInt(), clock.used)
    }
}
