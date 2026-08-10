package com.vault39.app

sealed class Screen(val route: String) {
    object Login       : Screen("login")
    object Accounts    : Screen("accounts")
    object AddAccount  : Screen("add_account")
    object Backup      : Screen("backup")
    object Detail      : Screen("detail/{accountId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
    object EditAccount : Screen("edit/{accountId}") {
        fun createRoute(id: Long) = "edit/$id"
    }
}
