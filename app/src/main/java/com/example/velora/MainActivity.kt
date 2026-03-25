package com.example.velora

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.velora.core.language.LocaleManager
import com.example.velora.data.local.SettingsPreferences
import com.example.velora.presentation.app.VeloraRoot
import com.example.velora.ui.tokens.VeloraTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
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

            var showLocaleOverlay by remember { mutableStateOf(false) }

            VeloraTheme(darkTheme = darkMode) {
                Box(modifier = Modifier.fillMaxSize()) {

                    VeloraRoot(
                        darkMode = darkMode,
                        onDarkModeChange = { enabled ->
                            lifecycleScope.launch {
                                settingsPrefs.setDarkMode(enabled)
                            }
                        },
                        selectedLanguageCode = languageCode,
                        onLanguageSelected = { code ->
                            if (code == LocaleManager.currentLanguageCode()) {
                                return@VeloraRoot
                            }

                            lifecycleScope.launch {
                                showLocaleOverlay = true
                                delay(120)
                                LocaleManager.applyAppLocale(code)
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = showLocaleOverlay,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        }
    }
}