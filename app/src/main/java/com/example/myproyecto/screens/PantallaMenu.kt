package com.example.myproyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PantallaMenu(navController: NavHostController, viewModel: ProViewModelAPI) {
    val usuario by viewModel.usuarioActual.collectAsState()
    val esAdmin = usuario?.rolId == 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido, ${usuario?.nombre ?: ""}")
        Spacer(modifier = Modifier.height(106.dp))
        Text("Menú principal", modifier = Modifier.padding(bottom = 32.dp))

        Button(
            onClick = { navController.navigate("principal") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("Ver / Modificar incidencias") }

        Button(
            onClick = { navController.navigate("detalle/AÑADIR") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("Añadir incidencias") }

        Button(
            onClick = { navController.navigate("usuarios") },
            enabled = esAdmin,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("Ver usuarios") }

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("Cerrar sesión") }
    }
}