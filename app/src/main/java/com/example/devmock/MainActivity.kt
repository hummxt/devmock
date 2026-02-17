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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devmock.ui.screens.quiz.QuestionsViewModel

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
                delay(2500)
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
                        "home", "questions", "interview", "profile", "settings" -> true
                        else -> false
                    }

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                BottomBar(
                                    navController = navController
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = if (isLoggedIn) "home" else "login",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("onboarding") {
                                OnboardingScreen(
                                    onComplete = { 
                                        navController.navigate("home") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    }
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
                                        navController.navigate("onboarding") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    },
                                    onNavigateToLogin = { navController.popBackStack() }
                                )
                            }
                            composable("home") {
                                Homepage(
                                    onNavigateToInterview = { navController.navigate("interview") },
                                    onNavigateToQuestions = { navController.navigate("questions") }
                                )
                            }
                            composable("questions") {
                                val context = LocalContext.current
                                val repository = remember { com.example.devmock.data.repository.LocalQuestionsRepository(context) }
                                val qViewModel: QuestionsViewModel = viewModel(
                                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                            return QuestionsViewModel(repository) as T
                                        }
                                    }
                                )
                                QuestionsScreen(
                                    navController = navController,
                                    viewModel = qViewModel
                                )
                            }
                            composable("interview") {
                                InterviewScreen(
                                    onStartInterview = { topic, count, difficulty ->
                                        navController.navigate("live_interview/$topic/$count/$difficulty")
                                    },
                                    onNavigateToDetail = { topicId ->
                                    }
                                )
                            }
                            composable("live_interview/{topic}/{count}/{difficulty}") { backStackEntry ->
                                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                                val count = backStackEntry.arguments?.getString("count")?.toIntOrNull() ?: 5
                                val userName = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
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
                                
                                var topic by remember { mutableStateOf<com.example.devmock.ui.screens.quiz.TopicGroup?>(null) }
                                
                                LaunchedEffect(topicId) {
                                    topic = repository.getTopicById(topicId)
                                }

                                topic?.let { currentTopic ->
                                    val viewModel = remember(currentTopic) {
                                        com.example.devmock.ui.screens.interview.live.LocalInterviewViewModel(currentTopic.fullQuestions)
                                    }

                                    com.example.devmock.ui.screens.interview.live.LiveInterviewScreen(
                                        onBackClick = { navController.popBackStack() },
                                        viewModel = viewModel
                                    )
                                }
                            }
                            composable("profile") {
                                ProfileScreen(
                                    onNavigateToSettings = { navController.navigate("settings") }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}