package com.example.myproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myproyecto.Screens.PantallaDetalle
import com.example.myproyecto.Screens.PantallaLogin
import com.example.myproyecto.Screens.PantallaPrincipal
import com.example.myproyecto.Screens.ProViewModel
import com.example.myproyecto.ui.theme.MyProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyProyectoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val viewModel: ProViewModel = viewModel()
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        // Pantalla de login
                        composable("login") {
                            PantallaLogin(navController, viewModel)
                        }

                        // Pantalla principal con lista de proyectos
                        composable("principal") {
                            PantallaPrincipal(navController, viewModel)
                        }

                        // Pantalla detalle (VER, MODIFICAR o AÑADIR) - modo se pasa como String
                        composable("detalle/{modo}") { backStackEntry ->
                            val modo = backStackEntry.arguments?.getString("modo") ?: "VER"
                            PantallaDetalle(
                                navController = navController,
                                viewModel = viewModel,
                                modo = modo
                            )
                        }
                    }
                }
            }
        }
    }
}


