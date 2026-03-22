package com.soorya.wealthmanager.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wealth_settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val NOTION_TOKEN = stringPreferencesKey("notion_token")
        val NOTION_DB_ID = stringPreferencesKey("notion_db_id")
        val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
        val DEFAULT_SYMBOL = stringPreferencesKey("default_symbol")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val notionToken: Flow<String> = context.dataStore.data.map { it[Keys.NOTION_TOKEN] ?: "" }
    val notionDbId: Flow<String> = context.dataStore.data.map { it[Keys.NOTION_DB_ID] ?: "" }
    val defaultCurrency: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_CURRENCY] ?: "INR" }
    val defaultSymbol: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_SYMBOL] ?: "₹" }
    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.HAPTICS_ENABLED] ?: true }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    suspend fun saveNotionSettings(token: String, dbId: String) {
        context.dataStore.edit {
            it[Keys.NOTION_TOKEN] = token
            it[Keys.NOTION_DB_ID] = dbId
        }
    }

    suspend fun saveDefaultCurrency(code: String, symbol: String) {
        context.dataStore.edit {
            it[Keys.DEFAULT_CURRENCY] = code
            it[Keys.DEFAULT_SYMBOL] = symbol
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setOnboardingDone() {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = true }
    }
}
