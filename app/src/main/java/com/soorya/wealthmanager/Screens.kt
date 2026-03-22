package com.soorya.wealthmanager

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soorya.wealthmanager.data.repository.WealthRepository
import com.soorya.wealthmanager.domain.model.*
import com.soorya.wealthmanager.ui.components.*
import com.soorya.wealthmanager.ui.screens.dashboard.TransactionItem
import com.soorya.wealthmanager.ui.theme.*
import com.soorya.wealthmanager.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ── TRANSACTIONS SCREEN ──

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: WealthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val transactions = repository.getAllTransactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currencySymbol = preferencesManager.defaultSymbol.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "₹")

    fun delete(id: Long) = viewModelScope.launch { repository.deleteTransaction(id) }
}

@Composable
fun TransactionsScreen(
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val symbol by viewModel.currencySymbol.collectAsState()
    val haptic = LocalHapticFeedback.current

    val grouped = transactions.groupBy { txn ->
        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(txn.date))
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundDark).statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBackIosNew, null, tint = TextPrimary)
            }
            Text("All Transactions", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No transactions yet", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (day, txns) ->
                    item {
                        Text(
                            day,
                            style = MaterialTheme.typography.labelMedium.copy(color = TextMuted, letterSpacing = 0.5.sp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(txns, key = { it.id }) { txn ->
                        TransactionItem(
                            transaction = txn,
                            currencySymbol = symbol,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ── NET WORTH SCREEN ──

@HiltViewModel
class NetWorthViewModel @Inject constructor(
    private val repository: WealthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val assets = repository.getAllAssets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val totalAssets = repository.getTotalAssets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
    val totalLiabilities = repository.getTotalLiabilities().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
    val symbol = preferencesManager.defaultSymbol.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "₹")

    fun addAsset(asset: Asset) = viewModelScope.launch { repository.addAsset(asset) }
    fun deleteAsset(id: Long) = viewModelScope.launch { repository.deleteAsset(id) }
}

@Composable
fun NetWorthScreen(onBack: () -> Unit, viewModel: NetWorthViewModel = hiltViewModel()) {
    val assets by viewModel.assets.collectAsState()
    val totalAssets by viewModel.totalAssets.collectAsState()
    val totalLiabilities by viewModel.totalLiabilities.collectAsState()
    val symbol by viewModel.symbol.collectAsState()
    val netWorth = totalAssets - totalLiabilities
    var showAdd by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundDark).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBackIosNew, null, tint = TextPrimary) }
            Text("Net Worth", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            IconButton(onClick = { showAdd = true }) { Icon(Icons.Rounded.Add, null, tint = GoldPrimary) }
        }

        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Net Worth Card
            item {
                GoldGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("NET WORTH", style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary, letterSpacing = 1.sp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$symbol${"%,.0f".format(netWorth)}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (netWorth >= 0) GoldPrimary else ExpenseRed
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$symbol${"%,.0f".format(totalAssets)}", style = MaterialTheme.typography.titleMedium.copy(color = IncomeGreen, fontWeight = FontWeight.Bold))
                                Text("Assets", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$symbol${"%,.0f".format(totalLiabilities)}", style = MaterialTheme.typography.titleMedium.copy(color = ExpenseRed, fontWeight = FontWeight.Bold))
                                Text("Liabilities", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Assets list
            item { SectionHeader("ASSETS & LIABILITIES") }

            if (assets.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Tap + to add assets and liabilities", style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted))
                    }
                }
            } else {
                items(assets, key = { it.id }) { asset ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                        .background(if (asset.isLiability) ExpenseRed.copy(0.1f) else IncomeGreen.copy(0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (asset.isLiability) Icons.Rounded.CreditCard else Icons.Rounded.Savings,
                                        null,
                                        tint = if (asset.isLiability) ExpenseRed else IncomeGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(asset.name, style = MaterialTheme.typography.titleSmall)
                                    Text(asset.type.name.replace("_", " "), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Text(
                                "${if (asset.isLiability) "-" else "+"}$symbol${"%,.0f".format(asset.value)}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (asset.isLiability) ExpenseRed else IncomeGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── GOALS SCREEN ──

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: WealthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val goals = repository.getAllGoals().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val symbol = preferencesManager.defaultSymbol.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "₹")

    fun addGoal(goal: Goal) = viewModelScope.launch { repository.addGoal(goal) }
    fun deleteGoal(id: Long) = viewModelScope.launch { repository.deleteGoal(id) }
}

@Composable
fun GoalsScreen(onBack: () -> Unit, viewModel: GoalsViewModel = hiltViewModel()) {
    val goals by viewModel.goals.collectAsState()
    val symbol by viewModel.symbol.collectAsState()
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize().background(BackgroundDark).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBackIosNew, null, tint = TextPrimary) }
            Text("Financial Goals", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            IconButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }) {
                Icon(Icons.Rounded.Add, null, tint = GoldPrimary)
            }
        }

        if (goals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎯", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No goals yet", style = MaterialTheme.typography.bodyMedium)
                    Text("Tap + to set a financial goal", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(goals, key = { it.id }) { goal ->
                    GoldGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(goal.emoji, style = MaterialTheme.typography.headlineSmall)
                                    Column {
                                        Text(goal.title, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            "Target: $symbol${"%,.0f".format(goal.targetAmount)}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GoldPrimary)
                                        )
                                    }
                                }
                                if (goal.isCompleted) {
                                    Icon(Icons.Rounded.CheckCircle, null, tint = IncomeGreen, modifier = Modifier.size(24.dp))
                                }
                            }

                            LinearProgressIndicator(
                                progress = goal.progress,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = if (goal.isCompleted) IncomeGreen else GoldPrimary,
                                trackColor = BackgroundSurface
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    "$symbol${"%,.0f".format(goal.currentAmount)} saved",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "${(goal.progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
