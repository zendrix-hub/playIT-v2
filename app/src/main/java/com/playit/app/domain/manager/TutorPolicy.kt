package com.playit.app.domain.manager

import com.playit.app.domain.model.SpeechErrorType
import com.playit.app.domain.model.SpeechJudgement

sealed interface TutorAction {
    data class Praise(val attempt: Int) : TutorAction
    data class Correct(val errorType: SpeechErrorType, val supportLevel: Int) : TutorAction // 1 = re-model, 2 = slow + lips
    data object LeadAndMoveOn : TutorAction
}

class TutorPolicy(private val maxScoredAttempts: Int = 3) {
    fun next(attempt: Int, judgement: SpeechJudgement): TutorAction = when {
        judgement.isCorrect -> TutorAction.Praise(attempt)
        attempt >= maxScoredAttempts -> TutorAction.LeadAndMoveOn
        else -> TutorAction.Correct(judgement.errorType, supportLevel = attempt)
    }
}
