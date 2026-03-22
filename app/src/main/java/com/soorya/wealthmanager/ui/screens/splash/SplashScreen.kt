package com.soorya.wealthmanager.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.soorya.wealthmanager.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var showCredit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(600)
        showTagline = true
        delay(600)
        showCredit = true
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.08f),
                        BackgroundDark
                    ),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / App Name
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    tween(600),
                    initialScale = 0.8f
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "₩",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = GoldPrimary,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "WEALTH",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = TextPrimary,
                            letterSpacing = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "MANAGER",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GoldPrimary,
                            letterSpacing = 6.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline
            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                Text(
                    text = "Your money. Your rules. Your Wealth.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Developer Credit at bottom
        AnimatedVisibility(
            visible = showCredit,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(tween(500))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text(
                    text = "Developed by",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Soorya × Claude",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GoldPrimary.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}
