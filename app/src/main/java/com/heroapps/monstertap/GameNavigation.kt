package com.heroapps.monstertap

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun GameNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "gameMenu"
    ) {
        // Game Menu Screen
        composable("gameMenu") {
            GameMenuScreen(
                onStartGame = { difficulty ->
                    navController.navigate("gameScreen/${difficulty.name}")
                }
            )
        }
        
        // Game Screen
        composable(
            route = "gameScreen/{difficulty}",
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: Difficulty.Medium.name
            MonsterTapGame(difficulty = Difficulty.valueOf(difficulty))
        }
    }
}