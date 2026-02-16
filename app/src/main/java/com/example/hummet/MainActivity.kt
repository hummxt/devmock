package com.example.hummet

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
import com.example.hummet.ui.components.BottomBar
import com.example.hummet.ui.screens.home.Homepage
import com.example.hummet.ui.screens.quiz.QuestionsScreen
import com.example.hummet.ui.screens.interview.InterviewScreen
import com.example.hummet.ui.screens.profile.ProfileScreen
import com.example.hummet.ui.screens.settings.SettingsScreen
import com.example.hummet.ui.screens.auth.LoginScreen
import com.example.hummet.ui.screens.auth.RegisterScreen
import com.example.hummet.ui.screens.auth.OnboardingScreen
import com.example.hummet.ui.theme.HummetTheme
import com.example.hummet.ui.theme.ThemeConfig
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            val auth = remember { FirebaseAuth.getInstance() }
            var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

            LaunchedEffect(Unit) {
                ThemeConfig.isDarkMode = systemInDarkTheme
                
                auth.addAuthStateListener { firebaseAuth ->
                    isLoggedIn = firebaseAuth.currentUser != null
                }
            }

            HummetTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val showBottomBar = currentRoute in listOf("home", "questions", "interview", "profile")
                        if (showBottomBar && isLoggedIn) {
                            BottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "home" else "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { 
                                    isLoggedIn = true
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    isLoggedIn = true
                                    navController.navigate("onboarding") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            Homepage(
                                onNavigateToInterview = { navController.navigate("interview") },
                                onNavigateToQuestions = { navController.navigate("questions") }
                            )
                        }
                        composable("questions") {
                            val repository = remember { com.example.hummet.data.repository.QuestionsRepository() }
                            val viewModel = remember { com.example.hummet.ui.screens.quiz.QuestionsViewModel(repository) }
                            QuestionsScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("interview") {
                            InterviewScreen(
                                onStartInterview = { topic, count, difficulty ->
                                    navController.navigate("live_interview/$topic/$count/$difficulty")
                                },
                                onNavigateToDetail = { topicId ->
                                    navController.navigate("interview_detail/$topicId")
                                }
                            )
                        }
                        composable(
                            "interview_detail/{topicId}",
                            arguments = listOf(
                                androidx.navigation.navArgument("topicId") { type = androidx.navigation.NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                            val repository = remember { com.example.hummet.data.repository.InterviewRepository(
                                com.example.hummet.data.remote.RetrofitClient.apiService
                            ) }
                            val viewModel = remember(topicId) {
                                com.example.hummet.ui.screens.interview.detail.InterviewDetailViewModel(
                                    repository, topicId
                                )
                            }
                            
                            com.example.hummet.ui.screens.interview.detail.InterviewDetailScreen(
                                topicId = topicId,
                                onBackClick = { navController.popBackStack() },
                                onStartInterview = { 
                                    val currentState = viewModel.state.value
                                    if (currentState is com.example.hummet.ui.screens.interview.detail.InterviewDetailState.Success) {
                                        val topic = currentState.detail.topic
                                        navController.navigate("live_interview/${topic.title}/${topic.questionsCount}/${topic.difficulty}")
                                    }
                                },
                                viewModel = viewModel
                            )
                        }
                        composable(
                            "live_interview/{topic}/{count}/{difficulty}",
                            arguments = listOf(
                                androidx.navigation.navArgument("topic") { type = androidx.navigation.NavType.StringType },
                                androidx.navigation.navArgument("count") { type = androidx.navigation.NavType.IntType },
                                androidx.navigation.navArgument("difficulty") { type = androidx.navigation.NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val topic = backStackEntry.arguments?.getString("topic") ?: ""
                            val count = backStackEntry.arguments?.getInt("count") ?: 10
                            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Middle"
                            
                            val repository = remember { com.example.hummet.data.repository.AIRepository() }
                            val viewModel = remember(topic, count, difficulty) {
                                com.example.hummet.ui.screens.interview.live.LiveInterviewViewModel(
                                    repository, topic, count, difficulty
                                )
                            }
                            
                            com.example.hummet.ui.screens.interview.live.LiveInterviewScreen(
                                onBackClick = { navController.popBackStack() },
                                viewModel = viewModel
                            )
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