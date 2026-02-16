package com.example.hummet.ui.screens.interview.live

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hummet.ui.theme.isAppInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveInterviewScreen(
    onBackClick: () -> Unit,
    viewModel: LiveInterviewViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val isDark = isAppInDarkTheme()
    
    val primaryTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (state.isCompleted) "Interview Result" else "AI Interview", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, "Exit")
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
                        onNext = viewModel::nextQuestion,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
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
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("AI is crafting your interview...", fontWeight = FontWeight.Medium)
        Text("Using Groq Llama 3.3 70B", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        Text("Preparing relevant questions and answers", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ErrorState(message: String, onBack: () -> Unit) {
    val isDark = isAppInDarkTheme()
    

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
            color = if (isDark) Color.White else Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        

        Text(
            text = errorMessage, 
            textAlign = TextAlign.Center, 
            color = if (isDark) Color.LightGray else Color.Gray,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
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
                    color = if (isDark) Color.White else Color.Black,
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
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Go Back", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun InterviewContent(
    state: LiveInterviewUiState,
    onOptionSelect: (Int) -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    val currentQuestion = state.questions[state.currentQuestionIndex]
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
            color = primaryTextColor
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            currentQuestion.options.forEachIndexed { index, option ->
                OptionCard(
                    text = option,
                    isSelected = state.selectedOptionIndex == index,
                    isCorrect = index == currentQuestion.correctAnswerIndex,
                    isRevealed = state.isAnswerRevealed,
                    onClick = { onOptionSelect(index) },
                    primaryTextColor = primaryTextColor
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        

        if (state.isAnswerRevealed) {
            ExplanationCard(currentQuestion.explanation)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (state.currentQuestionIndex == state.questions.size - 1) "See Results" else "Next Question")
            }
        } else {
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state.selectedOptionIndex != null,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm Answer")
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
    onClick: () -> Unit,
    primaryTextColor: Color
) {
    val isDark = isAppInDarkTheme()
    val baseColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
    
    val targetColor = when {
        isRevealed && isCorrect -> Color(0xFF10B981) // Green
        isRevealed && isSelected && !isCorrect -> Color(0xFFEF4444) // Red
        isSelected -> MaterialTheme.colorScheme.primary
        else -> baseColor
    }
    
    val contentColor = if (isSelected || (isRevealed && (isCorrect || (isSelected && !isCorrect)))) Color.White else primaryTextColor

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRevealed) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = targetColor,
        border = if (!isSelected && !isRevealed) androidx.compose.foundation.BorderStroke(1.dp, primaryTextColor.copy(alpha = 0.1f)) else null
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
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ExplanationCard(explanation: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Outlined.Lightbulb, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(explanation, fontSize = 13.sp)
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
        val percentage = (state.score.toFloat() / state.questions.size * 100).toInt()
        
        Text("Interview Finished!", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier.size(160.dp),
                strokeWidth = 12.dp,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$percentage%", fontWeight = FontWeight.Bold, fontSize = 32.sp)
                Text("${state.score}/${state.questions.size}", color = Color.Gray)
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
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Done")
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
