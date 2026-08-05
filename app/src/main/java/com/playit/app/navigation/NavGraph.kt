package com.playit.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import kotlinx.coroutines.delay
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            LaunchedEffect(Unit) {
                delay(1200)
                navController.navigate(Routes.PROFILE_SELECT) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
            SplashScreen()
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
                onNext = { phonemeId ->
                    navController.navigate(Routes.letterComplete(phonemeId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.LETTER_COMPLETE) {
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
                onSessionComplete = { groupId ->
                    navController.navigate(Routes.blendItComplete(groupId.toString()))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.BLEND_IT_COMPLETE) {
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
