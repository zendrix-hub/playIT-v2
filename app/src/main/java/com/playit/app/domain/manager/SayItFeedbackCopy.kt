package com.playit.app.domain.manager

import com.playit.app.domain.model.SpeechErrorType

data class FeedbackCopy(val mascot: String, val banner: String)

object SayItFeedbackCopy {
    /** Text for the result of one attempt. action = TutorPolicy's decision for it. */
    fun forResult(
        action: TutorAction,
        errorType: SpeechErrorType,
        wordMode: Boolean,
        word: String?
    ): FeedbackCopy {
        return when (action) {
            is TutorAction.Praise -> FeedbackCopy(
                mascot = "Yes! You said it!",
                banner = "Great listening!"
            )
            // Third miss: warm, and it points forward; Lily's voice says "Nice try! We'll practice
            // this one again later." (fb_try_later) (user decision 2026-10-10, card 28).
            is TutorAction.LeadAndMoveOn -> FeedbackCopy(
                mascot = "Let's say it together. We'll practice later.",
                banner = "Nice try! Let's keep going!"
            )
            is TutorAction.Correct -> {
                if (action.supportLevel == 2) {
                    FeedbackCopy(
                        mascot = "Watch my lips, then your turn.",
                        banner = "Watch my lips"
                    )
                } else {
                    val effectiveErrorType = when {
                        action.errorType != SpeechErrorType.NONE -> action.errorType
                        errorType != SpeechErrorType.NONE -> errorType
                        else -> SpeechErrorType.NONE
                    }
                    when (effectiveErrorType) {
                        SpeechErrorType.LETTER_NAME -> FeedbackCopy(
                            mascot = "That's the letter's name. Listen for its sound.",
                            banner = "Its sound, not its name"
                        )
                        SpeechErrorType.ADDED_VOWEL -> FeedbackCopy(
                            mascot = "Almost! Just the sound, no 'ah'.",
                            banner = "Just the sound"
                        )
                        SpeechErrorType.SUBSTITUTION -> FeedbackCopy(
                            mascot = "Listen closely to the sound.",
                            banner = "Listen closely"
                        )
                        SpeechErrorType.NO_SPEECH -> {
                            val w = if (wordMode && !word.isNullOrEmpty()) {
                                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            } else {
                                "it"
                            }
                            FeedbackCopy(
                                mascot = "I didn't hear you. Say $w!",
                                banner = "Say it out loud"
                            )
                        }
                        else -> FeedbackCopy(
                            mascot = "Good try! Listen again.",
                            banner = "Listen again"
                        )
                    }
                }
            }
        }
    }
}
