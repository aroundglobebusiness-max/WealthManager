package com.soorya.wealthmanager.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.soorya.wealthmanager.ui.components.*
import com.soorya.wealthmanager.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    var showToken by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBackIosNew, null, tint = TextPrimary)
            }
            Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Notion Section
            SectionHeader("NOTION INTEGRATION")
            Spacer(modifier = Modifier.height(4.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Connect to your Notion database to auto-sync every transaction.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Token
                    Text("Integration Token", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                    OutlinedTextField(
                        value = uiState.notionToken,
                        onValueChange = viewModel::setToken,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("secret_xxxx...") },
                        visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showToken = !showToken }) {
                                Icon(
                                    if (showToken) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    null, tint = TextMuted, modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary.copy(alpha = 0.5f),
                            unfocusedBorderColor = BackgroundSurface,
                            focusedContainerColor = BackgroundSurface,
                            unfocusedContainerColor = BackgroundSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // DB ID
                    Text("Database ID", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                    OutlinedTextField(
                        value = uiState.notionDbId,
                        onValueChange = viewModel::setDbId,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("32-char database ID from URL") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary.copy(alpha = 0.5f),
                            unfocusedBorderColor = BackgroundSurface,
                            focusedContainerColor = BackgroundSurface,
                            unfocusedContainerColor = BackgroundSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Connection status
                    AnimatedVisibility(visible = uiState.connectionStatus != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (uiState.connectionStatus == true) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                                null,
                                tint = if (uiState.connectionStatus == true) IncomeGreen else ExpenseRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                if (uiState.connectionStatus == true) "Connected to Notion ✓" else "Connection failed. Check your token & DB ID",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (uiState.connectionStatus == true) IncomeGreen else ExpenseRed
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.saveAndTest()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = BackgroundDark),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isTesting
                    ) {
                        if (uiState.isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BackgroundDark, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Save & Test Connection", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // How to guide
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("How to get your Notion token", style = MaterialTheme.typography.titleSmall.copy(color = GoldPrimary))
                    listOf(
                        "Go to notion.so/my-integrations",
                        "Create new integration → Copy token",
                        "Open your Notion DB → Share → Connect integration",
                        "Copy the DB ID from the URL (32-char string)"
                    ).forEachIndexed { i, step ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(GoldSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${i + 1}", style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary, fontWeight = FontWeight.Bold))
                            }
                            Text(step, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Currency
            SectionHeader("CURRENCY")
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Default Currency", style = MaterialTheme.typography.titleSmall)
                        Text("${uiState.currencySymbol} ${uiState.currency}", style = MaterialTheme.typography.bodyMedium.copy(color = GoldPrimary))
                    }
                    Icon(Icons.Rounded.ChevronRight, null, tint = TextMuted)
                }
            }

            // Haptics
            SectionHeader("PREFERENCES")
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Haptic Feedback", style = MaterialTheme.typography.titleSmall)
                        Text("Physical vibration on actions", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = uiState.hapticsEnabled,
                        onCheckedChange = viewModel::setHaptics,
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = GoldSubtle)
                    )
                }
            }

            // About
            SectionHeader("ABOUT")
            GoldGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("₩", style = MaterialTheme.typography.headlineLarge.copy(color = GoldPrimary))
                    Text("Wealth Manager", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall)
                    GoldDivider()
                    Text("Developed by", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                    Text(
                        "Soorya × Claude",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        "Your money. Your rules. Your Wealth.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
