package edu.esandpa202502.apptrueq.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    sharedViewModel: SharedViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val drawerState = androidx.compose.material3.rememberDrawerState(
        initialValue = androidx.compose.material3.DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    // Contador de notificaciones no leídas (versión main)
    val unreadCount by sharedViewModel.unreadNotificationCount.collectAsState()

    // Lista de items combinando HU-11 + main
    val items = remember(unreadCount) {
        listOf(
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
            NavigationItem(
                "Oferta",
                Icons.Filled.ShoppingCart,        // icono de HU-11
                Icons.Outlined.ShoppingCart,
                route = Routes.Offer.route
            ),
            NavigationItem(
                "Necesidad",
                Icons.AutoMirrored.Filled.Help,                // icono de HU-11
                Icons.AutoMirrored.Outlined.Help,
                route = Routes.Need.route
            ),
            NavigationItem(
                "QR",
                Icons.Filled.QrCode,
                Icons.Outlined.QrCode,
                route = Routes.PublicationQR.route   // asumiendo que ya tienes esta ruta
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
                badgeCount = unreadCount,           // badge dinámico
                route = Routes.Notifications.route
            ),
            NavigationItem(
                "Reportar Usuario",
                Icons.Filled.Report,
                Icons.Outlined.Report,
                route = Routes.ReportUser.route     // OJO: esta ruta requiere args, ver nota abajo
            ),
            NavigationItem(
                "Moderador",
                Icons.Filled.AdminPanelSettings,
                Icons.Outlined.AdminPanelSettings,
                route = Routes.ModerationPanel.route
            ),
            NavigationItem(
                "Propuestas Recibidas",
                Icons.Filled.SwapHoriz,
                Icons.Outlined.SwapHoriz,
                route = Routes.ProposalsReceived.route
            ),
            NavigationItem(
                "Cerrar Sesión",
                Icons.AutoMirrored.Filled.Logout,
                Icons.AutoMirrored.Outlined.Logout,
                route = Routes.Logout.route
            )
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
                                // Ítem de notificaciones con Badge (versión main)
                                if (item.route == Routes.Notifications.route) {
                                    BadgedBox(
                                        badge = {
                                            if (item.badgeCount != null && item.badgeCount > 0) {
                                                Badge { Text(text = item.badgeCount.toString()) }
                                            }
                                        }
                                    ) {
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
                            modifier = Modifier.padding(
                                NavigationDrawerItemDefaults.ItemPadding
                            )
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
                            scope.launch { drawerState.open() }
                        }) {
                            // Badge en el icono del menú si hay notificaciones (versión main)
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
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
