package com.playit.app.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val IDLE_REPROMPT_MS = 10_000L   // user decision 2026-10-01 (spec Table 2 [proposed] 10 s)
const val IDLE_MAX_PROMPTS = 3         // then stay quiet until the child touches the screen
const val IDLE_MAX_POSTPONES = 6       // busy (audio playing) postponements before giving up

class IdleTimer(
    private val scope: CoroutineScope,
    private val timeoutMs: Long = IDLE_REPROMPT_MS,
    private val maxPrompts: Int = IDLE_MAX_PROMPTS,
    private val maxPostpones: Int = IDLE_MAX_POSTPONES,
    private val isBusy: () -> Boolean = { false },
    private val onIdle: () -> Unit
) {
    private var job: Job? = null
    private var promptCount = 0
    private var postponeCount = 0

    fun start() {
        job?.cancel()
        promptCount = 0
        postponeCount = 0
        scheduleNext()
    }

    fun touch() {
        start()
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private fun scheduleNext() {
        job?.cancel()
        job = scope.launch {
            delay(timeoutMs)
            if (isBusy()) {
                postponeCount++
                if (postponeCount < maxPostpones) {
                    scheduleNext()
                } else {
                    job = null
                }
            } else {
                onIdle()
                promptCount++
                postponeCount = 0
                if (promptCount < maxPrompts) {
                    scheduleNext()
                } else {
                    job = null
                }
            }
        }
    }
}

/** Calls onInteraction for every pointer event in this subtree, without consuming it. */
fun Modifier.resetsIdle(onInteraction: () -> Unit): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent(PointerEventPass.Initial)
            onInteraction()
        }
    }
}
