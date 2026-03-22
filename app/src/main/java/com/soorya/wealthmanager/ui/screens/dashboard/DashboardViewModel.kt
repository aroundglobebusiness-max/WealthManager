package com.soorya.wealthmanager.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soorya.wealthmanager.data.repository.WealthRepository
import com.soorya.wealthmanager.domain.model.*
import com.soorya.wealthmanager.domain.usecase.GetWealthScoreUseCase
import com.soorya.wealthmanager.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val wealthScore: WealthScore? = null,
    val netWorth: NetWorth = NetWorth(0.0, 0.0),
    val currencySymbol: String = "₹",
    val isSyncing: Boolean = false,
    val syncMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WealthRepository,
    private val preferencesManager: PreferencesManager,
    private val getWealthScoreUseCase: GetWealthScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val notionToken: Flow<String> = preferencesManager.notionToken
    val notionDbId: Flow<String> = preferencesManager.notionDbId

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endOfMonth = calendar.timeInMillis
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            val startOfMonth = calendar.timeInMillis

            combine(
                repository.getTotalIncome(),
                repository.getTotalExpense(),
                repository.getIncomeByRange(startOfMonth, endOfMonth),
                repository.getExpenseByRange(startOfMonth, endOfMonth),
                repository.getRecentTransactions(),
                repository.getTotalAssets(),
                repository.getTotalLiabilities(),
                repository.getAllGoals(),
                preferencesManager.defaultSymbol
            ) { values ->
                val income = values[0] as Double
                val expense = values[1] as Double
                val monthlyIncome = values[2] as Double
                val monthlyExpense = values[3] as Double
                @Suppress("UNCHECKED_CAST")
                val transactions = values[4] as List<Transaction>
                val assets = values[5] as Double
                val liabilities = values[6] as Double
                @Suppress("UNCHECKED_CAST")
                val goals = values[7] as List<Goal>
                val symbol = values[8] as String

                val avgGoalProgress = if (goals.isEmpty()) 0f
                else goals.map { it.progress }.average().toFloat()

                val wealthScore = getWealthScoreUseCase(
                    totalIncome = income,
                    totalExpense = expense,
                    totalAssets = assets,
                    totalLiabilities = liabilities,
                    goalProgress = avgGoalProgress
                )

                DashboardUiState(
                    totalIncome = income,
                    totalExpense = expense,
                    balance = income - expense,
                    monthlyIncome = monthlyIncome,
                    monthlyExpense = monthlyExpense,
                    recentTransactions = transactions,
                    wealthScore = wealthScore,
                    netWorth = NetWorth(assets, liabilities),
                    currencySymbol = symbol
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun syncToNotion() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncMessage = null) }
            val token = preferencesManager.notionToken.first()
            val dbId = preferencesManager.notionDbId.first()
            val count = repository.syncPendingTransactions(token, dbId)
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    syncMessage = if (count > 0) "✓ Synced $count transactions" else "All synced!"
                )
            }
        }
    }

    fun dismissSyncMessage() {
        _uiState.update { it.copy(syncMessage = null) }
    }
}
