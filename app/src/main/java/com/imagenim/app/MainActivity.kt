package com.imagenim.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.imagenim.app.ui.navigation.NavGraph
import com.imagenim.app.ui.theme.ImageNimTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageNimTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
