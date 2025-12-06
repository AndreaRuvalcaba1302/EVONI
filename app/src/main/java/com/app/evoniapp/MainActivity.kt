package com.app.evoniapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.evoniapp.navigation.Route
import com.app.evoniapp.ui.theme.EvoniAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EvoniApp()
        }
    }
}

@Composable
fun EvoniApp() {
    val navController = rememberNavController()
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val toggleTheme = { darkTheme = !darkTheme }

    EvoniAPPTheme(darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar(navController)) {
                    EvoniBottomBar(navController)
                }
            }
        ) { padding ->
            EvoniNavHost(navController = navController, paddingValues = padding, onToggleTheme = toggleTheme)
        }
    }
}

@Composable
private fun shouldShowBottomBar(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // Whitelist of routes that show the bottom bar
    val topLevelRoutes = setOf(
        Route.Home.route,
        Route.Tasks.route,
        Route.Guests.route,
        Route.Budget.route,
        Route.Events.route,
        Route.Profile.route
    )
    // Show bottom bar only for top-level destinations
    return currentRoute in topLevelRoutes
}
