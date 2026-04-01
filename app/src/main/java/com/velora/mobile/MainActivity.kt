package com.velora.mobile

import VeloraRoot
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
import com.velora.mobile.core.language.LocaleManager
import com.velora.mobile.ui.tokens.VeloraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleManager.ensureDefaultLanguage()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showLocaleOverlay by remember { mutableStateOf(false) }
            val languageCode = LocaleManager.currentLanguageCode()

            VeloraTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    VeloraRoot(
                        selectedLanguageCode = languageCode,
                        onLanguageSelected = { code ->
                            if (code == LocaleManager.currentLanguageCode()) return@VeloraRoot

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