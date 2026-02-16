package com.example.devmock.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.devmock.R
import com.example.devmock.data.repository.UserRepository
import com.example.devmock.data.repository.ProfileImageRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { UserRepository() }
    val profileImageRepository = remember { ProfileImageRepository(context) }
    val scope = rememberCoroutineScope()
    
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedLevel by remember { mutableStateOf("Beginner") }
    var selectedPath by remember { mutableStateOf("Mobile Development") }
    var goal by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            selectedImageUri = it 
            scope.launch {
                isUploadingImage = true
                profileImageRepository.uploadProfileImage(it).fold(
                    onSuccess = { url ->
                        profileImageUrl = url
                        isUploadingImage = false
                    },
                    onFailure = { 
                        isUploadingImage = false
                    }
                )
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { 
                    if (currentStep < 3) currentStep++ else onComplete() 
                }) {
                    Text("Skip", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { step ->
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (step <= currentStep) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
                },
                label = "StepTransition",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    0 -> ProfileImageStep(
                        selectedUri = selectedImageUri,
                        profileImageUrl = profileImageUrl,
                        isUploading = isUploadingImage,
                        onSelectImage = { imagePicker.launch("image/*") }
                    )
                    1 -> ExperienceLevelStep(
                        selectedLevel = selectedLevel,
                        onLevelSelected = { selectedLevel = it }
                    )
                    2 -> LearningPathStep(
                        selectedPath = selectedPath,
                        onPathSelected = { selectedPath = it }
                    )
                    3 -> GoalStep(
                        goal = goal,
                        onGoalChange = { goal = it }
                    )
                }
            }

            Button(
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        isLoading = true
                        scope.launch {
                            val userName = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName ?: "Devmock User"
                            repository.updateOnboardingData(userName, selectedLevel, selectedPath, goal, profileImageUrl)
                            isLoading = false
                            onComplete()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isLoading && !isUploadingImage
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        if (currentStep < 3) "Continue" else "Finish Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileImageStep(
    selectedUri: Uri?,
    profileImageUrl: String?,
    isUploading: Boolean,
    onSelectImage: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Add a profile picture",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "Let others see who you are.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onSelectImage() },
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (isUploading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val imageSource = profileImageUrl ?: selectedUri
                    if (imageSource != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageSource)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
            
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                    .clickable { onSelectImage() }
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        if (profileImageUrl != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Photo successfully saved!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
    }
}

@Composable
fun ExperienceLevelStep(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "What's your experience level?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "This helps us tailor the interview questions for you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        val levels = listOf(
            OnboardingOption("Beginner", "Just starting out", Icons.Outlined.School),
            OnboardingOption("Intermediate", "Some experience", Icons.Outlined.LaptopMac),
            OnboardingOption("Advanced", "Experienced professional", Icons.Outlined.RocketLaunch)
        )

        levels.forEach { level ->
            SelectableCard(
                title = level.title,
                subtitle = level.subtitle,
                icon = level.icon,
                isSelected = selectedLevel == level.title,
                onClick = { onLevelSelected(level.title) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun LearningPathStep(
    selectedPath: String,
    onPathSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "What do you want to learn?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "Pick your primary focus area.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        val paths = listOf(
            OnboardingOption("Mobile Development", "Android, iOS, Flutter", Icons.Outlined.PhoneAndroid),
            OnboardingOption("Frontend Engineering", "React, Vue, Web", Icons.Outlined.Palette),
            OnboardingOption("Backend Engineering", "Kotlin, Python, Java", Icons.Outlined.Storage)
        )

        paths.forEach { path ->
            SelectableCard(
                title = path.title,
                subtitle = path.subtitle,
                icon = path.icon,
                isSelected = selectedPath == path.title,
                onClick = { onPathSelected(path.title) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun GoalStep(
    goal: String,
    onGoalChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "What is your career goal?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "Describe what you're aiming for in a few words.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = goal,
            onValueChange = onGoalChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("e.g., Get a Senior Android job at Devmock", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun SelectableCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), 
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            if (isSelected) {
                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

data class OnboardingOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)
