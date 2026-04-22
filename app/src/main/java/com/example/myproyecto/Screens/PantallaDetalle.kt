package com.example.myproyecto.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myproyecto.data.Proyecto
import androidx.compose.ui.Alignment

@Composable
fun PantallaDetalle(
    navController: NavHostController,
    viewModel: ProViewModel,
    modo: String // "VER", "MODIFICAR" o "AÑADIR"
) {
    LaunchedEffect(Unit) {
        viewModel.cargarOpciones()
       // viewModel.cargarUltimoIdProyecto()
    }

    val proyectoSeleccionado by viewModel.proyectoSeleccionado.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val urgencias by viewModel.urgencias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    val context = LocalContext.current
    val proximoId = viewModel.ultimoIdProyecto.value
    val usuario by viewModel.usuarioActual.collectAsState()
    val esAdmin = usuario?.tipo == "administrador"

    if (modo != "AÑADIR" && proyectoSeleccionado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tienes que seleccionar una incidencia de la lista")
        }
        return
    }

    //  Variables de estado
    var titulo by remember { mutableStateOf(proyectoSeleccionado?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(proyectoSeleccionado?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(proyectoSeleccionado?.categoria ?: "") }
    var estado by remember { mutableStateOf(if (modo == "AÑADIR") "Abierta" else proyectoSeleccionado?.estado ?: "") }
    var urgencia by remember { mutableStateOf(proyectoSeleccionado?.urgencia ?: "") }
    var ubicacion by remember { mutableStateOf(proyectoSeleccionado?.ubicacion ?: "") }
    val fecha = proyectoSeleccionado?.fecha ?: viewModel.obtenerFechaActual()

    //  Control de edición según usuario y modo
    val editableCampos = when {
        modo == "VER" -> false
        modo == "AÑADIR" -> true
        modo == "MODIFICAR" && esAdmin -> true
        else -> false
    }

    val estadoEditable = when {
        modo == "AÑADIR" -> false
        modo == "MODIFICAR" -> true
        else -> false
    }

    //  Filtrar estados según permisos del usuario
    val estadosDisponibles = if (esAdmin || modo == "AÑADIR") {
        estados
    } else {
        estados.filter { it.lowercase() != "cerrada" }
    }

    //  Contenedor scrollable
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
                    if (titulo.isBlank() || descripcion.isBlank() || categoria.isBlank() || estado.isBlank() || urgencia.isBlank() || ubicacion.isBlank()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (modo == "AÑADIR") {
                        viewModel.crearProyecto(
                            Proyecto(
                                titulo = titulo,
                                descripcion = descripcion,
                                categoria = categoria,
                                estado = estado,
                                urgencia = urgencia,
                                ubicacion = ubicacion,
                                fecha = fecha
                            )
                        )
                    } else {
                        viewModel.modificarProyecto(
                            proyectoSeleccionado!!.copy(
                                titulo = titulo,
                                descripcion = descripcion,
                                categoria = categoria,
                                estado = estado,
                                urgencia = urgencia,
                                ubicacion = ubicacion
                            )
                        )
                    }

                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) { Text("Guardar") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  ID
        Text("ID Incidencia")
        val idMostrar = if (modo == "AÑADIR") (proximoId + 1).toString() else (proyectoSeleccionado?.id ?: "").toString()
        OutlinedTextField(
            value = idMostrar,
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

        //  Título
        Text("Título")
        OutlinedTextField(
            value = titulo,
            onValueChange = { if (editableCampos) titulo = it },
            enabled = editableCampos,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { if (!editableCampos) Icon(Icons.Filled.Lock, contentDescription = "Bloqueado") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Descripción
        Text("Descripción")
        OutlinedTextField(
            value = descripcion,
            onValueChange = { if (editableCampos) descripcion = it },
            enabled = editableCampos,
            modifier = Modifier.fillMaxWidth().height(100.dp),
            trailingIcon = { if (!editableCampos) Icon(Icons.Filled.Lock, contentDescription = "Bloqueado") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Categoría
        Text("Categoría")
        ComboBoxDynamic(
            options = categorias,
            selectedOption = categoria,
            editable = editableCampos,
            onOptionSelected = { categoria = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Estado
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

        //  Urgencia
        Text("Urgencia")
        ComboBoxDynamic(
            options = urgencias,
            selectedOption = urgencia,
            editable = editableCampos,
            onOptionSelected = { urgencia = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Fecha
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

        //  Ubicación
        Text("Ubicación")
        ComboBoxDynamic(
            options = ubicaciones,
            selectedOption = ubicacion,
            editable = editableCampos,
            onOptionSelected = { ubicacion = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

//  ComboBox dinámico con bloqueo
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
            trailingIcon = { if (!editable) Icon(Icons.Filled.Lock, contentDescription = "Bloqueado") },
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
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}