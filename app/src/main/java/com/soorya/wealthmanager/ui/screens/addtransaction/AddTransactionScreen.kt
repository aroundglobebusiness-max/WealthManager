package com.soorya.wealthmanager.ui.screens.addtransaction

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.soorya.wealthmanager.domain.model.TransactionType
import com.soorya.wealthmanager.ui.components.GlassCard
import com.soorya.wealthmanager.ui.theme.*

val expenseCategories = listOf(
    "🛵 Petrol", "🍽 Food", "🛒 Shopping", "🚌 Transport",
    "💊 Health", "🏠 Rent", "📱 Bills", "🎮 Entertainment",
    "✈️ Travel", "📚 Education", "💸 Transfer", "📦 Other"
)

val incomeCategories = listOf(
    "💼 Salary", "🧑‍💻 Freelance", "📈 Investment",
    "🎁 Gift", "💰 Refund", "🏦 Business", "📦 Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onDismiss: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundCard, BackgroundDark)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(GoldPrimary.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .clickable(enabled = false) {}
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(GoldPrimary.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Log Transaction",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Type Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BackgroundSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TransactionType.values().forEach { type ->
                        val selected = uiState.type == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) when (type) {
                                        TransactionType.EXPENSE -> ExpenseRed.copy(alpha = 0.2f)
                                        TransactionType.INCOME -> IncomeGreen.copy(alpha = 0.2f)
                                    } else Color.Transparent
                                )
                                .border(
                                    width = if (selected) 1.dp else 0.dp,
                                    color = if (selected) when (type) {
                                        TransactionType.EXPENSE -> ExpenseRed
                                        TransactionType.INCOME -> IncomeGreen
                                    } else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.setType(type)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (type == TransactionType.EXPENSE) "↓ Expense" else "↑ Income",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = if (selected) when (type) {
                                        TransactionType.EXPENSE -> ExpenseRed
                                        TransactionType.INCOME -> IncomeGreen
                                    } else TextMuted,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundSurface)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        uiState.currencySymbol,
                        style = MaterialTheme.typography.headlineMedium.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = viewModel::setAmount,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeholder = { Text("0", style = MaterialTheme.typography.headlineMedium.copy(color = TextMuted)) },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Text(uiState.currency, style = MaterialTheme.typography.labelMedium.copy(color = TextMuted))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title with Smart Suggestions
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = {
                        viewModel.setTitle(it)
                        viewModel.loadSuggestions(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("What was this for?") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary.copy(alpha = 0.5f),
                        unfocusedBorderColor = BackgroundSurface,
                        focusedContainerColor = BackgroundSurface,
                        unfocusedContainerColor = BackgroundSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = {
                        Icon(Icons.Rounded.Edit, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true
                )

                // Suggestions
                AnimatedVisibility(visible = uiState.suggestions.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.suggestions) { suggestion ->
                            SuggestionChip(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.applySuggestion(suggestion)
                                },
                                label = { Text(suggestion, style = MaterialTheme.typography.labelMedium) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = GoldSubtle,
                                    labelColor = GoldPrimary
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = GoldPrimary.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = viewModel::setNote,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add a note (optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary.copy(alpha = 0.5f),
                        unfocusedBorderColor = BackgroundSurface,
                        focusedContainerColor = BackgroundSurface,
                        unfocusedContainerColor = BackgroundSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = {
                        Icon(Icons.Rounded.Notes, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categories
                Text("Category", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                Spacer(modifier = Modifier.height(8.dp))

                val cats = if (uiState.type == TransactionType.EXPENSE) expenseCategories else incomeCategories
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cats.forEach { cat ->
                        val selected = uiState.category == cat
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setCategory(cat)
                            },
                            label = { Text(cat, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GoldPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = GoldPrimary.copy(alpha = 0.5f),
                                borderColor = BackgroundSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.save(onSuccess = onDismiss)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BackgroundDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isSaving && uiState.amount.isNotEmpty() && uiState.category.isNotEmpty()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BackgroundDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Save Transaction →",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BackgroundDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
