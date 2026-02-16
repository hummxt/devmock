package com.example.hummet.ui.screens.interview

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

    val sampleTopics = listOf(
        InterviewTopic("1", "Object-Oriented Programming", "Core OOP concepts: encapsulation and inheritance.", DifficultyLevel.JUNIOR, Category.BACKEND, 24, "45 min"),
        InterviewTopic("2", "Kotlin Coroutines", "Structured concurrency and Flow.", DifficultyLevel.MIDDLE, Category.MOBILE, 32, "60 min"),
        InterviewTopic("3", "Jetpack Compose", "Modern declarative UI and State management.", DifficultyLevel.MIDDLE, Category.MOBILE, 28, "55 min"),
        InterviewTopic("4", "Clean Architecture", "SOLID principles and layered architecture.", DifficultyLevel.SENIOR, Category.ARCHITECTURE, 20, "50 min")
    )

    val filteredTopics = sampleTopics.filter { topic ->
        val matchesSearch = searchQuery.isEmpty() || topic.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || topic.category == selectedCategory
        val matchesDifficulty = selectedDifficulty == null || topic.difficulty == selectedDifficulty
        matchesSearch && matchesCategory && matchesDifficulty
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { showStats = !showStats }) {
                        Icon(
                            imageVector = if (showStats) Icons.Outlined.GridView else Icons.Outlined.BarChart,
                            contentDescription = "Toggle Stats",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(visible = showStats) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsGrid()
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
                    StreakCard(modifier = Modifier.weight(1f))
                    AchievementCard(modifier = Modifier.weight(1f))
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search topics...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            item {
                CategoryFilterRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = if (selectedCategory == it) null else it }
                )
            }

            item {
                DifficultyFilterRow(
                    selectedDifficulty = selectedDifficulty,
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (selectedCategory != null || selectedDifficulty != null) {
                        TextButton(onClick = {
                            selectedCategory = null
                            selectedDifficulty = null
                        }) {
                            Text("Clear filters", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            if (filteredTopics.isEmpty()) {
                item { EmptyState() }
            } else {
                items(filteredTopics, key = { it.id }) { topic ->
                    TopicCard(
                        topic = topic,
                        onClick = { onNavigateToDetail(topic.id) }
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
    onStartInterview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "AI Custom Interview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("What should we talk about?", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                OutlinedTextField(
                    value = topic,
                    onValueChange = onTopicChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. React Hooks, System Design, Java Basics", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    singleLine = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Interview Length", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
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
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "$count Qs",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Difficulty Level", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
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
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    level,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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

@Composable
fun StatsGrid() {
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
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
                                Text(stat.value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(stat.label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreakCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, 
        shape = RoundedCornerShape(20.dp), 
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text("0 Days", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Current Streak", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AchievementCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, 
        shape = RoundedCornerShape(20.dp), 
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.EmojiEvents, null, tint = Color(0xFFF5CA0E), modifier = Modifier.size(24.dp))
            Text("0 / 50", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Topics Mastered", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CategoryFilterRow(selectedCategory: Category?, onCategorySelected: (Category) -> Unit) {
    Column {
        Text("Categories", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
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
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun DifficultyFilterRow(selectedDifficulty: DifficultyLevel?, onDifficultySelected: (DifficultyLevel) -> Unit) {
    Column {
        Text("Difficulty Level", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DifficultyLevel.entries.forEach { difficulty ->
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = selectedDifficulty == difficulty,
                    onClick = { onDifficultySelected(difficulty) },
                    label = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(if (selectedDifficulty == difficulty) MaterialTheme.colorScheme.onPrimary else difficulty.color, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(difficulty.label, textAlign = TextAlign.Center, fontSize = 12.sp)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun TopicCard(
    topic: InterviewTopic,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${topic.questionsCount} Questions • ${topic.estimatedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(modifier = Modifier.fillMaxWidth().padding(60.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Outlined.SearchOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
        Text("No topics found", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Text("Try adjusting your filters", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}