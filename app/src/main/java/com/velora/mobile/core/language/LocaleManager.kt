package com.velora.mobile.core.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {

    private const val DEFAULT_LANGUAGE = "en"

    fun applyAppLocale(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
    }

    fun currentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return locales[0]?.language ?: DEFAULT_LANGUAGE
    }

    fun ensureDefaultLanguage() {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(DEFAULT_LANGUAGE)
            )
        }
    }
}