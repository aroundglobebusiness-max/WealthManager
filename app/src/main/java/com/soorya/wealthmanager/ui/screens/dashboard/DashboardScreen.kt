package com.soorya.wealthmanager.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.soorya.wealthmanager.domain.model.*
import com.soorya.wealthmanager.ui.components.*
import com.soorya.wealthmanager.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit,
    onViewAll: () -> Unit,
    onNetWorth: () -> Unit,
    onGoals: () -> Unit,
    onSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GoldPrimary.copy(alpha = 0.06f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                DashboardHeader(
                    currencySymbol = uiState.currencySymbol,
                    balance = uiState.balance,
                    monthlyExpense = uiState.monthlyExpense,
                    isSyncing = uiState.isSyncing,
                    onSync = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.syncToNotion()
                    },
                    onSettings = onSettings
                )
            }

            // Sync message
            uiState.syncMessage?.let { msg ->
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically() + fadeIn()
                    ) {
                        GlassCard(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall.copy(color = GoldPrimary)
                                )
                                IconButton(onClick = viewModel::dismissSyncMessage) {
                                    Icon(Icons.Rounded.Close, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Stats Row
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(
                        label = "Income",
                        value = "${uiState.currencySymbol}${formatAmount(uiState.monthlyIncome)}",
                        valueColor = IncomeGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = "Spent",
                        value = "${uiState.currencySymbol}${formatAmount(uiState.monthlyExpense)}",
                        valueColor = ExpenseRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = "Saved",
                        value = "${uiState.currencySymbol}${formatAmount((uiState.monthlyIncome - uiState.monthlyExpense).coerceAtLeast(0.0))}",
                        valueColor = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Wealth Score
            uiState.wealthScore?.let { score ->
                item {
                    GoldGlassCard(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .clickable { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    "WEALTH SCORE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GoldPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    score.label,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                score.breakdown.forEach { (key, value) ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "• $key",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                        )
                                        Text(
                                            "$value pts",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GoldPrimary)
                                        )
                                    }
                                }
                            }
                            WealthScoreRing(score = score.score, label = "/ 100")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Rounded.AccountBalance,
                        label = "Net Worth",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNetWorth()
                        }
                    )
                    QuickActionCard(
                        icon = Icons.Rounded.TrackChanges,
                        label = "Goals",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onGoals()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Recent Transactions
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("RECENT TRANSACTIONS")
                    TextButton(onClick = onViewAll) {
                        Text("See All", color = GoldPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💸", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions yet", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(uiState.recentTransactions) { txn ->
                    TransactionItem(
                        transaction = txn,
                        currencySymbol = uiState.currencySymbol,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onAddTransaction()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = GoldPrimary,
            contentColor = BackgroundDark,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun DashboardHeader(
    currencySymbol: String,
    balance: Double,
    monthlyExpense: Double,
    isSyncing: Boolean,
    onSync: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Good ${getGreeting()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row {
                if (isSyncing) {
                    LoadingDots()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(onClick = onSync) {
                    Icon(
                        if (isSyncing) Icons.Rounded.Sync else Icons.Rounded.CloudSync,
                        contentDescription = "Sync",
                        tint = if (isSyncing) GoldPrimary else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = TextSecondary, modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Total Balance", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "$currencySymbol${formatAmount(balance)}",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (balance >= 0) TextPrimary else ExpenseRed
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "This month: -$currencySymbol${formatAmount(monthlyExpense)}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(20.dp))
        GoldDivider()
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val emoji = getCategoryEmoji(transaction.category)
    val isIncome = transaction.type == TransactionType.INCOME
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Emoji icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isIncome) IncomeGreen.copy(alpha = 0.1f) else ExpenseRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, style = MaterialTheme.typography.titleLarge)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${transaction.category} • ${dateFormat.format(Date(transaction.date))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (isIncome) "+" else "-"}$currencySymbol${formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (isIncome) IncomeGreen else ExpenseRed,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (transaction.syncedToNotion) {
                    Text("✓ Notion", style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary.copy(alpha = 0.7f)))
                }
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return if (amount >= 1_00_000) {
        "%.1fL".format(amount / 1_00_000)
    } else if (amount >= 1_000) {
        "%.1fK".format(amount / 1_000)
    } else {
        "%.0f".format(amount)
    }
}

private fun getGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Morning 🌅"
        in 12..16 -> "Afternoon ☀️"
        in 17..20 -> "Evening 🌆"
        else -> "Night 🌙"
    }
}

private fun getCategoryEmoji(category: String): String = when (category.lowercase()) {
    "petrol", "fuel", "transport" -> "🛵"
    "food", "dining", "restaurant" -> "🍽️"
    "shopping", "clothes" -> "🛒"
    "health", "medical" -> "💊"
    "rent", "housing" -> "🏠"
    "bills", "utilities" -> "📱"
    "entertainment" -> "🎮"
    "salary", "income" -> "💼"
    "investment" -> "📈"
    "travel" -> "✈️"
    "education" -> "📚"
    else -> "💸"
}
