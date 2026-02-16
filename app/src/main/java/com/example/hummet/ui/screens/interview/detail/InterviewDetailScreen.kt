package com.example.hummet.ui.screens.interview.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.hummet.ui.theme.isAppInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hummet.data.model.TopicDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewDetailScreen(
    topicId: String,
    onBackClick: () -> Unit,
    onStartInterview: () -> Unit,
    viewModel: InterviewDetailViewModel
) {
    val state by viewModel.state.collectAsState()

    val isDark = isAppInDarkTheme()
    val primaryTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray
    val containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0)
    val accentColor = if (isDark) Color.White else Color.Black
    val accentTextColor = if (isDark) Color.Black else Color.White

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text("Topic Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val currentState = state) {
                is InterviewDetailState.Loading -> {
                    LoadingContent()
                }
                is InterviewDetailState.Success -> {
                    DetailContent(
                        detail = currentState.detail,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        containerColor = containerColor,
                        accentColor = accentColor,
                        accentTextColor = accentTextColor,
                        onStartInterview = onStartInterview
                    )
                }
                is InterviewDetailState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        textColor = primaryTextColor,
                        onRetry = { viewModel.retryLoad() }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text("Loading topic details...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    textColor: Color,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFEF4444)
            )
            Text(
                "Oops! Something went wrong",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
            Text(
                message,
                textAlign = TextAlign.Center,
                color = textColor.copy(alpha = 0.7f)
            )
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
fun DetailContent(
    detail: TopicDetail,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    containerColor: Color,
    accentColor: Color,
    accentTextColor: Color,
    onStartInterview: () -> Unit
) {
    val topic = detail.topic
    val difficultyColor = when (topic.difficulty) {
        "Junior" -> Color(0xFF10B981)
        "Middle" -> Color(0xFFF59E0B)
        "Senior" -> Color(0xFFEF4444)
        else -> Color.Gray
    }

    val categoryGradient = when (topic.category) {
        "Web" -> listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
        "Mobile" -> listOf(Color(0xFF10B981), Color(0xFF059669))
        "Backend" -> listOf(Color(0xFFEC4899), Color(0xFFDB2777))
        "Architecture" -> listOf(Color(0xFFEC4899), Color(0xFF9333EA))
        else -> listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopicHeaderCard(
                    topic = topic,
                    difficultyColor = difficultyColor,
                    categoryGradient = categoryGradient,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    containerColor = containerColor,
                    completionRate = detail.completionRate
                )
            }

            item {
                StatsRow(
                    topic = topic,
                    containerColor = containerColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }

            item {
                ObjectivesSection(
                    objectives = topic.objectives,
                    containerColor = containerColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }

            item {
                SkillsSection(
                    skills = detail.skillsCovered,
                    containerColor = containerColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }

            item {
                SampleQuestionsSection(
                    questions = detail.sampleQuestions,
                    containerColor = containerColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }

            item {
                PrerequisitesSection(
                    prerequisites = topic.prerequisites,
                    containerColor = containerColor,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }
        }

        BottomActionBar(
            accentColor = accentColor,
            accentTextColor = accentTextColor,
            onStartInterview = onStartInterview
        )
    }
}

@Composable
fun TopicHeaderCard(
    topic: com.example.hummet.data.model.InterviewTopic,
    difficultyColor: Color,
    categoryGradient: List<Color>,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    containerColor: Color,
    completionRate: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Brush.linearGradient(categoryGradient), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Code,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        topic.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = primaryTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge(difficultyColor, topic.difficulty)
                        Text("•", color = secondaryTextColor)
                        Text(topic.category, color = secondaryTextColor, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                topic.description,
                color = secondaryTextColor,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            if (completionRate > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Your Progress", fontSize = 13.sp, color = secondaryTextColor)
                        Text("${(completionRate * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryTextColor)
                    }
                    LinearProgressIndicator(
                        progress = { completionRate },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = difficultyColor,
                        trackColor = difficultyColor.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun Badge(color: Color, text: String) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatsRow(
    topic: com.example.hummet.data.model.InterviewTopic,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.QuestionAnswer,
            value = topic.questionsCount.toString(),
            label = "Questions",
            containerColor = containerColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Schedule,
            value = topic.estimatedTime,
            label = "Duration",
            containerColor = containerColor,
            primaryTextColor = primaryTextColor,
            secondaryTextColor = secondaryTextColor
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = primaryTextColor, modifier = Modifier.size(24.dp))
            Column {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryTextColor)
                Text(label, fontSize = 12.sp, color = secondaryTextColor)
            }
        }
    }
}

@Composable
fun ObjectivesSection(
    objectives: List<String>,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    SectionCard(
        title = "Learning Objectives",
        icon = Icons.Outlined.TrackChanges,
        containerColor = containerColor,
        primaryTextColor = primaryTextColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            objectives.forEach { objective ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF10B981).copy(0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        objective,
                        color = secondaryTextColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SkillsSection(
    skills: List<String>,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    SectionCard(
        title = "Skills Covered",
        icon = Icons.Outlined.Psychology,
        containerColor = containerColor,
        primaryTextColor = primaryTextColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.take((skills.size + 1) / 2).forEach { skill ->
                    SkillChip(skill, secondaryTextColor)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.drop((skills.size + 1) / 2).forEach { skill ->
                    SkillChip(skill, secondaryTextColor)
                }
            }
        }
    }
}

@Composable
fun SkillChip(text: String, textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, textColor.copy(0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 13.sp, color = textColor)
    }
}

@Composable
fun SampleQuestionsSection(
    questions: List<String>,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    SectionCard(
        title = "Sample Questions",
        icon = Icons.Outlined.LiveHelp,
        containerColor = containerColor,
        primaryTextColor = primaryTextColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            questions.forEachIndexed { index, question ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF3B82F6).copy(0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${index + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3B82F6)
                        )
                    }
                    Text(
                        question,
                        color = secondaryTextColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PrerequisitesSection(
    prerequisites: List<String>,
    containerColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    SectionCard(
        title = "Prerequisites",
        icon = Icons.Outlined.School,
        containerColor = containerColor,
        primaryTextColor = primaryTextColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            prerequisites.forEach { prerequisite ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = secondaryTextColor,
                        modifier = Modifier.size(8.dp)
                    )
                    Text(prerequisite, color = secondaryTextColor, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    primaryTextColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(icon, contentDescription = null, tint = primaryTextColor, modifier = Modifier.size(22.dp))
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryTextColor
                )
            }
            content()
        }
    }
}

@Composable
fun BoxScope.BottomActionBar(
    accentColor: Color,
    accentTextColor: Color,
    onStartInterview: () -> Unit
) {
    Card(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save")
            }
            Button(
                onClick = onStartInterview,
                modifier = Modifier.weight(2f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = accentTextColor
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Interview", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


