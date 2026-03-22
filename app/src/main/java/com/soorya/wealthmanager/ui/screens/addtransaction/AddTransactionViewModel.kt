package com.soorya.wealthmanager.ui.screens.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soorya.wealthmanager.data.repository.WealthRepository
import com.soorya.wealthmanager.domain.model.Transaction
import com.soorya.wealthmanager.domain.model.TransactionType
import com.soorya.wealthmanager.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val title: String = "",
    val category: String = "",
    val note: String = "",
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val suggestions: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: WealthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private var suggestionJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.defaultCurrency,
                preferencesManager.defaultSymbol
            ) { currency, symbol -> Pair(currency, symbol) }
                .collect { (currency, symbol) ->
                    _uiState.update { it.copy(currency = currency, currencySymbol = symbol) }
                }
        }
    }

    fun setType(type: TransactionType) {
        _uiState.update { it.copy(type = type, category = "") }
    }

    fun setAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun loadSuggestions(query: String) {
        suggestionJob?.cancel()
        if (query.length < 2) {
            _uiState.update { it.copy(suggestions = emptyList()) }
            return
        }
        suggestionJob = viewModelScope.launch {
            delay(300)
            val suggestions = repository.getSuggestions(query)
            _uiState.update { it.copy(suggestions = suggestions) }
        }
    }

    fun applySuggestion(title: String) {
        viewModelScope.launch {
            val last = repository.getLastByTitle(title)
            _uiState.update { state ->
                state.copy(
                    title = title,
                    category = last?.category ?: state.category,
                    amount = if (last != null) last.amount.toString() else state.amount,
                    suggestions = emptyList()
                )
            }
        }
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return

        if (state.category.isEmpty()) {
            _uiState.update { it.copy(error = "Please select a category") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val token = preferencesManager.notionToken.first()
            val dbId = preferencesManager.notionDbId.first()

            val transaction = Transaction(
                type = state.type,
                amount = amount,
                currency = state.currency,
                currencySymbol = state.currencySymbol,
                title = state.title.ifEmpty { state.category },
                category = state.category,
                note = state.note
            )

            repository.addTransaction(transaction, token, dbId)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
