package com.example.myproyecto.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleUsuarios(
    navController: NavHostController,
    viewModel: ProViewModelAPI,
    modo: String // "MODIFICAR" o "CREAR"
) {
    val context = LocalContext.current
    val usuarioSeleccionado by viewModel.usuarioSeleccionado.collectAsState()
    val roles by viewModel.roles.collectAsState()

    // Campos locales
    var nombre by remember { mutableStateOf(
        if (modo == "CREAR") ""
        else usuarioSeleccionado?.nombre?: ""
    ) }
    var correo by remember { mutableStateOf(
        if (modo == "CREAR") ""
        else usuarioSeleccionado?.email?: ""
    ) }

    var contraseña by remember { mutableStateOf("") }

    var rolNombre by remember {
        mutableStateOf(
            if (modo == "CREAR") ""
            else roles.firstOrNull { it.id == usuarioSeleccionado?.id }?.nombre ?: ""
        )
    }

    LaunchedEffect(Unit) {
        viewModel.cargarOpciones()
    }

    val editableCorreo = modo == "CREAR"
    val editableNombre = modo == "CREAR"
    val editableContraseña = true
    val editableRol = true

    var expandedRol by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (modo == "CREAR") "Crear usuario" else "Modificar usuario",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ID (solo lectura en MODIFICAR, oculto en CREAR)
        if (modo == "MODIFICAR") {
            OutlinedTextField(
                value = usuarioSeleccionado?.id?.toString() ?: "",
                onValueChange = {},
                label = { Text("ID Usuario") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { if (editableNombre) nombre = it },
            label = { Text("Nombre") },
            readOnly = !editableNombre,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Correo / Email
        OutlinedTextField(
            value = correo,
            onValueChange = { if (editableCorreo) correo = it },
            label = { Text("Correo") },
            readOnly = !editableCorreo,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contraseña
        OutlinedTextField(
            value = contraseña,
            onValueChange = { contraseña = it },
            label = { Text(if (modo == "MODIFICAR") "Nueva contraseña" else "Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rol
        Box {
            OutlinedTextField(
                value = rolNombre,
                onValueChange = {},
                label = { Text("Rol") },
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = editableRol) { expandedRol = true }
            )
            DropdownMenu(
                expanded = expandedRol,
                onDismissRequest = { expandedRol = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                roles.forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol.nombre) },
                        onClick = { rolNombre = rol.nombre; expandedRol = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) { Text("Volver") }

            Button(
                onClick = {
                    // Validación
                    if (correo.isBlank() || contraseña.isBlank() || rolNombre.isBlank()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (modo == "CREAR" && nombre.isBlank()) {
                        Toast.makeText(context, "Rellena el nombre", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val rolId = roles.firstOrNull { it.nombre == rolNombre }?.id

                    when (modo) {
                        "CREAR" -> {
                            viewModel.crearUsuario(nombre, correo, contraseña, rolId)
                            Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                        }

                        "MODIFICAR" -> {
                            //TODO: Enpoint modificar
                            Toast.makeText(
                                context,
                                "Endpoint PUT /usuarios/:id pendiente en el backend",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) { Text("Guardar") }
        }
    }
}