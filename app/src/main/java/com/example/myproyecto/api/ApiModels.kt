package com.example.myproyecto.api

import com.google.gson.annotations.SerializedName

// ─── LOGIN ────────────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("contraseña") val contrasena: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UsuarioResponse
)

// ─── USUARIOS ─────────────────────────────────────────────────────────────────

data class UsuarioResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("rolId") val rolId: Int
)

data class UsuarioListItem(
    @SerializedName("Id") val id: Int,
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Email") val email: String,
    @SerializedName("Rol") val rol: String
)

data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("contraseña") val contrasena: String,
    @SerializedName("rolId") val rolId: Int? = null
)

// ─── INCIDENCIAS ──────────────────────────────────────────────────────────────

data class IncidenciaResponse(
    @SerializedName("Id") val id: Int,
    @SerializedName("Titulo") val titulo: String,
    @SerializedName("Descripcion") val descripcion: String,
    @SerializedName("Categoria") val categoria: String,
    @SerializedName("Estado") val estado: String,
    @SerializedName("Urgencia") val urgencia: String,
    @SerializedName("Ubicacion") val ubicacion: String,
    @SerializedName("Fecha") val fecha: String,
    @SerializedName("Abierto") val abierto: Int
)

data class CrearIncidenciaRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoriaId") val categoriaId: Int,
    @SerializedName("urgencia") val urgencia: String,
    @SerializedName("ubicacionId") val ubicacionId: Int
)

data class EditarIncidenciaRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoriaId") val categoriaId: Int,
    @SerializedName("urgencia") val urgencia: String,
    @SerializedName("ubicacionId") val ubicacionId: Int
)

data class FiltroIncidenciasRequest(
    @SerializedName("estados") val estados: List<String> = emptyList(),
    @SerializedName("fecha") val fecha: String? = null,
    @SerializedName("ubicaciones") val ubicaciones: List<String> = emptyList(),
    @SerializedName("urgencias") val urgencias: List<String> = emptyList(),
    @SerializedName("abierto") val abierto: Boolean? = null
)

data class CambiarEstadoRequest(
    @SerializedName("estado") val estado: String
)

//OPCIONES (listas para ComboBox)

data class CategoriaResponse(
    @SerializedName("Id") val id: Int,
    @SerializedName("Nombre") val nombre: String
)

data class UbicacionResponse(
    @SerializedName("Id") val id: Int,
    @SerializedName("Nombre") val nombre: String
)

data class RolResponse(
    @SerializedName("Id") val id: Int,
    @SerializedName("Nombre") val nombre: String
)


data class UrgenciaResponse(
    @SerializedName("Urgencia") val urgencia: String
)

data class EstadoResponse(
    @SerializedName("Estado") val estado: String
)

data class MessageResponse(
    @SerializedName("message") val message: String
)