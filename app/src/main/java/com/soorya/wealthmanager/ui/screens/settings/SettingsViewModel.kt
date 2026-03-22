package com.soorya.wealthmanager.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soorya.wealthmanager.data.repository.WealthRepository
import com.soorya.wealthmanager.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notionToken: String = "",
    val notionDbId: String = "",
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val hapticsEnabled: Boolean = true,
    val isTesting: Boolean = false,
    val connectionStatus: Boolean? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val repository: WealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.notionToken,
                preferencesManager.notionDbId,
                preferencesManager.defaultCurrency,
                preferencesManager.defaultSymbol,
                preferencesManager.hapticsEnabled
            ) { token, dbId, currency, symbol, haptics ->
                SettingsUiState(
                    notionToken = token,
                    notionDbId = dbId,
                    currency = currency,
                    currencySymbol = symbol,
                    hapticsEnabled = haptics
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setToken(token: String) = _uiState.update { it.copy(notionToken = token, connectionStatus = null) }
    fun setDbId(dbId: String) = _uiState.update { it.copy(notionDbId = dbId, connectionStatus = null) }
    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setHapticsEnabled(enabled) }
    }

    fun saveAndTest() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isTesting = true, connectionStatus = null) }
            preferencesManager.saveNotionSettings(state.notionToken.trim(), state.notionDbId.trim())
            val result = repository.testNotionConnection(state.notionToken.trim(), state.notionDbId.trim())
            _uiState.update { it.copy(isTesting = false, connectionStatus = result) }
        }
    }
}
