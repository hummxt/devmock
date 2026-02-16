package com.example.devmock.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devmock.R
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Person
import com.example.devmock.data.repository.UserRepository
import com.example.devmock.data.repository.UserData
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSection(
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    val repository = remember { UserRepository() }
    val auth = remember { FirebaseAuth.getInstance() }
    var userData by remember { mutableStateOf<UserData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        userData = repository.getUserProfile()
        isLoading = false
    }

    val displayName = userData?.name ?: auth.currentUser?.displayName ?: "Devmock User"
    val experienceLevel = userData?.experienceLevel ?: "Beginner"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                color = primaryTextColor.copy(alpha = 0.1f)
            ) {
                if (userData?.profilePhotoUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userData?.profilePhotoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = primaryTextColor.copy(alpha = 0.5f)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                val nameToDisplay = if (displayName == "Devmock User") "Hummet User" else displayName
                Text(
                    "Hello, $nameToDisplay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor
                )
                Text(
                    experienceLevel,
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor
                )
            }
        }
        
        IconButton(onClick = { }) {
            Icon(
                Icons.Outlined.Notifications,
                null,
                modifier = Modifier.size(26.dp),
                tint = primaryTextColor
            )
        }
    }
}