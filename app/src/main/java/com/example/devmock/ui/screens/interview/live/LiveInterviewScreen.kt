package com.example.devmock.ui.screens.interview.live

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveInterviewScreen(
    onBackClick: () -> Unit,
    viewModel: InterviewViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = { 
                    Text(
                        if (state.isCompleted) "Interview Result" else state.title, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, "Exit", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    if (!state.isCompleted && state.questions.isNotEmpty()) {
                        Text(
                            "${state.currentQuestionIndex + 1}/${state.questions.size}",
                            modifier = Modifier.padding(end = 16.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, onBackClick)
                state.isCompleted -> CompletionState(state, onBackClick)
                state.questions.isNotEmpty() -> {
                    InterviewContent(
                        state = state,
                        onOptionSelect = viewModel::selectOption,
                        onConfirm = viewModel::confirmAnswer,
                        onNext = viewModel::nextQuestion
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "AI is crafting your interview...", 
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Preparing relevant questions and answers", 
            fontSize = 12.sp, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorState(message: String, onBack: () -> Unit) {
    val parts = message.split("\n\n")
    val errorTitle = parts.getOrNull(0) ?: "Something went wrong"
    val errorMessage = parts.getOrNull(1) ?: message
    val errorSuggestion = parts.getOrNull(2)?.removePrefix("💡 ") ?: "Please try again later."
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ErrorOutline, 
                contentDescription = null, 
                modifier = Modifier.size(48.dp), 
                tint = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = errorTitle, 
            fontWeight = FontWeight.Bold, 
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = errorMessage, 
            textAlign = TextAlign.Center, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = errorSuggestion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Go Back", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun InterviewContent(
    state: LiveInterviewUiState,
    onOptionSelect: (Int) -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit
) {
    val currentQuestion = state.questions[state.currentQuestionIndex]
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
                .padding(bottom = 120.dp) // Extra padding for fixed button
        ) {
            LinearProgressIndicator(
                progress = { (state.currentQuestionIndex + 1).toFloat() / state.questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                color = getDifficultyColor(currentQuestion.difficulty).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = currentQuestion.difficulty.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = getDifficultyColor(currentQuestion.difficulty)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    OptionCard(
                        text = option,
                        isSelected = state.selectedOptionIndex == index,
                        isCorrect = index == currentQuestion.correctAnswerIndex,
                        isRevealed = state.isAnswerRevealed,
                        onClick = { onOptionSelect(index) }
                    )
                }
            }
            
            if (state.isAnswerRevealed) {
                Spacer(modifier = Modifier.height(24.dp))
                ExplanationCard(currentQuestion.explanation)
            }
        }

        // Fixed bottom button area with background that matches app theme
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 16.dp
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                if (state.isAnswerRevealed) {
                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            if (state.currentQuestionIndex == state.questions.size - 1) "See Results" else "Next Question",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = state.selectedOptionIndex != null,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Confirm Answer", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isRevealed: Boolean,
    onClick: () -> Unit
) {
    val targetColor = when {
        isRevealed && isCorrect -> Color(0xFF10B981)
        isRevealed && isSelected && !isCorrect -> Color(0xFFEF4444)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = if (isSelected || (isRevealed && (isCorrect || (isSelected && !isCorrect)))) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRevealed) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = targetColor,
        border = if (!isSelected && !isRevealed) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) else null
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            
            if (isRevealed) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ExplanationCard(explanation: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Outlined.Lightbulb, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(explanation, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun CompletionState(state: LiveInterviewUiState, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val percentage = if (state.questions.isEmpty()) 0 else (state.score.toFloat() / state.questions.size * 100).toInt()
        
        Text("Interview Finished!", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier.size(160.dp),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$percentage%", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
                Text("${state.score}/${state.questions.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = when {
                percentage >= 80 -> "Excellent! You're ready for the real thing."
                percentage >= 50 -> "Good job! A bit more practice and you'll be perfect."
                else -> "Keep learning! Practice makes perfect."
            },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Done", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty.lowercase()) {
        "junior", "easy" -> Color(0xFF10B981)
        "middle", "medium" -> Color(0xFFF59E0B)
        "senior", "hard" -> Color(0xFFEF4444)
        else -> Color.Gray
    }
}
