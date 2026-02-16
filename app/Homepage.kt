package com.example.devmock.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devmock.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(
    onNavigateToCoding: () -> Unit,
    onNavigateToQuestions: () -> Unit
) {
    var showNotifs by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Logic") }
    val tabs = listOf("Logic", "Algorithms")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Hello, Devmock User",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Level 3",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showNotifs = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showNotifs,
                            onDismissRequest = { showNotifs = false },
                            modifier = Modifier.width(200.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("No new questions today!") },
                                onClick = { showNotifs = false },
                                leadingIcon = { Icon(Icons.Outlined.Lightbulb, null) }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                active = false,
                onActiveChange = {},
                placeholder = { Text("Search programming questions...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {}

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC3E7FF)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Outlined.Terminal, null, modifier = Modifier.size(32.dp))
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { 0f },
                                modifier = Modifier.size(45.dp),
                                color = Color.Black,
                                strokeWidth = 4.dp,
                                trackColor = Color.White.copy(0.4f)
                            )
                            Text("0/3", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Practice Kotlin",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Master loops and functions",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToCoding,
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Try it now", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(tabs.size) { index ->
                    FilterChip(
                        selected = selectedTab == tabs[index],
                        onClick = { selectedTab = tabs[index] },
                        label = { Text(tabs[index]) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallTaskCard(
                    title = "Sorting Algorithm",
                    color = Color(0xFFE8B9FF),
                    icon = Icons.Rounded.Psychology,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToQuestions
                )
                SmallTaskCard(
                    title = "Debug Session",
                    color = Color(0xFFFFB366),
                    icon = Icons.Outlined.Code,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCoding
                )
            }
        }
    }
}

@Composable
fun SmallTaskCard(
    title: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(170.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, modifier = Modifier.size(28.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Brief 001",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(0.6f)
                )
            }
        }
    }
}