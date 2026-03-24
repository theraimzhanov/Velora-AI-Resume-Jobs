package com.example.velora

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.velora.core.language.LocaleManager
import com.example.velora.data.local.SettingsPreferences
import com.example.velora.navigation.VeloraNavHost
import com.example.velora.ui.tokens.VeloraTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsPrefs: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkMode by settingsPrefs.darkModeFlow.collectAsState(initial = false)
            val languageCode = LocaleManager.currentLanguageCode()

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
                        selectedLanguageCode = languageCode,
                        onLanguageSelected = { code ->
                            if (code == LocaleManager.currentLanguageCode()) {
                                return@VeloraNavHost
                            }
                            LocaleManager.applyAppLocale(code)
                        }
                    )
                }
            }
        }
    }
}