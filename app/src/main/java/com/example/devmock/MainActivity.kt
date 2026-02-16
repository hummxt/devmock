package com.example.devmock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.devmock.ui.components.BottomBar
import com.example.devmock.ui.screens.home.Homepage
import com.example.devmock.ui.screens.quiz.QuestionsScreen
import com.example.devmock.ui.screens.interview.InterviewScreen
import com.example.devmock.ui.screens.profile.ProfileScreen
import com.example.devmock.ui.screens.settings.SettingsScreen
import com.example.devmock.ui.screens.auth.LoginScreen
import com.example.devmock.ui.screens.auth.RegisterScreen
import com.example.devmock.ui.screens.auth.OnboardingScreen
import com.example.devmock.ui.theme.DevmockTheme
import com.example.devmock.ui.theme.ThemeConfig
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.example.devmock.ui.screens.splash.SplashScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            val auth = remember { FirebaseAuth.getInstance() }
            var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(2500) // Show splash for 2.5 seconds
                showSplash = false
            }

            LaunchedEffect(systemInDarkTheme) {
                ThemeConfig.isDarkMode = systemInDarkTheme
            }

            DevmockTheme {
                if (showSplash) {
                    SplashScreen()
                } else {
                    val navController = rememberNavController()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStackEntry?.destination?.route

                    val showBottomBar = when (currentRoute) {
                        "home", "quiz", "interview", "profile", "settings" -> true
                        else -> false
                    }

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                BottomBar(
                                    navController = navController,
                                    currentRoute = currentRoute ?: "home"
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = if (isLoggedIn) "home" else "onboarding",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("onboarding") {
                                OnboardingScreen(
                                    onGetStarted = { navController.navigate("login") }
                                )
                            }
                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = {
                                        isLoggedIn = true
                                        navController.navigate("home") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = { navController.navigate("register") }
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    onRegisterSuccess = {
                                        isLoggedIn = true
                                        navController.navigate("home") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    },
                                    onNavigateToLogin = { navController.popBackStack() }
                                )
                            }
                            composable("home") {
                                Homepage(
                                    onNavigateToQuiz = { navController.navigate("quiz") },
                                    onNavigateToInterview = { navController.navigate("interview") }
                                )
                            }
                            composable("quiz") {
                                QuestionsScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onStartInterview = { topicId ->
                                        navController.navigate("library_interview/$topicId")
                                    }
                                )
                            }
                            composable("interview") {
                                InterviewScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onStartInterview = { topic, count, difficulty ->
                                        navController.navigate("live_interview/$topic/$count/$difficulty")
                                    }
                                )
                            }
                            composable("live_interview/{topic}/{count}/{difficulty}") { backStackEntry ->
                                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                                val count = backStackEntry.arguments?.getString("count")?.toIntOrNull() ?: 5
                                val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Medium"

                                val viewModel = remember {
                                    com.example.devmock.ui.screens.interview.live.LiveInterviewViewModel(
                                        repository = com.example.devmock.data.repository.AIRepository(),
                                        topic = topic,
                                        questionCount = count,
                                        difficulty = difficulty
                                    )
                                }

                                com.example.devmock.ui.screens.interview.live.LiveInterviewScreen(
                                    onBackClick = { navController.popBackStack() },
                                    viewModel = viewModel
                                )
                            }
                            composable("library_interview/{topicId}") { backStackEntry ->
                                val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                                val repository = remember { com.example.devmock.data.repository.LocalQuestionsRepository(this@MainActivity) }
                                val questions = remember { repository.getQuestionsByTopicId(topicId) }

                                val viewModel = remember {
                                    com.example.devmock.ui.screens.interview.live.LocalInterviewViewModel(questions)
                                }

                                com.example.devmock.ui.screens.interview.live.LiveInterviewScreen(
                                    onBackClick = { navController.popBackStack() },
                                    viewModel = viewModel
                                )
                            }
                            composable("profile") {
                                ProfileScreen(
                                    onLogout = {
                                        auth.signOut()
                                        isLoggedIn = false
                                        navController.navigate("onboarding") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("settings") {
                                SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}