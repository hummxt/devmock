package com.example.hummet.ui.screens.home

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.hummet.ui.theme.isAppInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hummet.ui.components.ProfileSection

data class InterviewTopic(
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(
    onNavigateToInterview: () -> Unit,
    onNavigateToQuestions: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Languages") }
    val isDark = isAppInDarkTheme()

    val primaryTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray
    val accentButtonColor = if (isDark) Color.White else Color.Black
    val accentButtonTextColor = if (isDark) Color.Black else Color.White
    val searchContainerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0)
    val dailyCardColor = if (isDark) Color(0xFF0D2F47) else Color(0xFFC3E7FF)
    val dailyCardTextColor = if (isDark) Color.White else Color.Black

    val row1 = listOf("Languages", "Backend", "Frontend")
    val row2 = listOf("Databases", "DevOps", "Mobile")

    val cardData = when (selectedTab) {
        "Languages" -> Pair(
            InterviewTopic("Core Languages", "Java, Kotlin, JavaScript, Python – syntax and memory model."),
            InterviewTopic("Advanced Concepts", "OOP, Functional Programming, closures, and generics.")
        )
        "Backend" -> Pair(
            InterviewTopic("Backend Frameworks", "Spring Boot, Ktor, Node.js – REST APIs and MVC."),
            InterviewTopic("APIs & Auth", "REST, GraphQL, JWT, OAuth 2.0, and middleware.")
        )
        "Frontend" -> Pair(
            InterviewTopic("Frontend Frameworks", "React, Next.js, Vue – components and state management."),
            InterviewTopic("UI & Performance", "Responsive design and rendering optimization.")
        )
        "Databases" -> Pair(
            InterviewTopic("SQL & NoSQL", "PostgreSQL, MySQL, MongoDB – schema design and queries."),
            InterviewTopic("Performance", "Indexes, transactions, and query optimization.")
        )
        "DevOps" -> Pair(
            InterviewTopic("Containers & CI/CD", "Docker, GitHub Actions, and automated deployments."),
            InterviewTopic("Cloud Basics", "AWS, GCP, scaling and monitoring.")
        )
        "Mobile" -> Pair(
            InterviewTopic("Android Development", "Kotlin, Jetpack Compose, and MVVM architecture."),
            InterviewTopic("Cross-Platform", "React Native, Flutter – performance tradeoffs.")
        )
        else -> Pair(
            InterviewTopic("System Design", "Scalability, load balancing, and microservices."),
            InterviewTopic("Clean Code", "SOLID principles and code reviews.")
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp)
                ) {
                    ProfileSection(
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        placeholder = { Text("Search interview topics...", color = secondaryTextColor, fontSize = 14.sp) },
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
                            focusedContainerColor = searchContainerColor,
                            unfocusedContainerColor = searchContainerColor,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = primaryTextColor,
                            unfocusedTextColor = primaryTextColor
                        )
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = dailyCardColor),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(Icons.Outlined.Terminal, null, modifier = Modifier.size(28.dp), tint = dailyCardTextColor)
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { 0f },
                                    modifier = Modifier.size(40.dp),
                                    color = dailyCardTextColor,
                                    strokeWidth = 4.dp,
                                    trackColor = dailyCardTextColor.copy(0.2f)
                                )
                                Text("0/3", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dailyCardTextColor)
                            }
                        }
                        Text(
                            "Daily Challenge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp),
                            color = dailyCardTextColor
                        )
                        Text(
                            "Today: System Design Basics",
                            style = MaterialTheme.typography.bodyMedium,
                            color = dailyCardTextColor.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToInterview,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentButtonColor,
                                contentColor = accentButtonTextColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Start Interview")
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(row1, row2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { tab ->
                                FilterChip(
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    label = { Text(tab, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentButtonColor,
                                        selectedLabelColor = accentButtonTextColor,
                                        containerColor = searchContainerColor,
                                        labelColor = primaryTextColor
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedTab == tab,
                                        borderColor = if (selectedTab == tab) accentButtonColor else Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val purpleAccent = if (isDark) Color(0xFF4A3457) else Color(0xFFE8B9FF)
                    val orangeAccent = if (isDark) Color(0xFF5C3D1E) else Color(0xFFFFB366)

                    SmallTaskCard(
                        topic = cardData.first,
                        icon = Icons.Rounded.Psychology,
                        accentColor = purpleAccent,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onClick = onNavigateToQuestions
                    )
                    SmallTaskCard(
                        topic = cardData.second,
                        icon = Icons.Outlined.Code,
                        accentColor = orangeAccent,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onClick = onNavigateToQuestions
                    )
                }
            }
        }
    }
}

@Composable
fun SmallTaskCard(
    topic: InterviewTopic,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val isDark = isAppInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = accentColor),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, modifier = Modifier.size(26.dp), tint = contentColor)
            Column {
                Text(topic.title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, lineHeight = 18.sp, color = contentColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(topic.description, style = MaterialTheme.typography.bodySmall, color = contentColor.copy(0.7f), lineHeight = 14.sp)
            }
        }
    }
}

