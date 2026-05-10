package com.example.myproyecto.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PantallaDetalle(
    navController: NavHostController,
    viewModel: ProViewModelAPI,
    modo: String // "VER", "MODIFICAR" o "AÑADIR"
) {
    LaunchedEffect(Unit) {
        viewModel.cargarOpciones()
    }

    val incidenciaSeleccionada by viewModel.incidenciaSeleccionada.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val urgencias by viewModel.urgencias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    val usuario by viewModel.usuarioActual.collectAsState()
    val context = LocalContext.current
    val esAdmin = usuario?.rolId == 2

    if (modo != "AÑADIR" && incidenciaSeleccionada == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tienes que seleccionar una incidencia de la lista")
        }
        return
    }

    // Campos editables — se inicializan con los datos de la incidencia seleccionado
    var titulo by remember { mutableStateOf(incidenciaSeleccionada?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(incidenciaSeleccionada?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(incidenciaSeleccionada?.categoria ?: "") }
    var estado by remember {
        mutableStateOf(
            if (modo == "AÑADIR") "Abierta" else incidenciaSeleccionada?.estado ?: ""
        )
    }
    var urgencia by remember { mutableStateOf(incidenciaSeleccionada?.urgencia ?: "") }
    var ubicacion by remember { mutableStateOf(incidenciaSeleccionada?.ubicacion ?: "") }
    val fecha = incidenciaSeleccionada?.fecha ?: viewModel.obtenerFechaActual()

    // Permisos de edición
    val editableCampos = when {
        modo == "VER" -> false
        modo == "AÑADIR" -> true
        modo == "MODIFICAR" && esAdmin -> true
        else -> false
    }
    val estadoEditable = modo == "MODIFICAR"

    val estadosDisponibles = if (esAdmin) estados
    else estados.filter { it.lowercase() != "cerrada" }

    // Nombres de categorías y ubicaciones para los ComboBox
    val nombresCategorias = categorias.map { it.nombre }
    val nombresUbicaciones = ubicaciones.map { it.nombre }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { navController.popBackStack() }) { Text("Volver") }

            if (modo != "VER") {
                Button(onClick = {
                    if (titulo.isBlank() || descripcion.isBlank() || categoria.isBlank() ||
                        estado.isBlank() || urgencia.isBlank() || ubicacion.isBlank()
                    ) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    // Buscar los IDs a partir del nombre seleccionado
                    val categoriaId = categorias.firstOrNull { it.nombre == categoria }?.id
                    val ubicacionId = ubicaciones.firstOrNull { it.nombre == ubicacion }?.id

                    if (categoriaId == null || ubicacionId == null) {
                        Toast.makeText(
                            context,
                            "Categoría o ubicación no válida",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (modo == "AÑADIR") {
                        viewModel.crearIncidencia(
                            titulo,
                            descripcion,
                            categoriaId,
                            urgencia,
                            ubicacionId
                        )
                    } else {
                        val id = incidenciaSeleccionada!!.id
                        viewModel.editarIncidencia(
                            id,
                            titulo,
                            descripcion,
                            categoriaId,
                            urgencia,
                            ubicacionId
                        )
                        // Si el estado cambió, actualizarlo por separado
                        if (estado != incidenciaSeleccionada!!.estado) {
                            viewModel.cambiarEstadoIncidencia(id, estado)
                        }
                    }

                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) { Text("Guardar") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ID (solo lectura)
        Text("ID Incidencia")
        OutlinedTextField(
            value = if (modo == "AÑADIR") "—" else (incidenciaSeleccionada?.id ?: "").toString(),
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Bloqueado") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título
        Text("Título")
        OutlinedTextField(
            value = titulo,
            onValueChange = { if (editableCampos) titulo = it },
            enabled = editableCampos,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (!editableCampos) Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Bloqueado"
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descripción
        Text("Descripción")
        OutlinedTextField(
            value = descripcion,
            onValueChange = { if (editableCampos) descripcion = it },
            enabled = editableCampos,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            trailingIcon = {
                if (!editableCampos) Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Bloqueado"
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Categoría
        Text("Categoría")
        ComboBoxDynamic(
            options = nombresCategorias,
            selectedOption = categoria,
            editable = editableCampos,
            onOptionSelected = { categoria = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado
        Text("Estado")
        if (modo == "AÑADIR") {
            OutlinedTextField(
                value = "Abierta",
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Bloqueado") },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        } else {
            ComboBoxDynamic(
                options = estadosDisponibles,
                selectedOption = estado,
                editable = estadoEditable,
                onOptionSelected = { estado = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Urgencia
        Text("Urgencia")
        ComboBoxDynamic(
            options = urgencias,
            selectedOption = urgencia,
            editable = editableCampos,
            onOptionSelected = { urgencia = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Fecha (solo lectura)
        Text("Fecha")
        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Bloqueado") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ubicación
        Text("Ubicación")
        ComboBoxDynamic(
            options = nombresUbicaciones,
            selectedOption = ubicacion,
            editable = editableCampos,
            onOptionSelected = { ubicacion = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ComboBoxDynamic(
    options: List<String>,
    selectedOption: String,
    editable: Boolean,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            trailingIcon = {
                if (!editable) Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Bloqueado"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = editable) { expanded = true },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
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