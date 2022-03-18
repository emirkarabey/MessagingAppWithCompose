package com.example.messagingappwithcompose

sealed class Screen(val route:String){
    object LoginScreen : Screen("login_screen")
    object MessageScreen : Screen("message_screen")
}
