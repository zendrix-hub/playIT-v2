package com.playit.app.navigation

import com.playit.app.presentation.theme.PlayItMotion
import com.playit.app.presentation.theme.LocalReducedMotion
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.playit.app.presentation.blendit.BlendItCompleteScreen
import com.playit.app.presentation.blendit.BlendItCompleteViewModel
import com.playit.app.presentation.blendit.BlendItScreen
import com.playit.app.presentation.blendit.BlendItViewModel
import com.playit.app.presentation.dashboard.ParentDashboardScreen
import com.playit.app.presentation.dashboard.ParentDashboardViewModel
import com.playit.app.presentation.dashboard.ReportPreviewScreen
import com.playit.app.presentation.findit.FindItScreen
import com.playit.app.presentation.findit.FindItViewModel
import com.playit.app.presentation.hearit.HearItScreen
import com.playit.app.presentation.hearit.HearItViewModel
import com.playit.app.presentation.lettercomplete.LetterCompleteScreen
import com.playit.app.presentation.lettercomplete.LetterCompleteViewModel
import com.playit.app.presentation.map.MapScreen
import com.playit.app.presentation.map.MapViewModel
import com.playit.app.presentation.profile.NamePromptScreen
import com.playit.app.presentation.profile.ProfileSelectScreen
import com.playit.app.presentation.profile.ProfileViewModel
import com.playit.app.presentation.sayit.SayItScreen
import com.playit.app.presentation.sayit.SayItViewModel
import com.playit.app.presentation.splash.SplashScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    // Screen change: a fade plus a slight upward move (21_ANIMATION_GUIDE); none under reduced motion.
    val reduced = LocalReducedMotion.current
    val enter: EnterTransition = if (reduced) EnterTransition.None else
        fadeIn(tween(PlayItMotion.SCREEN_MS)) + slideInVertically(tween(PlayItMotion.SCREEN_MS)) { it / 40 }
    val exit: ExitTransition = if (reduced) ExitTransition.None else fadeOut(tween(PlayItMotion.SCREEN_MS))

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { enter },
        exitTransition = { exit },
        popEnterTransition = { enter },
        popExitTransition = { exit }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onStartClick = {
                    navController.navigate(Routes.PROFILE_SELECT) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE_SELECT) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileSelectScreen(
                viewModel = viewModel,
                onProfileSelected = { _ ->
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.PROFILE_SELECT) { inclusive = true }
                    }
                },
                onAddProfileClick = {
                    navController.navigate(Routes.NAME_PROMPT)
                },
                onParentDashboardClick = {
                    navController.navigate(Routes.PARENT_DASHBOARD)
                }
            )
        }

        composable(Routes.NAME_PROMPT) {
            val viewModel: ProfileViewModel = hiltViewModel()
            NamePromptScreen(
                viewModel = viewModel,
                onProfileCreated = { _ ->
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.PROFILE_SELECT) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MAP) {
            val viewModel: MapViewModel = hiltViewModel()
            MapScreen(
                viewModel = viewModel,
                onNodeSelected = { nodeId ->
                    if (nodeId.startsWith("blend_")) {
                        val groupId = nodeId.removePrefix("blend_")
                        navController.navigate(Routes.blendIt(groupId))
                    } else {
                        navController.navigate(Routes.hearIt(nodeId))
                    }
                },
                onBack = {
                    viewModel.clearSession()
                    navController.navigate(Routes.PROFILE_SELECT) {
                        popUpTo(Routes.MAP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HEAR_IT) {
            val viewModel: HearItViewModel = hiltViewModel()
            HearItScreen(
                viewModel = viewModel,
                onNext = { phonemeId ->
                    navController.navigate(Routes.sayIt(phonemeId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SAY_IT) {
            val viewModel: SayItViewModel = hiltViewModel()
            SayItScreen(
                viewModel = viewModel,
                onNext = { phonemeId ->
                    navController.navigate(Routes.findIt(phonemeId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.FIND_IT) {
            val viewModel: FindItViewModel = hiltViewModel()
            FindItScreen(
                viewModel = viewModel,
                onNext = { phonemeId, heartsLost ->
                    navController.navigate(Routes.letterComplete(phonemeId, heartsLost))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.LETTER_COMPLETE,
            arguments = listOf(
                navArgument("heartsLost") {
                    type = NavType.StringType
                    defaultValue = "0"
                }
            )
        ) {
            val viewModel: LetterCompleteViewModel = hiltViewModel()
            LetterCompleteScreen(
                viewModel = viewModel,
                onReturnToMap = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.MAP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.BLEND_IT) {
            val viewModel: BlendItViewModel = hiltViewModel()
            BlendItScreen(
                viewModel = viewModel,
                onSessionComplete = { result ->
                    navController.navigate(
                        Routes.blendItComplete(
                            result.groupId.toString(),
                            result.heartsLost,
                            result.wordsCorrect,
                            result.totalWords
                        )
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.BLEND_IT_COMPLETE,
            arguments = listOf(
                navArgument("heartsLost") {
                    type = NavType.StringType
                    defaultValue = "0"
                },
                navArgument("wordsCorrect") {
                    type = NavType.StringType
                    defaultValue = "5"
                },
                navArgument("totalWords") {
                    type = NavType.StringType
                    defaultValue = "5"
                }
            )
        ) {
            val viewModel: BlendItCompleteViewModel = hiltViewModel()
            BlendItCompleteScreen(
                viewModel = viewModel,
                onReturnToMap = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.MAP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PARENT_DASHBOARD) {
            val viewModel: ParentDashboardViewModel = hiltViewModel()
            ParentDashboardScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onReportPreview = { file ->
                    navController.navigate(Routes.reportPreview(file.absolutePath))
                },
                onAllProfilesDeleted = {
                    navController.navigate(Routes.PROFILE_SELECT) { popUpTo(0) }
                }
            )
        }

        composable(Routes.REPORT_PREVIEW) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: ""
            ReportPreviewScreen(
                pdfFilePath = filePath,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
