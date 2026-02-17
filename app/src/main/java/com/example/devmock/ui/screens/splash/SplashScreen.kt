package com.example.devmock.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.devmock.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    var animationStarted by remember { mutableStateOf(false) }
    
    val logoScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "logo_alpha"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    
    val particleScale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_1"
    )
    
    val particleScale2 by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_2"
    )
    
    val particleScale3 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_3"
    )
    
    val shimmer by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    ),
                    startY = gradientOffset,
                    endY = gradientOffset + 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .scale(particleScale1)
                .alpha(0.08f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(particleScale2)
                .alpha(0.12f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .size(500.dp)
                .scale(particleScale3)
                .alpha(0.06f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.full_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(220.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.15f)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            startX = shimmer - 200f,
                            endX = shimmer + 200f
                        )
                    )
            )
        }
    }
}
