package com.example.velora.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val LANGUAGE_NAME = stringPreferencesKey("language_name")
    }

    val darkModeFlow: Flow<Boolean> =
        context.settingsDataStore.data.map { prefs ->
            prefs[Keys.DARK_MODE] ?: false
        }

    val languageCodeFlow: Flow<String> =
        context.settingsDataStore.data.map { prefs ->
            prefs[Keys.LANGUAGE_CODE] ?: "en"
        }

    val languageNameFlow: Flow<String> =
        context.settingsDataStore.data.map { prefs ->
            prefs[Keys.LANGUAGE_NAME] ?: "English"
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setLanguage(name: String, code: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.LANGUAGE_NAME] = name
            prefs[Keys.LANGUAGE_CODE] = code
        }
    }
}