package com.velora.mobile.core.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {

    fun applyAppLocale(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
    }

    fun currentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return locales[0]?.language ?: "en"
    }
}