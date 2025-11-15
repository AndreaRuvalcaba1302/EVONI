package com.app.evoniapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController 
import com.app.evoniapp.navigation.Route
import com.app.evoniapp.ui.theme.EvoniAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // The state for dark mode is managed within EvoniApp
            EvoniApp()
        }
    }
}

@Composable
fun EvoniApp() {
    val navController = rememberNavController()
    var darkTheme by remember { mutableStateOf(false) }
    val toggleTheme = { darkTheme = !darkTheme }

    EvoniAPPTheme(darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar(navController)) {
                    // Assuming EvoniBottomBar doesn't need the toggle function
                    EvoniBottomBar(navController)
                }
            }
        ) { padding ->
            // Pass the toggle function down to the NavHost
            EvoniNavHost(navController = navController, paddingValues = padding, onToggleTheme = toggleTheme)
        }
    }
}

@Composable
private fun shouldShowBottomBar(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    return currentRoute != Route.Splash.route &&
            currentRoute != Route.Login.route &&
            currentRoute != Route.Register.route
}
