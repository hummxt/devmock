package com.example.hummet.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.hummet.ui.theme.isAppInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hummet.R
import com.example.hummet.data.repository.UserData
import com.example.hummet.data.repository.ProfileImageRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(onNavigateToSettings: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { com.example.hummet.data.repository.UserRepository() }
    val profileImageRepository = remember { ProfileImageRepository(context) }
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()
    
    var userData by remember { mutableStateOf<UserData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var showImageOptions by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        userData = repository.getUserProfile()
        profileImageUrl = profileImageRepository.getProfileImageUrl()
        isLoading = false
    }

    val isDark = isAppInDarkTheme()
    val primaryTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray
    val containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val accentColor = if (isDark) Color.White else Color.Black
    
    val displayName = userData?.name ?: auth.currentUser?.displayName ?: "Hummet User"
    val userRole = userData?.role ?: "Fullstack Developer"
    val userGoal = userData?.goal ?: "Aiming for Mid-level Backend at Hummet MMC"
    val readyMeterProgress = (userData?.readyMeter ?: 0) / 100f
    val readyMeterText = "${(readyMeterProgress * 100).toInt()}%"
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding: PaddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .background(borderColor, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Settings, null, tint = primaryTextColor, modifier = Modifier.size(20.dp))
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, accentColor, CircleShape),
                            color = containerColor
                        ) {
                            if (profileImageUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(profileImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(primaryTextColor.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp),
                                        tint = primaryTextColor.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryTextColor
                    )
                    Text(
                        userRole,
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        color = containerColor,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.Flag, null, modifier = Modifier.size(14.dp), tint = secondaryTextColor)
                            Text(
                                userGoal,
                                style = MaterialTheme.typography.labelMedium,
                                color = primaryTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "Ready Meter",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor
                            )
                            Text(
                                readyMeterText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryTextColor
                            )
                        }
                        LinearProgressIndicator(
                            progress = { readyMeterProgress },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp)),
                            color = accentColor,
                            trackColor = borderColor
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Skills & Strengths",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Javascript", "Python", "SQL", "Kotlin", "React", "Docker").forEach { skill ->
                            Surface(
                                color = containerColor,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                            ) {
                                Text(
                                    skill,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = primaryTextColor
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF2D1B47) else Color(0xFFE8B9FF).copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if(isDark) Color(0xFFE8B9FF).copy(0.2f) else Color(0xFFE8B9FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if(isDark) Color(0xFFE8B9FF).copy(0.2f) else Color(0xFFE8B9FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, tint = if(isDark) Color(0xFFE8B9FF) else Color(0xFF4A3457))
                            }
                            Column {
                                Text("Top Strength", style = MaterialTheme.typography.labelSmall, color = if(isDark) Color(0xFFE8B9FF).copy(0.7f) else Color(0xFF4A3457).copy(0.7f))
                                Text("Logic King", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if(isDark) Color(0xFFE8B9FF) else Color(0xFF4A3457))
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Activity Heatmap",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(containerColor, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(7) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(20) {
                                    val randomVal = 0
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                when(randomVal) {
                                                    0 -> borderColor
                                                    1 -> Color(0xFF10B981).copy(0.2f)
                                                    2 -> Color(0xFF10B981).copy(0.5f)
                                                    3 -> Color(0xFF10B981).copy(0.8f)
                                                    else -> Color(0xFF10B981)
                                                }
                                            )
                                    )
                                }
                            }
                        }
                        Text(
                            "0 contributions in the last year",
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryTextColor,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = "Solved",
                                value = "0/100",
                                icon = Icons.Outlined.CheckCircle,
                                containerColor = containerColor,
                                borderColor = borderColor,
                                textColor = primaryTextColor
                            )
                            StatCard(
                                label = "Avg. Score",
                                value = "0%",
                                icon = Icons.Outlined.ShowChart,
                                containerColor = containerColor,
                                borderColor = borderColor,
                                textColor = primaryTextColor
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = "Mocks",
                                value = "0",
                                icon = Icons.Outlined.SupportAgent,
                                containerColor = containerColor,
                                borderColor = borderColor,
                                textColor = primaryTextColor
                            )
                            StatCard(
                                label = "Streak",
                                value = "0 Days",
                                icon = Icons.Outlined.Bolt,
                                containerColor = containerColor,
                                borderColor = borderColor,
                                textColor = primaryTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    borderColor: Color,
    textColor: Color
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = textColor.copy(0.6f))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = textColor)
            Text(label, style = MaterialTheme.typography.labelSmall, color = textColor.copy(0.6f))
        }
    }
}