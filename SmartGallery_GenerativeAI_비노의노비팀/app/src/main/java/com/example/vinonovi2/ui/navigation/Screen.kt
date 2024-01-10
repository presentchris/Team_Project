package com.example.vinonovi2.ui.navigation

sealed class Screen(val route: String) {
//    object Login : Screen("login")
    object Gallery : Screen("gallery")
    object Upload : Screen("upload")
    object Search : Screen("search")
}