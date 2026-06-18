package af.market.nerkhtimes

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import af.market.nerkhtimes.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootApp(vm: MarketViewModel) {
    val nav: NavHostController = rememberNavController()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val activity = ctx as? Activity

    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: NavRoutes.HOME

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    fun go(r: String) = scope.launch {
        drawerState.close()
        nav.navigate(r) {
            launchSingleTop = true
            restoreState = true
            popUpTo(NavRoutes.HOME) { saveState = true }
        }
    }

    fun shareApp() {
        val shareText =
            "NerkhTimes — د افغانستان د بازار نرخونه\n\n" +
                    "Download: https://play.google.com/store/apps/details?id=af.market.nerkhtimes"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        ctx.startActivity(Intent.createChooser(intent, stringResource(R.string.menu_share)))
    }

    fun exitApp() {
        scope.launch { drawerState.close() }
        activity?.finish()
    }

    val topTitle = when (route) {
        NavRoutes.HOME     -> stringResource(R.string.menu_home)
        NavRoutes.CONTACT  -> stringResource(R.string.menu_contact)
        NavRoutes.ABOUT    -> stringResource(R.string.menu_about)
        NavRoutes.CURRENCY -> stringResource(R.string.menu_currency)
        NavRoutes.METALS   -> stringResource(R.string.btn_metals)
        NavRoutes.GEMS     -> stringResource(R.string.btn_gems)
        NavRoutes.FOOD     -> stringResource(R.string.btn_food)
        NavRoutes.SON      -> stringResource(R.string.btn_son)
        NavRoutes.CHART    -> stringResource(R.string.btn_chart)
        else               -> stringResource(R.string.app_name)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.widthIn(max = 320.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_home)) },
                    selected = route == NavRoutes.HOME,
                    onClick = { go(NavRoutes.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_currency)) },
                    selected = route == NavRoutes.CURRENCY,
                    onClick = { go(NavRoutes.CURRENCY) },
                    icon = { Icon(Icons.Default.CurrencyExchange, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_contact)) },
                    selected = route == NavRoutes.CONTACT,
                    onClick = { go(NavRoutes.CONTACT) },
                    icon = { Icon(Icons.Default.Call, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_about)) },
                    selected = route == NavRoutes.ABOUT,
                    onClick = { go(NavRoutes.ABOUT) },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_share)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        shareApp()
                    },
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.menu_exit)) },
                    selected = false,
                    onClick = { exitApp() },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = topTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            AppNav(nav = nav, padding = padding, vm = vm)
        }
    }
}
