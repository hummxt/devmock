package com.example.devmock.ui.screens.home

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
import com.example.devmock.ui.components.ProfileSection

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
    var selectedTab by remember { mutableStateOf("Languages") }

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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    ProfileSection(
                        primaryTextColor = MaterialTheme.colorScheme.onBackground,
                        secondaryTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        placeholder = { Text("Search interview topics...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
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
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(Icons.Outlined.Terminal, null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { 0f },
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(0.2f)
                                )
                                Text("0/3", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            "Daily Challenge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Today: System Design Basics",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToInterview,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
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
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedTab == tab,
                                        borderColor = Color.Transparent
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
                    SmallTaskCard(
                        topic = cardData.first,
                        icon = Icons.Rounded.Psychology,
                        accentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onClick = onNavigateToQuestions
                    )
                    SmallTaskCard(
                        topic = cardData.second,
                        icon = Icons.Outlined.Code,
                        accentColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
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
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = accentColor),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, modifier = Modifier.size(26.dp), tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(topic.title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(topic.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.7f), lineHeight = 14.sp)
            }
        }
    }
}
