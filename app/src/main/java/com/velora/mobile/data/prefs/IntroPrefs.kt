package com.velora.mobile.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("velora_prefs")

class IntroPrefs(private val context: Context) {
    private val KEY_DONE = booleanPreferencesKey("intro_done")

    val introDone: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_DONE] ?: false }

    suspend fun setIntroDone(value: Boolean) {
        context.dataStore.edit { it[KEY_DONE] = value }
    }
}