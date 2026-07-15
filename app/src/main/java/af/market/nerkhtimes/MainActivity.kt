package af.market.nerkhtimes

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import af.market.nerkhtimes.ui.theme.NerkhTimesTheme

class MainActivity : ComponentActivity() {

    /** Apply the user-selected locale before any resource resolution happens. */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate() — swaps the splash theme for the app theme
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hasLanguage = LanguageManager.hasLanguage(this)

        setContent {
            NerkhTimesTheme {
                val vm: MarketViewModel = viewModel()

                // If a locale change was triggered (from language picker), re-process item names.
                LaunchedEffect(Unit) {
                    if (vm.consumeLocaleChangePending()) {
                        vm.refreshLocale()
                    }
                }

                if (!hasLanguage) {
                    // First launch: mandatory language selection before the main app.
                    LanguageSelectionScreen(
                        currentCode = null,
                        isFirstLaunch = true,
                        onLanguageSelected = { langCode ->
                            LanguageManager.saveLanguage(this@MainActivity, langCode)
                            vm.markLocaleChangePending()
                            recreate()
                        }
                    )
                } else {
                    RootApp(vm = vm)
                }
            }
        }
    }
}
