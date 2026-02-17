package com.example.devmock.ui.screens.settings

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import com.example.devmock.ui.theme.ThemeConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { com.example.devmock.data.repository.UserRepository() }
    val profileImageRepository = remember { com.example.devmock.data.repository.ProfileImageRepository(context) }
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var selectedLocalUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            selectedLocalUri = selectedUri
            scope.launch {
                isUploadingImage = true
                profileImageRepository.uploadProfileImage(selectedUri).onSuccess { url ->
                    profileImageUrl = url
                    isUploadingImage = false
                    selectedLocalUri = null
                    snackbarHostState.showSnackbar("Profile picture updated!")
                }.onFailure { e ->
                    isUploadingImage = false
                    selectedLocalUri = null
                    snackbarHostState.showSnackbar("Upload failed: ${e.message ?: "Unknown error"}")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val profile = repository.getUserProfile()
        if (profile != null) {
            name = profile.name
            role = profile.role
            goal = profile.goal
        }
        profileImageUrl = profileImageRepository.getProfileImageUrl()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    .clickable { imagePicker.launch("image/*") },
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                if (isUploadingImage) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                                    }
                                } else if (selectedLocalUri != null || profileImageUrl != null) {
                                    val imageData = selectedLocalUri ?: if (profileImageUrl != null) {
                                        "$profileImageUrl?t=${System.currentTimeMillis()}"
                                    } else null
                                    
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(imageData)
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
                                        modifier = Modifier.size(50.dp).padding(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                                }
                            }
                            
                            Row(
                                modifier = Modifier.offset(x = 8.dp, y = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (profileImageUrl != null && !isUploadingImage) {
                                    SmallFloatingActionButton(
                                        onClick = {
                                            scope.launch {
                                                isUploadingImage = true
                                                profileImageRepository.deleteProfileImage().onSuccess {
                                                    profileImageUrl = null
                                                    isUploadingImage = false
                                                }
                                            }
                                        },
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                                SmallFloatingActionButton(
                                    onClick = { imagePicker.launch("image/*") },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Profile Photo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                item {
                    Text(
                        "Profile Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text("Current Role") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = goal,
                            onValueChange = { goal = it },
                            label = { Text("Career Goal") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            scope.launch {
                                repository.saveUserProfile(name, role, goal, profileImageUrl)
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "App Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingItem(icon = Icons.Outlined.Notifications, title = "Notifications")
                        
                        ToggleSettingItem(
                            icon = Icons.Outlined.DarkMode,
                            title = "Dark Mode",
                            checked = ThemeConfig.isDarkMode,
                            onCheckedChange = { ThemeConfig.isDarkMode = it }
                        )
                        
                        SettingItem(icon = Icons.Outlined.Lock, title = "Privacy & Security")
                        SettingItem(icon = Icons.Outlined.HelpOutline, title = "Help & Support")

                        Spacer(modifier = Modifier.height(16.dp))

                        SettingItem(
                            icon = Icons.Outlined.Logout,
                            title = "Sign Out",
                            textColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                onNavigateBack()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Made for Developers",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                )
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = textColor.copy(alpha = 0.7f))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = textColor, modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.ChevronRight, null, modifier = Modifier.size(20.dp), tint = textColor.copy(alpha = 0.3f))
        }
    }
}
