package com.example.myproyecto.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myproyecto.data.Usuarios

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuarios(
    navController: NavHostController,
    viewModel: ProViewModel
) {
    //  Estados de lista y selección

    val usuarios by viewModel.usuarios.collectAsState(initial = emptyList())
    val usuarioSeleccionado by viewModel.usuarioSeleccionado.collectAsState()
    val idsUsuarios by viewModel.idsUsuarios.collectAsState()
    val correos by viewModel.correos.collectAsState()
    val tiposUsuarios by viewModel.tiposUsuarios.collectAsState()

    var selectedId by remember { mutableStateOf("-") }
    var selectedCorreo by remember { mutableStateOf("-") }
    var selectedTipo by remember { mutableStateOf("-") }
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

    //  Carga inicial
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios() // carga usuarios y llena ComboBox
    }

    //  Listas dinámicas para ComboBox
    val listaIds = listOf("-") + idsUsuarios
    val listaCorreos = listOf("-") + correos
    val listaTipos = listOf("-") + tiposUsuarios

    //  ComboBox reutilizable
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
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

    //  UI principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {



        //  Filtros ComboBox
        ComboBox("ID Usuario", listaIds, selectedId) { selectedId = it }
        ComboBox("Correo", listaCorreos, selectedCorreo) { selectedCorreo = it }
        ComboBox("Tipo", listaTipos, selectedTipo) { selectedTipo = it }

        Spacer(modifier = Modifier.height(16.dp))

        //  Botones superiores
        Row {
            Button(onClick = { navController.popBackStack() }) { Text("Volver") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                viewModel.cargarUsuariosFiltrados(
                    selectedId,
                    selectedCorreo,
                    selectedTipo
                )
            }) { Text("Cargar") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Lista de usuarios")

        Spacer(modifier = Modifier.height(8.dp))

        //  Lista seleccionable
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(usuarios) { usuario ->
                val color = if (usuarioSeleccionado?.id == usuario.id)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.background

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.seleccionarUsuario(usuario) }
                        .background(color)
                        .padding(8.dp)
                ) {
                    Text(usuario.id.toString(), modifier = Modifier.width(40.dp))
                    Text(usuario.correo, modifier = Modifier.weight(1f))
                    Text(usuario.tipo, modifier = Modifier.width(120.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  Botones inferiores en columna
        Column(modifier = Modifier.fillMaxWidth()) {

            Button(
                onClick = { mostrarDialogoBorrar = true },
                enabled = usuarioSeleccionado != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("Borrar usuario")
            }

            Button(
                onClick = {
                    if (usuarioSeleccionado != null) {
                        viewModel.seleccionarUsuario(usuarioSeleccionado!!)
                        navController.navigate("detalleUsuario/MODIFICAR")
                    }
                },
                enabled = usuarioSeleccionado != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("Modificar usuario")
            }

            Button(
                onClick = {
                    navController.navigate("detalleUsuario/CREAR")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("Crear usuario")
            }
        }

    //  Diálogo de confirmación de borrado
    usuarioSeleccionado?.let { usuario ->
        if (mostrarDialogoBorrar) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoBorrar = false },
                title = { Text("Confirmar borrado") },
                text = { Text("¿Borrar usuario ${usuario.correo}?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.borrarUsuario(usuario)
                        mostrarDialogoBorrar = false
                    }) { Text("Sí") }
                },
                dismissButton = {
                    Button(onClick = { mostrarDialogoBorrar = false }) { Text("No") }
                }
            )
        }
    }
}}