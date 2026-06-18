package af.market.nerkhtimes

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    private const val PREFS_NAME = "lang_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANG_EN = "en"
    const val LANG_PS = "ps"
    const val LANG_FA = "fa"

    fun getSavedLanguage(ctx: Context): String? =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, null)

    fun saveLanguage(ctx: Context, langCode: String) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    fun hasLanguage(ctx: Context): Boolean = getSavedLanguage(ctx) != null

    /**
     * Wraps [ctx] with a locale-configured context.
     * Falls back to English when no preference is saved.
     * Called from MainActivity.attachBaseContext — must be synchronous.
     */
    fun wrapContext(ctx: Context): Context {
        val lang = getSavedLanguage(ctx) ?: LANG_EN
        return applyToContext(ctx, lang)
    }

    fun applyToContext(ctx: Context, langCode: String): Context {
        val locale = Locale(langCode)
        val config = Configuration(ctx.resources.configuration)
        config.setLocale(locale)
        return ctx.createConfigurationContext(config)
    }
}
