package com.app.evoniapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
// --- CORRECCIÓN AQUÍ ---
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomBarRoute(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomBarRoute("home", "Inicio", Icons.Filled.Home)
    data object Events : BottomBarRoute("events", "Eventos", Icons.Filled.DateRange)
    data object Profile : BottomBarRoute("profile", "Perfil", Icons.Filled.Person)
}


sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Login : Route("login")
    data object Register : Route("register")


    data object Home : Route(BottomBarRoute.Home.route)
    data object Events : Route(BottomBarRoute.Events.route)
    data object Profile : Route(BottomBarRoute.Profile.route)


    data object EditProfile : Route("edit_profile")
    data object Settings : Route("settings")
    data object Tasks : Route("tasks")
    data object AddTask : Route("add_task")
    data object Budget : Route("budget")
    data object AddBudget : Route("add_budget")
    data object GuestList : Route("guest_list")
    data object AddEvent : Route("add_event")
    // Aquí puedes añadan sus rutas, como "Tasks", "Guests", "Budget", etc.
}
