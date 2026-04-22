package com.example.myproyecto.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState as collectAsState1

@Composable
fun PantallaMenu(navController: NavHostController, viewModel: ProViewModel) {
    val usuario = viewModel.usuarioActual.collectAsState1().value
    val esAdmin = usuario?.tipo == "administrador"

    // Contenedor central
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido ${usuario?.correo}")
        Spacer(modifier = Modifier.height(106.dp))
        Text("Menú principal", modifier = Modifier.padding(bottom = 32.dp))

        //  Botón 1: Ver / Modificar incidencias
        Button(
            onClick = { navController.navigate("principal") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Ver / Modificar incidencias")
        }

        //  Botón 2: Añadir incidencias
        Button(
            onClick = { navController.navigate("detalle/AÑADIR") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Añadir incidencias")
        }

        //  Botón 3: Ver usuarios (solo administradores)
        Button(
            onClick = { navController.navigate("usuarios") },
            enabled = esAdmin,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Ver usuarios")
        }

        //  Botón 4: Cerrar sesión
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}