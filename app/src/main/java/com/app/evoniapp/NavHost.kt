package com.app.evoniapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.evoniapp.navigation.Route
import com.app.evoniapp.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EvoniNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onToggleTheme: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        composable(Route.Splash.route) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val destination = if (currentUser != null) Route.Home.route else Route.Login.route
            SplashScreen(onFinished = { navController.navigate(destination) { popUpTo(Route.Splash.route) { inclusive = true } } })
        }
        composable(Route.Login.route) {
            LoginScreen(
                onLogin = { navController.navigate(Route.Home.route) { popUpTo(Route.Login.route) { inclusive = true } } },
                onRegister = { navController.navigate(Route.Register.route) }
            )
        }
        composable(Route.Register.route) {
            RegisterScreen(
                onRegistered = { navController.navigate(Route.Home.route) { popUpTo(Route.Register.route) { inclusive = true } } },
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }

        // --- FLUJO PRINCIPAL ---
        composable(Route.Home.route) {
            HomeScreen(
                modifier = Modifier.padding(paddingValues),
                onOpenProfile = { navController.navigate(Route.Profile.route) },
                onOpenAccount = { navController.navigate(Route.Settings.route) },
                onOpenTasks = { navController.navigate(Route.Tasks.route) },
                onOpenGuests = { navController.navigate(Route.Guests.route) },
                onOpenBudget = { navController.navigate(Route.Budget.route) },
                onOpenEvents = { navController.navigate(Route.Events.route) },
                onToggleTheme = onToggleTheme
            )
        }
        composable(Route.Profile.route) {
            ProfileScreen(
                modifier = Modifier.padding(paddingValues),
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Route.EditProfile.route) },
                onOpenAccount = { navController.navigate(Route.Settings.route) },
                onOpenEvents = { navController.navigate(Route.Events.route) }
            )
        }

        // --- SECCIÓN DE EVENTOS ---
        composable(Route.Events.route) {
            EventsScreen(navController)
        }
        composable(Route.AddEvent.route) {
            AddEventScreen(navController)
        }
        composable(
            route = Route.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            EventDetailScreen(
                navController = navController,
                eventId = eventId
            )
        }
        composable(
            route = Route.EditEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            EditEventScreen(
                navController = navController,
                eventId = eventId
            )
        }

        composable(Route.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(Route.Settings.route) {
            AccountConfigScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Route.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // --- SECCIÓN DE TAREAS ---
        composable(Route.Tasks.route) {
            TasksScreen(navController)
        }
        composable(Route.AddTask.route) {
            AddTaskScreen(navController)
        }
        composable(
            route = Route.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            EditTaskScreen(
                navController = navController,
                taskId = taskId
            )
        }

        // --- SECCIÓN DE PRESUPUESTO ---
        composable(Route.Budget.route) {
            BudgetScreen(navController)
        }
        composable(Route.AddBudget.route) {
            AddBudgetScreen(navController)
        }
        composable(
            route = Route.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.StringType })
        ) { backStackEntry ->
            BudgetDetailScreen(
                navController = navController,
                budgetId = backStackEntry.arguments?.getString("budgetId")
            )
        }
        composable(
            route = Route.EditBudget.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditBudgetScreen(
                navController = navController,
                budgetId = backStackEntry.arguments?.getString("budgetId")
            )
        }
        composable(
            route = Route.AddBudgetItem.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddBudgetItemScreen(
                navController = navController,
                budgetId = backStackEntry.arguments?.getString("budgetId")
            )
        }
        composable(
            route = Route.EditBudgetItem.route,
            arguments = listOf(navArgument("budgetItemId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditBudgetItemScreen(
                navController = navController,
                budgetItemId = backStackEntry.arguments?.getString("budgetItemId")
            )
        }

        // --- SECCIÓN DE INVITADOS ---
        composable(Route.Guests.route) {
            GuestsScreen(
                navController = navController
            )
        }
        composable(Route.AddGuest.route) {
            AddGuestScreen(
                navController = navController
            )
        }
        composable(
            route = Route.EditGuest.route,
            arguments = listOf(navArgument("guestId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditGuestScreen(
                navController = navController,
                guestId = backStackEntry.arguments?.getString("guestId")
            )
        }
    }
}
