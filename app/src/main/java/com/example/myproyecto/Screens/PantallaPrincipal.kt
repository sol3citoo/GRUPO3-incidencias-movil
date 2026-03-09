package com.example.myproyecto.Screens

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
import com.example.myproyecto.data.Proyecto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    viewModel: ProViewModel
) {
    val proyectos by viewModel.proyectos.collectAsState()
    val usuario by viewModel.usuarioActual.collectAsState()
    val proyectoSeleccionado by viewModel.proyectoSeleccionado.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val urgencias by viewModel.urgencias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()

    val context = LocalContext.current
    val esAdmin = usuario?.tipo == "administrador"

    // 🔹 Cargar datos iniciales
    LaunchedEffect(Unit) {
        viewModel.cargarFiltrados("-", "-", "-", "-", "-")
        viewModel.cargarOpciones() // llena filtros dinámicos
    }

    // 🔹 Estados de filtros
    var selectedId by remember { mutableStateOf("-") }
    var selectedCategoria by remember { mutableStateOf("-") }
    var selectedEstado by remember { mutableStateOf("-") }
    var selectedUrgencia by remember { mutableStateOf("-") }
    var selectedUbicacion by remember { mutableStateOf("-") }

    val proyectoSel = proyectoSeleccionado
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

    // 🔹 Lista de opciones dinámicas
    val listaIds = listOf("-") + proyectos.map { it.id.toString() }
    val listaCategorias = listOf("-") + categorias.distinct()
    val listaEstados = listOf("-") + estados.distinct()
    val listaUrgencias = listOf("-") + urgencias.distinct()
    val listaUbicaciones = listOf("-") + ubicaciones.distinct()

    // 🔹 ComboBox reutilizable
    @Composable
    fun ComboBox(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
        var expanded by remember { mutableStateOf(false) }
        Column(Modifier.padding(vertical = 4.dp)) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth().clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    // 🔹 UI principal
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bienvenido ${usuario?.correo}")
        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Botones superiores
        Row {
            Button(onClick = {
                viewModel.logout()
                navController.navigate("login") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
            }) { Text("Salir") }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                viewModel.cargarFiltrados(selectedId, selectedCategoria, selectedEstado, selectedUrgencia, selectedUbicacion)
            }) { Text("Cargar") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Filtros ComboBox dinámicos
        ComboBox("ID Incidencia", listaIds, selectedId) { selectedId = it }
        ComboBox("Categoría", listaCategorias, selectedCategoria) { selectedCategoria = it }
        ComboBox("Estado", listaEstados, selectedEstado) { selectedEstado = it }
        ComboBox("Urgencia", listaUrgencias, selectedUrgencia) { selectedUrgencia = it }
        ComboBox("Ubicación", listaUbicaciones, selectedUbicacion) { selectedUbicacion = it }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Botones de acción
        Row {
            Button(
                onClick = { mostrarDialogoBorrar = true },
                enabled = esAdmin && proyectoSel != null
            ) { Text("Borrar") }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = { navController.navigate("detalle/AÑADIR") },
                enabled = esAdmin
            ) { Text("Crear") }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    if (proyectoSel == null) {
                        Toast.makeText(context, "Tienes que seleccionar una incidencia de la lista", Toast.LENGTH_SHORT).show()
                    } else {
                        val modo = if (esAdmin) "MODIFICAR" else "VER"
                        navController.navigate("detalle/$modo")
                    }
                },
                enabled = true
            ) { Text(if (esAdmin) "Modificar" else "Ver") }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Lista de Proyectos")
        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Lista de proyectos debajo de los botones
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(proyectos) { proyecto ->
                val color = if (proyectoSel?.id == proyecto.id) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.background
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .clickable { viewModel.seleccionarProyecto(proyecto) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(proyecto.id.toString())
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(proyecto.titulo)
                }
            }
        }
    }

    // 🔹 Diálogo de confirmación de borrado
    if (mostrarDialogoBorrar && proyectoSel != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Desea borrar el proyecto \"${proyectoSel.titulo}\"?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.borrarProyecto(proyectoSel)
                    mostrarDialogoBorrar = false
                }) { Text("Sí") }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogoBorrar = false }) { Text("No") }
            }
        )
    }
}

