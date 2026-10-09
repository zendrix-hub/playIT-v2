package com.playit.app.presentation.sayit

import org.junit.Assert.assertEquals
import org.junit.Test

class MicStatusTest {
    @Test fun idle() = assertEquals(MicStatus.IDLE, micStatusFor(SayItState.Idle, heardSpeech = false))
    @Test fun listeningSilent() = assertEquals(MicStatus.LISTENING, micStatusFor(SayItState.Listening, false))
    @Test fun listeningHeard() = assertEquals(MicStatus.HEARD, micStatusFor(SayItState.Listening, true))
    @Test fun correct() = assertEquals(MicStatus.RESULT_CORRECT, micStatusFor(SayItState.Correct("mouse"), false))
    @Test fun incorrect() = assertEquals(MicStatus.RESULT_TRY_AGAIN, micStatusFor(SayItState.Incorrect("em"), true))
}
