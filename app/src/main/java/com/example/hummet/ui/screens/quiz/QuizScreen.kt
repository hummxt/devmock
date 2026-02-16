package com.example.hummet.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.hummet.ui.theme.isAppInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class TopicGroup(
    val id: String,
    val topicTitle: String,
    val category: String,
    val difficulty: String,
    val questions: List<String>,
    val accentColor: Color,
    val tags: List<String> = emptyList(),
    val companyHistory: String = ""
)

val topicData = listOf(
    TopicGroup("1", "React State Management", "Frontend", "Medium",
        listOf("What is the difference between useState and useReducer?", "When should you use Context API over Redux?", "How do you lift state up in React?", "What are the limitations of local state?"),
        Color(0xFFE8B9FF)),
    TopicGroup("2", "Kotlin Coroutines", "Languages", "Hard",
        listOf("What is a CoroutineScope?", "Explain the difference between Launch and Async", "How does 'suspend' work under the hood?", "What is a Dispatcher?"),
        Color(0xFFFFB366)),
    TopicGroup("3", "SQL Indexing", "Databases", "Medium",
        listOf("What is a B-Tree index?", "Difference between Clustered and Non-Clustered indexes?", "When does an index slow down performance?", "How to use EXPLAIN to optimize queries?"),
        Color(0xFFA2D2FF)),
    TopicGroup("4", "Node.js Event Loop", "Backend", "Hard",
        listOf("Explain the phases of the Event Loop", "What is setImmediate vs process.nextTick?", "How does Node handle non-blocking I/O?", "What is the role of Libuv?"),
        Color(0xFFB9FBC0)),
    TopicGroup("5", "Swift UI Basics", "Mobile", "Easy",
        listOf("What is a View in SwiftUI?", "How do @State and @Binding differ?", "Explain the Body property", "How to create a List in SwiftUI?"),
        Color(0xFFFFCFD2))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(
    navController: NavController,
    viewModel: QuestionsViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedCompany by remember { mutableStateOf("All") }

    val isDark = isAppInDarkTheme()
    val primaryTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray
    val containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val cardBorderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val chipContainerColor = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
    val accentColor = if (isDark) Color.White else Color.Black
    val accentTextColor = if (isDark) Color.Black else Color.White

    val companies = listOf("All", "Google", "Meta", "Amazon", "Apple", "Netflix")
    val tags = listOf("All", "Arrays", "Linked Lists", "Closures", "Dynamic Programming", "System Design")

    val filteredTopics = state.topics.filter { topic ->
        val matchesSearch = topic.topicTitle.contains(searchQuery, ignoreCase = true)
        val matchesDiff = selectedDifficulty == "All" || topic.difficulty == selectedDifficulty
        val matchesCat = selectedCategory == "All" || topic.category == selectedCategory
        matchesSearch && matchesDiff && matchesCat
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding: PaddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 0.dp,
                top = padding.calculateTopPadding() + 24.dp,
                end = 0.dp,
                bottom = padding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Questions Library",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search topics, tags, companies...", color = secondaryTextColor, fontSize = 14.sp) },
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
                            focusedContainerColor = chipContainerColor,
                            unfocusedContainerColor = chipContainerColor,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = primaryTextColor,
                            unfocusedTextColor = primaryTextColor
                        )
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column {
                        Text(
                            "Company",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(companies.size) { index ->
                                val company = companies[index]
                                FilterChip(
                                    selected = selectedCompany == company,
                                    onClick = { selectedCompany = company },
                                    label = { Text(company, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentColor,
                                        selectedLabelColor = accentTextColor,
                                        containerColor = chipContainerColor,
                                        labelColor = primaryTextColor
                                    ),
                                    border = null
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            "Tags",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tags.size) { index ->
                                val tag = tags[index]
                                AssistChip(
                                    onClick = { },
                                    label = { Text(tag, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = chipContainerColor,
                                        labelColor = primaryTextColor
                                    ),
                                    border = null
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            "Difficulty",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Easy", "Medium", "Hard").forEach { diff ->
                                FilterChip(
                                    modifier = Modifier.weight(1f),
                                    selected = selectedDifficulty == diff,
                                    onClick = { selectedDifficulty = diff },
                                    label = {
                                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                            Text(diff, fontSize = 12.sp)
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentColor,
                                        selectedLabelColor = accentTextColor,
                                        containerColor = chipContainerColor,
                                        labelColor = primaryTextColor
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Topics (${filteredTopics.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 0.dp)
                )
            }

            itemsIndexed(filteredTopics) { index, topic ->
                QuestionCard(
                    topic = topic,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    containerColor = containerColor,
                    borderColor = cardBorderColor,
                    onClick = { navController.navigate("questions/${topic.id}") }
                )
            }
        }
    }
}

@Composable
fun QuestionCard(
    topic: TopicGroup,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when(topic.difficulty) {
                            "Easy" -> Color(0xFF10B981).copy(alpha = 0.1f)
                            "Medium" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                            else -> Color(0xFFEF4444).copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = topic.difficulty,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when(topic.difficulty) {
                                "Easy" -> Color(0xFF10B981)
                                "Medium" -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = topic.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = topic.topicTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor
                )
                Text(
                    text = "${topic.questions.size} key concepts to master",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor
                )
                if (topic.companyHistory.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.History, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = topic.companyHistory,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
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
