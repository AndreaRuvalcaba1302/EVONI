package com.app.evoniapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
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

    // Task Flow
    data object Tasks : Route("tasks")
    data object AddTask : Route("add_task")
    data object EditTask : Route("edit_task/{taskId}")

    // Event Flow
    data object AddEvent : Route("add_event")
    data object EventDetail : Route("event_detail/{eventId}")
    data object EditEvent : Route("edit_event/{eventId}")

    // Budget Flow
    data object Budget : Route("budget")
    data object AddBudget : Route("add_budget")
    data object BudgetDetail : Route("budget_detail/{budgetId}")
    data object EditBudget : Route("edit_budget/{budgetId}") // Ya no es eventName
    data object AddBudgetItem : Route("add_budget_item/{budgetId}")
    data object EditBudgetItem : Route("edit_budget_item/{budgetItemId}")

    // Guest Flow
    data object Guests : Route("guests")
    data object AddGuest : Route("add_guest")
    data object EditGuest : Route("edit_guest/{guestId}")
}
