package com.example.velora

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.velora.core.language.LocaleManager
import com.example.velora.data.local.SettingsPreferences
import com.example.velora.navigation.VeloraNavHost
import com.example.velora.ui.tokens.VeloraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("language_bootstrap", MODE_PRIVATE)
        val languageCode = prefs.getString("language_code", "en") ?: "en"
        val wrapped = LocaleManager.setLocale(newBase, languageCode)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsPrefs = remember { SettingsPreferences(applicationContext) }

            val darkMode by settingsPrefs.darkModeFlow.collectAsState(initial = false)
            val languageName by settingsPrefs.languageNameFlow.collectAsState(initial = "English")

            VeloraTheme(darkTheme = darkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    VeloraNavHost(
                        modifier = Modifier.padding(innerPadding),
                        darkMode = darkMode,
                        onDarkModeChange = { enabled ->
                            lifecycleScope.launch {
                                settingsPrefs.setDarkMode(enabled)
                            }
                        },
                        selectedLanguage = languageName,
                        onLanguageSelected = { name, code ->
                            lifecycleScope.launch {
                                settingsPrefs.setLanguage(name, code)
                            }

                            getSharedPreferences("language_bootstrap", MODE_PRIVATE)
                                .edit()
                                .putString("language_code", code)
                                .apply()

                            LocaleManager.updateLocale(this, code)
                        }
                    )
                }
            }
        }
    }
}