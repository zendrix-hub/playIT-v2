package com.playit.app.presentation.sayit

/**
 * What the Say It mic shows: idle, listening, heard speech, the result of the attempt, or done.
 * DONE is the resting mic after the third miss (LeadAndMoveOn): it takes no taps, so the child
 * looks at the pulsing Next button instead of tapping a mic that no longer listens (card 28).
 */
enum class MicStatus { IDLE, LISTENING, HEARD, RESULT_CORRECT, RESULT_TRY_AGAIN, DONE }

fun micStatusFor(state: SayItState, heardSpeech: Boolean, movedOn: Boolean = false): MicStatus = when (state) {
    SayItState.Idle -> MicStatus.IDLE
    SayItState.Listening -> if (heardSpeech) MicStatus.HEARD else MicStatus.LISTENING
    is SayItState.Correct -> MicStatus.RESULT_CORRECT
    is SayItState.Incorrect -> if (movedOn) MicStatus.DONE else MicStatus.RESULT_TRY_AGAIN
}
