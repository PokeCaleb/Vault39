package com.vault39.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vault39.app.theme.MikuTheme
import com.vault39.app.ui.accounts.AccountsScreen
import com.vault39.app.ui.add.AddAccountScreen
import com.vault39.app.ui.backup.BackupScreen
import com.vault39.app.ui.detail.DetailScreen
import com.vault39.app.ui.edit.EditAccountScreen
import com.vault39.app.ui.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MikuTheme {
                Vault39NavGraph()
            }
        }
    }
}

@Composable
fun Vault39NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Accounts.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Accounts.route) {
            AccountsScreen(
                onAccountClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onAddAccount   = { navController.navigate(Screen.AddAccount.route) },
                onOpenBackup   = { navController.navigate(Screen.Backup.route) }
            )
        }

        composable(Screen.AddAccount.route) {
            AddAccountScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Backup.route) {
            BackupScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("accountId") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("accountId") ?: return@composable
            DetailScreen(
                accountId      = accountId,
                onNavigateBack = { navController.popBackStack() },
                onEditAccount  = { id -> navController.navigate(Screen.EditAccount.createRoute(id)) }
            )
        }

        composable(
            route = Screen.EditAccount.route,
            arguments = listOf(navArgument("accountId") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("accountId") ?: return@composable
            EditAccountScreen(
                accountId      = accountId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
