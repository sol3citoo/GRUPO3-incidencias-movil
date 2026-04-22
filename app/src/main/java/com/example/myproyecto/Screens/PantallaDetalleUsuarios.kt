package com.example.myproyecto.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myproyecto.data.Usuarios

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleUsuarios(
    navController: NavHostController,
    viewModel: ProViewModel,
    modo: String // "VER", "MODIFICAR" o "CREAR"
) {
    val context = LocalContext.current

    //  Datos del usuario seleccionado
    val usuarioSeleccionado by viewModel.usuarioSeleccionado.collectAsState()
    val ultimoId by viewModel.ultimoIdUsuario.collectAsState(initial = 0)

    //  IdUsuario
    val idUsuario = if (modo == "CREAR") ultimoId + 1 else usuarioSeleccionado?.id ?: 0

    //  Campos locales
    var correo by remember { mutableStateOf(usuarioSeleccionado?.correo ?: "") }
    var contraseña by remember { mutableStateOf(usuarioSeleccionado?.contraseña ?: "") }
    var tipo by remember { mutableStateOf(usuarioSeleccionado?.tipo ?: "") }

    //  Editable según modo
    val editableCorreo = modo == "CREAR"
    val editableContraseña = modo != "VER"
    val editableTipo = modo != "VER"

    //  Tipos disponibles
    val tiposDisponibles by viewModel.tiposUsuarios.collectAsState(initial = listOf())
    var expandedTipo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = when (modo) {
                "VER" -> "Ver usuario"
                "MODIFICAR" -> "Modificar usuario"
                "CREAR" -> "Crear usuario"
                else -> "Detalle usuario"
            },
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        //  IdUsuario (solo lectura)
        OutlinedTextField(
            value = idUsuario.toString(),
            onValueChange = {},
            label = { Text("ID Usuario") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { if (editableCorreo) correo = it },
            label = { Text("Correo") },
            readOnly = !editableCorreo,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Contraseña
        OutlinedTextField(
            value = contraseña,
            onValueChange = { if (editableContraseña) contraseña = it },
            label = { Text("Contraseña") },
            readOnly = !editableContraseña,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //  Tipo (ComboBox funcional)
        Box {
            OutlinedTextField(
                value = tipo,
                onValueChange = {},
                label = { Text("Tipo") },
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = editableTipo) { expandedTipo = true }
            )
            DropdownMenu(
                expanded = expandedTipo,
                onDismissRequest = { expandedTipo = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                tiposDisponibles.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            tipo = t
                            expandedTipo = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  Botones en columna
        Column(modifier = Modifier.fillMaxWidth()) {

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("Volver")
            }

            if (modo != "VER") {
                Button(
                    onClick = {
                        //  Validación
                        if (correo.isBlank() || contraseña.isBlank() || tipo.isBlank()) {
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        when (modo) {
                            "MODIFICAR" -> {
                                usuarioSeleccionado?.let { usuario ->
                                    val actualizado = usuario.copy(
                                        contraseña = contraseña,
                                        tipo = tipo
                                    )
                                    viewModel.actualizarUsuario(actualizado)
                                    Toast.makeText(context, "Usuario modificado", Toast.LENGTH_SHORT).show()
                                }
                            }
                            "CREAR" -> {
                                val nuevo = Usuarios(
                                    id = idUsuario,
                                    correo = correo,
                                    contraseña = contraseña,
                                    tipo = tipo
                                )
                                viewModel.crearUsuario(nuevo)
                                Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                            }
                        }

                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}