package com.example.myproyecto.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.myproyecto.R


@Composable
fun PantallaLogin(navController: NavHostController, viewModel: ProViewModel) {

    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        //  CABECERA AZUL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0)) // Azul profesional
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🖼 Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // <-- nombre de tu logo
                contentDescription = "Logo",
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            // Texto blanco
            Text(
                text = "Bienvenido a la app de incidencias",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //  FORMULARIO LOGIN
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text("INICIAR SESIÓN")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contraseña,
                    onValueChange = { contraseña = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Lock
                        else
                            Icons.Filled.AccountCircle

                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (correo.isBlank() || contraseña.isBlank()) {
                        error = "Campos vacíos"
                    } else {
                        error = ""
                        viewModel.login(correo.trim(), contraseña.trim())
                    }
                }) {
                    Text("Entrar")
                }

                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red)
                }

                when (loginState) {
                    is LoginState.Error ->
                        Text("Usuario o contraseña incorrectos", color = Color.Red)
                    else -> {}
                }
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate("menu") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}
