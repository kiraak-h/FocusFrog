package com.example.focusfrog.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_SESSION_LENGTH = intPreferencesKey("selected_session_length")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val EQUIPPED_THEME = stringPreferencesKey("equipped_theme")
    }

    val selectedSessionLength: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_SESSION_LENGTH] ?: 25
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SOUND_ENABLED] ?: true
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    val equippedTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EQUIPPED_THEME] ?: "Pond"
    }

    suspend fun setSelectedSessionLength(lengthMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_SESSION_LENGTH] = lengthMinutes
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setEquippedTheme(themeName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EQUIPPED_THEME] = themeName
        }
    }
}
