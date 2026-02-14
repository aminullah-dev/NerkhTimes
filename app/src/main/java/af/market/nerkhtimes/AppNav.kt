package af.market.nerkhtimes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import af.market.nerkhtimes.feature.home.*
import af.market.nerkhtimes.navigation.NavRoutes

@Composable
fun AppNav(
    nav: NavHostController,
    padding: PaddingValues,
    vm: MarketViewModel
) {
    NavHost(
        navController = nav,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) { HomeScreen(padding = padding, nav = nav, vm = vm) }
        composable(NavRoutes.METALS) { GoldSilverScreen(padding = padding, vm = vm) }
        composable(NavRoutes.GEMS) { GemsScreen(padding = padding, vm = vm) }
        composable(NavRoutes.FOOD) { FoodScreen(padding = padding, vm = vm) }
        composable(NavRoutes.SON) { SonScreen(padding = padding, vm = vm) }
        composable(NavRoutes.CHART) { ChartScreen(padding = padding, vm = vm) }

        composable(NavRoutes.ABOUT) { AboutScreen(padding = padding) }
        composable(NavRoutes.CONTACT) { ContactScreen(padding = padding) }
    }
}
