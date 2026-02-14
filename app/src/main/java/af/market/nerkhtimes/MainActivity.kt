package af.market.nerkhtimes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import af.market.nerkhtimes.ui.theme.NerkhTimesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NerkhTimesTheme {
                val vm: MarketViewModel = viewModel()

                LaunchedEffect(Unit) {
                    vm.load()
                    vm.startAutoRefresh(60)
                }

                RootApp(vm = vm)
            }
        }
    }
}
