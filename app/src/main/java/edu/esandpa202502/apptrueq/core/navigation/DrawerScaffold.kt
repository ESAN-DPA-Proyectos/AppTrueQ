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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.esandpa202502.apptrueq.notification.viewmodel.SharedViewModel
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
fun DrawerScaffold(
    navController: NavController,
    sharedViewModel: SharedViewModel = viewModel(), // Se obtiene instancia del ViewModel compartido
    content: @Composable () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Se observa el contador de notificaciones no leídas
    val unreadCount by sharedViewModel.unreadNotificationCount.collectAsState()


    // La lista de items ahora se calcula dinámicamente
    val items = remember(unreadCount) {
        listOf(
            NavigationItem("Dashboard", Icons.Filled.Home, Icons.Outlined.Home, route = Routes.Dashboard.route),
            NavigationItem("Explorar", Icons.Filled.Search, Icons.Outlined.Search, route = Routes.Explore.route),
            NavigationItem("Login", Icons.Filled.Person, Icons.Outlined.Person, route = Routes.Login.route),
            NavigationItem("Oferta", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, route = Routes.Offer.route),
            NavigationItem("Necesidad", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, route = Routes.Need.route),
            NavigationItem("QR", Icons.Filled.QrCode, Icons.Outlined.QrCode, route = Routes.PublicationQR.route),
            NavigationItem("Historial", Icons.Filled.History, Icons.Outlined.History, route = Routes.TradeHistory.route),
            // SOLUCIÓN: Se pasa el contador de notificaciones al item correspondiente.
            NavigationItem("Notificaciones", Icons.Filled.Notifications, Icons.Outlined.Notifications, badgeCount = unreadCount, route = Routes.Notifications.route),
            NavigationItem("Reportar Usuario", Icons.Filled.Report, Icons.Outlined.Report, route = Routes.ReportUser.route),
            NavigationItem("Propuestas Recibidas", Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz, route = Routes.ProposalsReceived.route),
            NavigationItem("Cerrar Sesión", Icons.Filled.Logout, Icons.Outlined.Logout, route = Routes.Logout.route)
        )
    }

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
                                // SOLUCIÓN: Se envuelve el ícono de Notificaciones en un BadgedBox.
                                if (item.route == Routes.Notifications.route) {
                                    BadgedBox(badge = {
                                        if (item.badgeCount != null && item.badgeCount > 0) {
                                            Badge { Text(text = item.badgeCount.toString()) }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
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
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            // SOLUCIÓN: Se envuelve el ícono del menú en un BadgedBox.
                            BadgedBox(badge = {
                                if (unreadCount > 0) {
                                    Badge() // Un punto rojo si hay notificaciones
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                content()
            }
        }
    }
}