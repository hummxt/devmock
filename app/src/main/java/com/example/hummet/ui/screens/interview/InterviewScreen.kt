package com.example.hummet.ui.screens.interview

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.hummet.ui.theme.isAppInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InterviewTopic(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: DifficultyLevel,
    val category: Category,
    val questionsCount: Int,
    val estimatedTime: String,
    val isCompleted: Boolean = false,
    val progress: Float = 0f
)

data class StatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

enum class DifficultyLevel(val label: String, val color: Color) {
    JUNIOR("Junior", Color(0xFF10B981)),
    MIDDLE("Middle", Color(0xFFF59E0B)),
    SENIOR("Senior", Color(0xFFEF4444))
}

enum class Category(val label: String, val icon: ImageVector, val gradient: List<Color>) {
    WEB("Web", Icons.Outlined.Language, listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))),
    MOBILE("Mobile", Icons.Outlined.PhoneAndroid, listOf(Color(0xFF10B981), Color(0xFF059669))),
    DESKTOP("Desktop", Icons.Outlined.Computer, listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))),
    BACKEND("Backend", Icons.Outlined.Storage, listOf(Color(0xFFEC4899), Color(0xFFDB2777))),
    FRONTEND("Frontend", Icons.Outlined.Palette, listOf(Color(0xFF14B8A6), Color(0xFF0D9488))),
    DEVOPS("DevOps", Icons.Outlined.CloudQueue, listOf(Color(0xFFF97316), Color(0xFFEA580C))),
    DATABASE("Database", Icons.Outlined.TableChart, listOf(Color(0xFF6366F1), Color(0xFF4F46E5))),
    ARCHITECTURE("Architecture", Icons.Outlined.AccountTree, listOf(Color(0xFFEC4899), Color(0xFF9333EA))),
    TESTING("Testing", Icons.Outlined.BugReport, listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))),
    SECURITY("Security", Icons.Outlined.Security, listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
}

val sampleTopics = listOf(
    InterviewTopic("1", "Object-Oriented Programming", "Core OOP concepts: encapsulation and inheritance.", DifficultyLevel.JUNIOR, Category.BACKEND, 24, "45 min"),
    InterviewTopic("2", "Kotlin Coroutines", "Structured concurrency and Flow.", DifficultyLevel.MIDDLE, Category.MOBILE, 32, "60 min"),
    InterviewTopic("3", "Jetpack Compose", "Modern declarative UI and State management.", DifficultyLevel.MIDDLE, Category.MOBILE, 28, "55 min"),
    InterviewTopic("4", "Clean Architecture", "SOLID principles and layered architecture.", DifficultyLevel.SENIOR, Category.ARCHITECTURE, 20, "50 min"),
    InterviewTopic("5", "REST API Design", "RESTful principles and best practices.", DifficultyLevel.MIDDLE, Category.BACKEND, 18, "40 min"),
    InterviewTopic("6", "SQL & Database Design", "Queries, indexing, and normalization.", DifficultyLevel.JUNIOR, Category.DATABASE, 30, "50 min"),
    InterviewTopic("7", "Docker & Containers", "Containerization and orchestration basics.", DifficultyLevel.MIDDLE, Category.DEVOPS, 22, "45 min"),
    InterviewTopic("8", "Unit Testing", "TDD, mocking, and test strategies.", DifficultyLevel.JUNIOR, Category.TESTING, 26, "40 min")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewScreen(
    onStartInterview: (String, Int, String) -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    var customTopic by remember { mutableStateOf("") }
    var customQuestionCount by remember { mutableIntStateOf(10) }
    var customDifficulty by remember { mutableStateOf("Middle") }
    var isRandomMix by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedDifficulty by remember { mutableStateOf<DifficultyLevel?>(null) }
    var showStats by remember { mutableStateOf(false) }

    val isDark = isAppInDarkTheme()
    val primaryTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray
    val accentButtonColor = if (isDark) Color.White else Color.Black
    val accentButtonTextColor = if (isDark) Color.Black else Color.White
    val containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0)
    val dailyCardColor = if (isDark) Color(0xFF0D2F47) else Color(0xFFC3E7FF)
    val dailyCardTextColor = if (isDark) Color.White else Color.Black
    val streakCardColor = if (isDark) Color(0xFF2D1B47) else Color(0xFFE9D5FF)
    val achievementCardColor = if (isDark) Color(0xFF1F3A1F) else Color(0xFFD1FAE5)

    val filteredTopics = remember(searchQuery, selectedCategory, selectedDifficulty) {
        sampleTopics.filter { topic ->
            val matchesSearch = searchQuery.isEmpty() || topic.title.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || topic.category == selectedCategory
            val matchesDifficulty = selectedDifficulty == null || topic.difficulty == selectedDifficulty
            matchesSearch && matchesCategory && matchesDifficulty
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding: PaddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 24.dp,
                bottom = padding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interview Prep",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                    IconButton(onClick = { showStats = !showStats }) {
                        Icon(
                            imageVector = if (showStats) Icons.Outlined.GridView else Icons.Outlined.BarChart,
                            contentDescription = "Toggle Stats",
                            tint = primaryTextColor
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(visible = showStats) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsGrid(
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor,
                            containerColor = containerColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            item {
                CustomAiInterviewCard(
                    topic = customTopic,
                    onTopicChange = { customTopic = it },
                    questionCount = customQuestionCount,
                    onQuestionCountChange = { customQuestionCount = it },
                    difficulty = customDifficulty,
                    onDifficultyChange = { customDifficulty = it },
                    isRandomMix = isRandomMix,
                    onRandomMixChange = { isRandomMix = it },
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentButtonColor,
                    accentTextColor = accentButtonTextColor,
                    containerColor = containerColor,
                    onStartInterview = { 
                        onStartInterview(customTopic, customQuestionCount, customDifficulty)
                    }
                )
            }


            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StreakCard(
                        modifier = Modifier.weight(1f),
                        cardColor = streakCardColor,
                        textColor = dailyCardTextColor
                    )
                    AchievementCard(
                        modifier = Modifier.weight(1f),
                        cardColor = achievementCardColor,
                        textColor = dailyCardTextColor
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search topics...", color = secondaryTextColor, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = primaryTextColor, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = secondaryTextColor, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor
                    )
                )
            }

            item {
                CategoryFilterRow(
                    selectedCategory = selectedCategory,
                    accentColor = accentButtonColor,
                    accentTextColor = accentButtonTextColor,
                    containerColor = containerColor,
                    textColor = primaryTextColor,
                    onCategorySelected = { selectedCategory = if (selectedCategory == it) null else it }
                )
            }

            item {
                DifficultyFilterRow(
                    selectedDifficulty = selectedDifficulty,
                    accentColor = accentButtonColor,
                    accentTextColor = accentButtonTextColor,
                    containerColor = containerColor,
                    textColor = primaryTextColor,
                    onDifficultySelected = { selectedDifficulty = if (selectedDifficulty == it) null else it }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Topics (${filteredTopics.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                    if (selectedCategory != null || selectedDifficulty != null) {
                        TextButton(onClick = {
                            selectedCategory = null
                            selectedDifficulty = null
                        }) {
                            Text("Clear filters", fontSize = 12.sp)
                        }
                    }
                }
            }

            if (filteredTopics.isEmpty()) {
                item { EmptyState(secondaryTextColor) }
            } else {
                items(filteredTopics, key = { it.id }) { topic ->
                    TopicCard(
                        topic = topic,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        containerColor = containerColor,
                        onClick = { 
                            onNavigateToDetail(topic.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomAiInterviewCard(
    topic: String,
    onTopicChange: (String) -> Unit,
    questionCount: Int,
    onQuestionCountChange: (Int) -> Unit,
    difficulty: String,
    onDifficultyChange: (String) -> Unit,
    isRandomMix: Boolean,
    onRandomMixChange: (Boolean) -> Unit,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    accentTextColor: Color,
    containerColor: Color,
    onStartInterview: () -> Unit
) {
    val isDark = isAppInDarkTheme()
    val gradient = if (isDark) {
        listOf(Color(0xFF1E1E1E), Color(0xFF0D2F47))
    } else {
        listOf(Color.White, Color(0xFFC3E7FF))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(accentColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "AI Custom Interview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("What should we talk about?", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = primaryTextColor)
                    OutlinedTextField(
                        value = topic,
                        onValueChange = onTopicChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. React Hooks, System Design, Java Basics", color = secondaryTextColor, fontSize = 14.sp) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = primaryTextColor,
                            unfocusedTextColor = primaryTextColor,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = primaryTextColor.copy(alpha = 0.1f)
                        ),
                        singleLine = true
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Interview Length", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = primaryTextColor)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 20).forEach { count ->
                            val isSelected = questionCount == count
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable { onQuestionCountChange(count) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) accentColor else primaryTextColor.copy(alpha = 0.05f),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, primaryTextColor.copy(alpha = 0.1f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "$count Qs",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) accentTextColor else primaryTextColor,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Difficulty Level", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = primaryTextColor)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Junior", "Middle", "Senior", "Random Mix").forEach { level ->
                            val isSelected = difficulty == level
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable { onDifficultyChange(level) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) accentColor else primaryTextColor.copy(alpha = 0.05f),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, primaryTextColor.copy(alpha = 0.1f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        level,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) accentTextColor else primaryTextColor,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onStartInterview,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = accentTextColor
                    ),
                    enabled = topic.isNotBlank()
                ) {
                    Icon(Icons.Outlined.ElectricBolt, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start AI Interview", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatsGrid(primaryTextColor: Color, secondaryTextColor: Color, containerColor: Color) {
    val stats = listOf(
        StatItem("Topics Completed", "0", Icons.Outlined.CheckCircle, Color(0xFF10B981)),
        StatItem("Hours Practiced", "0", Icons.Outlined.Schedule, Color(0xFF3B82F6)),
        StatItem("Success Rate", "0%", Icons.Outlined.TrendingUp, Color(0xFFF59E0B)),
        StatItem("Current Streak", "0 days", Icons.Outlined.LocalFireDepartment, Color(0xFFEF4444))
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        stats.chunked(2).forEach { rowStats ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowStats.forEach { stat ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).background(stat.color.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(stat.icon, null, tint = stat.color, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(stat.value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryTextColor)
                                Text(stat.label, fontSize = 11.sp, color = secondaryTextColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreakCard(modifier: Modifier = Modifier, cardColor: Color, textColor: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.LocalFireDepartment, null, tint = textColor, modifier = Modifier.size(24.dp))
            Text("0 Days", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textColor)
            Text("Current Streak", fontSize = 12.sp, color = textColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun AchievementCard(modifier: Modifier = Modifier, cardColor: Color, textColor: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.EmojiEvents, null, tint = textColor, modifier = Modifier.size(24.dp))
            Text("0 / 50", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textColor)
            Text("Topics Mastered", fontSize = 12.sp, color = textColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun CategoryFilterRow(selectedCategory: Category?, accentColor: Color, accentTextColor: Color, containerColor: Color, textColor: Color, onCategorySelected: (Category) -> Unit) {
    Column {
        Text("Categories", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Category.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(category.icon, null, modifier = Modifier.size(14.dp))
                            Text(category.label, fontSize = 12.sp)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accentColor, selectedLabelColor = accentTextColor, containerColor = containerColor, labelColor = textColor),
                    border = null
                )
            }
        }
    }
}

@Composable
fun DifficultyFilterRow(selectedDifficulty: DifficultyLevel?, accentColor: Color, accentTextColor: Color, containerColor: Color, textColor: Color, onDifficultySelected: (DifficultyLevel) -> Unit) {
    Column {
        Text("Difficulty Level", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DifficultyLevel.entries.forEach { difficulty ->
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = selectedDifficulty == difficulty,
                    onClick = { onDifficultySelected(difficulty) },
                    label = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(if (selectedDifficulty == difficulty) accentTextColor else difficulty.color, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(difficulty.label, textAlign = TextAlign.Center, fontSize = 12.sp)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accentColor, selectedLabelColor = accentTextColor, containerColor = containerColor, labelColor = textColor),
                    border = null
                )
            }
        }
    }
}

@Composable
fun TopicCard(
    topic: InterviewTopic,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    val isDark = isAppInDarkTheme()
    val bgColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = topic.difficulty.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = topic.difficulty.label,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = topic.difficulty.color
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = topic.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor
                )
                Text(
                    text = "${topic.questionsCount} Questions • ${topic.estimatedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor
                )
            }
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(borderColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = primaryTextColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(textColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(60.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Outlined.SearchOff, null, tint = textColor.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
        Text("No topics found", color = textColor, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Text("Try adjusting your filters", color = textColor.copy(alpha = 0.7f), fontSize = 13.sp)
    }
}