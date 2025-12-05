package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(navController: NavController, content: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        NavigationItem(
            "Dashboard",
            Icons.Filled.Home,
            Icons.Outlined.Home,
            route = Routes.Dashboard.route
        ),
        NavigationItem(
            "Explorar",
            Icons.Filled.Search,
            Icons.Outlined.Search,
            route = Routes.Explore.route
        ),
        NavigationItem(
            "Login",
            Icons.Filled.Person,
            Icons.Outlined.Person,
            route = Routes.Login.route
        ),
        // NavigationItem("Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, route = Routes.Profile.route),
        NavigationItem(
            "Oferta",
            Icons.Filled.ShoppingCart,      // Ícono para ofertas
            Icons.Outlined.ShoppingCart,
            route = Routes.Offer.route
        ),
        NavigationItem(
            "Necesidad",
            Icons.Filled.Help,              // Ícono para necesidades
            Icons.Outlined.Help,
            route = Routes.Need.route
        ),
        NavigationItem(
            "Historial",
            Icons.Filled.History,
            Icons.Outlined.History,
            route = Routes.TradeHistory.route
        ),
        NavigationItem(
            "Notificaciones",
            Icons.Filled.Notifications,
            Icons.Outlined.Notifications,
            route = Routes.Notifications.route
        ),
        NavigationItem(
            "Reportar Usuario",
            Icons.Filled.Report,
            Icons.Outlined.Report,
            route = Routes.ReportUser.route
        ),
        NavigationItem(
            "Propuestas Recibidas",
            Icons.Filled.SwapHoriz,
            Icons.Outlined.SwapHoriz,
            route = Routes.ProposalsReceived.route
        ),
        NavigationItem(
            "Cerrar Sesión",
            Icons.Filled.Logout,
            Icons.Outlined.Logout,
            route = Routes.Logout.route
        )
    )

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEach { item ->
                        val isSelected = navController.currentDestination?.route == item.route
                        NavigationDrawerItem(
                            label = { Text(text = item.title) },
                            selected = isSelected,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            badge = {
                                item.badgeCount?.let {
                                    Text(text = it.toString())
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "AppTrueQ") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}
