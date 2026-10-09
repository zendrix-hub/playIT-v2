package com.playit.app.presentation.sayit

/** What the Say It mic shows: idle, listening, heard speech, or the result of the attempt. */
enum class MicStatus { IDLE, LISTENING, HEARD, RESULT_CORRECT, RESULT_TRY_AGAIN }

fun micStatusFor(state: SayItState, heardSpeech: Boolean): MicStatus = when (state) {
    SayItState.Idle -> MicStatus.IDLE
    SayItState.Listening -> if (heardSpeech) MicStatus.HEARD else MicStatus.LISTENING
    is SayItState.Correct -> MicStatus.RESULT_CORRECT
    is SayItState.Incorrect -> MicStatus.RESULT_TRY_AGAIN
}
