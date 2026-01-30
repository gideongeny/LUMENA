package com.dn0ne.player.core.data

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {

    private const val LANGUAGE_KEY = "language"

    /**
     * Set the app locale and return the new Locale configuration
     */
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = if (languageCode.isEmpty()) {
            Locale.getDefault()
        } else {
            Locale(languageCode)
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    /**
     * Get the saved language code
     */
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "") ?: ""
    }

    /**
     * Save the language code
     */
    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    /**
     * Get the system default locale
     */
    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale.getDefault()
        } else {
            @Suppress("DEPRECATION")
            Locale.getDefault()
        }
    }

    /**
     * Get locale from language code
     */
    fun getLocaleFromCode(languageCode: String): Locale {
        return if (languageCode.isEmpty()) {
            getSystemLocale()
        } else {
            Locale(languageCode)
        }
    }
}
