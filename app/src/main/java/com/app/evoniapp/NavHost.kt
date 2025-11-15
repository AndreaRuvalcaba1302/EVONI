package com.app.evoniapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.evoniapp.navigation.Route
import com.app.evoniapp.ui.screens.*

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
            SplashScreen(onFinished = { navController.navigate(Route.Login.route) { popUpTo(Route.Splash.route) { inclusive = true } } })
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
                onOpenGuests = { /* TODO */ },
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

        composable(Route.Events.route) {
            EventsScreen(navController)
        }

        composable(Route.AddEvent.route) {
            AddEventScreen(navController)
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
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Route.Tasks.route) {
            TasksScreen(navController)
        }

        composable(Route.AddTask.route) {
            AddTaskScreen(navController)
        }

        composable(Route.Budget.route) {
            BudgetScreen(navController)
        }

        composable(Route.AddBudget.route) {
            AddBudgetScreen(navController)
        }
    }
}
