package com.playit.app.presentation.soundcheck

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playit.app.domain.model.SoundCheckResult
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.components.LessonStep
import com.playit.app.presentation.components.LessonTopBar
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.resetsIdle
import com.playit.app.presentation.theme.ButtonShape
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.EmeraldLeafShadow
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GentleCorrectionOrangeShadow
import com.playit.app.presentation.theme.GroundColors
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalPlayItDimens
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.PillShape
import com.playit.app.presentation.theme.PrimaryJoy
import com.playit.app.presentation.theme.PrimaryJoyDark
import com.playit.app.presentation.theme.PrimaryJoyShadow
import com.playit.app.presentation.theme.Squircle16
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldShadow
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.SurfaceCardShadow
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

/**
 * The sound check before a chapter's word game (user choice, 2026-11-06). While asking: "Sound n of N", a speaker
 * button that plays the sound again, and the chapter's letters as tiles. At the end: the sounds the child knew, the
 * ones to practise, and "Build words", which opens Blend It.
 */
@Composable
fun SoundCheckScreen(
    viewModel: SoundCheckViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val tiles by viewModel.tiles.collectAsStateWithLifecycle()
    val index by viewModel.index.collectAsStateWithLifecycle()
    val total by viewModel.total.collectAsStateWithLifecycle()
    val speech by viewModel.speech.collectAsStateWithLifecycle()
    val lastTap by viewModel.lastTap.collectAsStateWithLifecycle()
    val accepting by viewModel.acceptingTaps.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val skip by viewModel.skip.collectAsStateWithLifecycle()

    LaunchedEffect(skip) { if (skip) onDone() }
    DisposableEffect(Unit) {
        viewModel.onScreenVisible()
        onDispose {
            viewModel.onScreenHidden()
            viewModel.stopAudio()
        }
    }

    SoundCheckContent(
        tiles = tiles,
        index = index,
        total = total,
        speech = speech,
        lastTap = lastTap,
        accepting = accepting,
        isPlaying = isPlaying,
        result = result,
        onTap = viewModel::tap,
        onReplay = viewModel::replaySound,
        onInteraction = viewModel::onUserInteraction,
        onDone = {
            viewModel.stopAudio()
            onDone()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SoundCheckContent(
    tiles: List<String>,
    index: Int,
    total: Int,
    speech: SoundCheckSpeech,
    lastTap: SoundCheckTap?,
    accepting: Boolean,
    isPlaying: Boolean,
    result: SoundCheckResult?,
    onTap: (String) -> Unit,
    onReplay: () -> Unit,
    onInteraction: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val d = LocalPlayItDimens.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .resetsIdle(onInteraction)
            .background(brush = Brush.verticalGradient(colors = GroundColors))
    ) {
        LessonScaffold(
            topBar = { LessonTopBar(currentStep = LessonStep.BLEND_IT, onBack = onBack) },
            header = {
                MascotSpeechHeader(
                    message = speech.text,
                    mascotState = when (speech) {
                        SoundCheckSpeech.RIGHT, SoundCheckSpeech.DONE -> MascotState.CELEBRATING
                        SoundCheckSpeech.GOOD_TRY -> MascotState.ENCOURAGING
                        else -> MascotState.POINTING
                    },
                    isPlayingAudio = isPlaying,
                    onMascotTap = onReplay
                )
            },
            bottomBar = {
                if (result != null) {
                    GummyContainer(
                        onClick = onDone,
                        faceColor = SunnyGold,
                        shadowColor = SunnyGoldShadow,
                        shape = ButtonShape,
                        strokeWidth = 2.5.dp,
                        strokeColor = ModernBorder,
                        depthHeight = 6.dp,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp).testTag("soundcheck_build_words")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = TextMidnight, modifier = Modifier.size(30.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Build words",
                                fontFamily = LexendFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextMidnight
                            )
                        }
                    }
                }
            }
        ) {
            val done = result
            if (done == null) {
                Text(
                    text = "Sound ${(index + 1).coerceAtMost(total.coerceAtLeast(1))} of ${total.coerceAtLeast(1)}",
                    fontFamily = LexendFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryJoyDark,
                    modifier = Modifier
                        .background(color = PrimaryJoy.copy(alpha = 0.12f), shape = PillShape)
                        .border(width = 1.5.dp, color = PrimaryJoy.copy(alpha = 0.35f), shape = PillShape)
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                )
                GummyContainer(
                    onClick = onReplay,
                    faceColor = PrimaryJoy,
                    shadowColor = PrimaryJoyShadow,
                    shape = Squircle16,
                    strokeWidth = 2.dp,
                    strokeColor = ModernBorder,
                    depthHeight = 5.dp,
                    modifier = Modifier.size(88.dp).semantics { contentDescription = "Hear the sound again" }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("soundcheck_tiles")
                ) {
                    tiles.forEach { letter ->
                        val tapped = lastTap?.takeIf { it.letter == letter }
                        GummyContainer(
                            onClick = { onTap(letter) },
                            enabled = accepting,
                            faceColor = when (tapped?.right) {
                                true -> EmeraldLeaf
                                false -> GentleCorrectionOrange
                                null -> SunnyGold
                            },
                            shadowColor = when (tapped?.right) {
                                true -> EmeraldLeafShadow
                                false -> GentleCorrectionOrangeShadow
                                null -> SunnyGoldShadow
                            },
                            shape = Squircle16,
                            strokeWidth = 2.dp,
                            strokeColor = ModernBorder,
                            depthHeight = 5.dp,
                            modifier = Modifier.size(d.tileSize + 16.dp).testTag("soundcheck_tile_$letter")
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = letter.uppercase(),
                                    fontFamily = LexendFontFamily,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (tapped?.right == true) Color.White else TextMidnight
                                )
                            }
                        }
                    }
                }
            } else {
                ResultLetters("You know these sounds!", done.known, known = true)
                if (done.notYet.isNotEmpty()) ResultLetters("Let's practise these again:", done.notYet, known = false)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultLetters(title: String, letters: List<String>, known: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            fontFamily = LexendFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = if (known) TextMidnight else TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
        Spacer(Modifier.size(8.dp))
        if (letters.isEmpty()) {
            Text("Let's keep listening!", fontFamily = LexendFontFamily, fontSize = 18.sp, color = TextMuted)
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            letters.forEach { letter ->
                GummyContainer(
                    faceColor = if (known) EmeraldLeaf else SurfaceCard,
                    shadowColor = if (known) EmeraldLeafShadow else SurfaceCardShadow,
                    shape = Squircle16,
                    strokeWidth = 2.dp,
                    strokeColor = ModernBorder,
                    depthHeight = 4.dp,
                    modifier = Modifier.size(72.dp).testTag(if (known) "soundcheck_known_$letter" else "soundcheck_practise_$letter")
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            letter.uppercase(),
                            fontFamily = LexendFontFamily,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (known) Color.White else TextMidnight
                        )
                        if (known) {
                            Icon(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
