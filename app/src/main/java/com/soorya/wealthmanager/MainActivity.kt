package com.soorya.wealthmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.*
import com.soorya.wealthmanager.ui.screens.addtransaction.AddTransactionScreen
import com.soorya.wealthmanager.ui.screens.dashboard.DashboardScreen
import com.soorya.wealthmanager.ui.screens.settings.SettingsScreen
import com.soorya.wealthmanager.ui.screens.splash.SplashScreen
import com.soorya.wealthmanager.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WealthManagerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
                    WealthManagerApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object AddTransaction : Screen("add_transaction")
    object Settings : Screen("settings")
    object NetWorth : Screen("net_worth")
    object Goals : Screen("goals")
    object Transactions : Screen("transactions")
}

@Composable
fun WealthManagerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onViewAll = { navController.navigate(Screen.Transactions.route) },
                onNetWorth = { navController.navigate(Screen.NetWorth.route) },
                onGoals = { navController.navigate(Screen.Goals.route) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            Screen.AddTransaction.route,
            enterTransition = { slideInVertically { it } + fadeIn() },
            exitTransition = { slideOutVertically { it } + fadeOut() }
        ) {
            AddTransactionScreen(
                onDismiss = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Transactions.route) {
            TransactionsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.NetWorth.route) {
            NetWorthScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Goals.route) {
            GoalsScreen(onBack = { navController.popBackStack() })
        }
    }
}
