package com.example.myproyecto.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    viewModel: ProViewModelAPI
) {
    val incidencias by viewModel.incidencias.collectAsState()
    val usuario by viewModel.usuarioActual.collectAsState()
    val incidenciaSeleccionada by viewModel.incidenciaSeleccionada.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val urgencias by viewModel.urgencias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    val ids by viewModel.ids.collectAsState()

    val context = LocalContext.current
    val esAdmin = usuario?.rolId == 2

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarOpciones()
        viewModel.cargarFiltrados()
    }

    // Estados de los filtros
    var selectedEstado by remember { mutableStateOf("-") }
    var selectedUrgencia by remember { mutableStateOf("-") }
    var selectedUbicacion by remember { mutableStateOf("-") }

    val incidenciaSel = incidenciaSeleccionada
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

    // Listas para ComboBox
    val listaCategorias = listOf("-") + categorias.map { it.nombre }
    val listaEstados = listOf("-") + estados
    val listaUrgencias = listOf("-") + urgencias
    val listaUbicaciones = listOf("-") + ubicaciones.map { it.nombre }

    @Composable
    fun ComboBox(
        label: String,
        options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        Column(Modifier.padding(vertical = 4.dp)) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onOptionSelected(option); expanded = false }
                    )
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Filtros
        ComboBox("Estado", listaEstados, selectedEstado) { selectedEstado = it }
        ComboBox("Urgencia", listaUrgencias, selectedUrgencia) { selectedUrgencia = it }
        ComboBox("Ubicación", listaUbicaciones, selectedUbicacion) { selectedUbicacion = it }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                navController.navigate("menu") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
            }) { Text("Salir") }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                viewModel.cargarFiltrados(
                    estados = if (selectedEstado == "-") emptyList() else listOf(selectedEstado),
                    urgencias = if (selectedUrgencia == "-") emptyList() else listOf(
                        selectedUrgencia
                    ),
                    ubicaciones = if (selectedUbicacion == "-") emptyList()
                    else listOf(selectedUbicacion)
                )
            }) { Text("Cargar") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Lista de Incidencias")
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(incidencias) { incidencia ->
                val color = if (incidenciaSel?.id == incidencia.id)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.background

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .clickable { viewModel.seleccionarIncidencia(incidencia) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(incidencia.id.toString())
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(incidencia.titulo)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botones de acción
        Row {
            Button(
                onClick = { mostrarDialogoBorrar = true },
                enabled = esAdmin && incidenciaSel != null
            ) { Text("Borrar") }

            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = {
                if (incidenciaSel == null) {
                    Toast.makeText(context, "Selecciona una incidencia", Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate("detalle/VER")
                }
            }) { Text("Ver") }

            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = {
                if (incidenciaSel == null) {
                    Toast.makeText(context, "Selecciona una incidencia", Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate("detalle/MODIFICAR")
                }
            }) { Text("Modificar") }
        }
    }

    // MODIFICAR
    if (mostrarDialogoBorrar && incidenciaSel != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Cerrar la incidencia \"${incidenciaSel.titulo}\"?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.cambiarEstadoIncidencia(incidenciaSel.id, "Cerrada")
                    mostrarDialogoBorrar = false
                }) { Text("Sí") }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogoBorrar = false }) { Text("No") }
            }
        )
    }
}