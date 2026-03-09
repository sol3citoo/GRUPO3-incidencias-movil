package com.example.myproyecto.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    val proyectoSeleccionado by viewModel.proyectoSeleccionado.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val urgencias by viewModel.urgencias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    val context = LocalContext.current

    if (modo != "AÑADIR" && proyectoSeleccionado == null) {
        // Mensaje si no hay proyecto seleccionado
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tienes que seleccionar una incidencia de la lista")
        }
        return
    }

    // Variables de estado para los campos
    var titulo by remember { mutableStateOf(proyectoSeleccionado?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(proyectoSeleccionado?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(proyectoSeleccionado?.categoria ?: "") }
    var estado by remember { mutableStateOf(proyectoSeleccionado?.estado ?: "") }
    var urgencia by remember { mutableStateOf(proyectoSeleccionado?.urgencia ?: "") }
    var ubicacion by remember { mutableStateOf(proyectoSeleccionado?.ubicacion ?: "") }
    val id = proyectoSeleccionado?.id ?: 0
    val fecha = proyectoSeleccionado?.fecha ?: viewModel.obtenerFechaActual()

    val editable = modo != "VER"

    // Contenedor scrollable
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
                            proyectoSeleccionado?.copy(
                                titulo = titulo,
                                descripcion = descripcion,
                                categoria = categoria,
                                estado = estado,
                                urgencia = urgencia,
                                ubicacion = ubicacion,
                                fecha = fecha
                            ) ?: Proyecto(
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

        // 🔹 ID
        Text("ID Incidencia")
        OutlinedTextField(
            value = id.toString(),
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Título
        Text("Título")
        OutlinedTextField(
            value = titulo,
            onValueChange = { if (editable) titulo = it },
            enabled = editable,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Descripción
        Text("Descripción")
        OutlinedTextField(
            value = descripcion,
            onValueChange = { if (editable) descripcion = it },
            enabled = editable,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Categoría
        Text("Categoría")
        ComboBoxDynamic(
            options = categorias,
            selectedOption = categoria,
            editable = editable,
            onOptionSelected = { categoria = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Estado
        Text("Estado")
        ComboBoxDynamic(
            options = estados,
            selectedOption = estado,
            editable = editable,
            onOptionSelected = { estado = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Urgencia
        Text("Urgencia")
        ComboBoxDynamic(
            options = urgencias,
            selectedOption = urgencia,
            editable = editable,
            onOptionSelected = { urgencia = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Fecha
        Text("Fecha")
        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Ubicación (ahora ancho completo)
        Text("Ubicación")
        ComboBoxDynamic(
            options = ubicaciones,
            selectedOption = ubicacion,
            editable = editable,
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = editable) { expanded = true }
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