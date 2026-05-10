package com.example.myproyecto.screens

import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myproyecto.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

// ─── Estado de login ──────────────────────────────────────────────────────────
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UsuarioResponse) : LoginState()
    object Error : LoginState()
}

// ─── ViewModel ────────────────────────────────────────────────────────────────
class ProViewModelAPI(application: Application) : AndroidViewModel(application) {

    // API
    private val api = RetrofitClient.create(application.applicationContext)
    private val ctx = application.applicationContext

    //Usuario actual
    private val _usuarioActual = MutableStateFlow<UsuarioResponse?>(null)
    val usuarioActual: StateFlow<UsuarioResponse?> = _usuarioActual

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        viewModelScope.launch {
            val token = TokenStorage.get(ctx)
            if (token != null) {
                try {
                    val payloadBase64 = token.split(".")[1]
                    val padded = payloadBase64.padEnd(
                        payloadBase64.length + (4 - payloadBase64.length % 4) % 4, '='
                    )
                    val json = org.json.JSONObject(
                        String(android.util.Base64.decode(padded, android.util.Base64.DEFAULT))
                    )
                    val usuario = UsuarioResponse(
                        id = json.getInt("id"),
                        nombre = json.getString("nombre"),
                        email = json.getString("email"),
                        rolId = json.getInt("rolId")
                    )
                    _usuarioActual.value = usuario
                    _loginState.value = LoginState.Success(usuario)
                } catch (e: Exception) {
                    TokenStorage.clear(ctx)
                    _loginState.value = LoginState.Idle
                }
            } else {
                _loginState.value = LoginState.Idle
            }
        }
    }

    //Incidencias
    private val _incidencias = MutableStateFlow<List<IncidenciaResponse>>(emptyList())
    val incidencias: StateFlow<List<IncidenciaResponse>> = _incidencias

    private val _incidenciaSeleccionada = MutableStateFlow<IncidenciaResponse?>(null)
    val incidenciaSeleccionada: StateFlow<IncidenciaResponse?> = _incidenciaSeleccionada

    private val _categorias = MutableStateFlow<List<CategoriaResponse>>(emptyList())
    val categorias: StateFlow<List<CategoriaResponse>> = _categorias

    private val _estados = MutableStateFlow<List<String>>(emptyList())
    val estados: StateFlow<List<String>> = _estados

    private val _urgencias = MutableStateFlow<List<String>>(emptyList())
    val urgencias: StateFlow<List<String>> = _urgencias

    private val _ubicaciones = MutableStateFlow<List<UbicacionResponse>>(emptyList())
    val ubicaciones: StateFlow<List<UbicacionResponse>> = _ubicaciones

    private val _roles = MutableStateFlow<List<RolResponse>>(emptyList())
    val roles: StateFlow<List<RolResponse>> = _roles

    // IDs de incidencias
    private val _ids = MutableStateFlow<List<String>>(emptyList())
    val ids: StateFlow<List<String>> = _ids

    //Usuarios
    private val _todosUsuarios = MutableStateFlow<List<UsuarioListItem>>(emptyList())
    private val _usuarios = MutableStateFlow<List<UsuarioListItem>>(emptyList())
    val usuarios: StateFlow<List<UsuarioListItem>> = _usuarios

    private val _usuarioSeleccionado = MutableStateFlow<UsuarioListItem?>(null)
    val usuarioSeleccionado: StateFlow<UsuarioListItem?> = _usuarioSeleccionado

    val idsUsuarios = MutableStateFlow<List<String>>(emptyList())
    val correos = MutableStateFlow<List<String>>(emptyList())
    val tiposUsuarios = MutableStateFlow<List<String>>(emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // =========================================================================
    // LOGIN / LOGOUT
    // =========================================================================

    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = api.login(LoginRequest(email, contrasena))
                println("RESPONSE:")
                println(response)
                TokenStorage.save(ctx, response.token)
                _usuarioActual.value = response.user
                _loginState.value = LoginState.Success(response.user)
                cargarFiltrados()
            } catch (e: Exception) {
                println(e.message)
                _loginState.value = LoginState.Error
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            TokenStorage.clear(ctx)
            _usuarioActual.value = null
            _incidencias.value = emptyList()
            _incidenciaSeleccionada.value = null
            _loginState.value = LoginState.Idle
        }
    }

    // =========================================================================
    // INCIDENCIAS
    // =========================================================================

    fun cargarFiltrados(
        estados: List<String> = emptyList(),
        urgencias: List<String> = emptyList(),
        ubicaciones: List<String> = emptyList(),
        fecha: String? = null,
        abierto: Boolean? = null
    ) {
        viewModelScope.launch {
            try {
                val filtro =
                    FiltroIncidenciasRequest(estados, fecha, ubicaciones, urgencias, abierto)
                _incidencias.value = api.filtrarIncidencias(filtro)
                _ids.value = _incidencias.value.map { it.id.toString() }
                _incidenciaSeleccionada.value = null
            } catch (e: Exception) {
                println(e)
                _error.value = "Error al cargar incidencias: ${e.message}"
            }
        }
    }

    fun seleccionarIncidencia(incidencia: IncidenciaResponse?) {
        _incidenciaSeleccionada.value = incidencia
    }

    fun crearIncidencia(
        titulo: String,
        descripcion: String,
        categoriaId: Int,
        urgencia: String,
        ubicacionId: Int
    ) {
        viewModelScope.launch {
            try {
                api.crearIncidencia(
                    CrearIncidenciaRequest(titulo, descripcion, categoriaId, urgencia, ubicacionId)
                )
                cargarFiltrados()
            } catch (e: Exception) {
                _error.value = "Error al crear incidencia: ${e.message}"
            }
        }
    }

    fun editarIncidencia(
        id: Int,
        titulo: String,
        descripcion: String,
        categoriaId: Int,
        urgencia: String,
        ubicacionId: Int
    ) {
        viewModelScope.launch {
            try {
                api.editarIncidencia(
                    id,
                    EditarIncidenciaRequest(titulo, descripcion, categoriaId, urgencia, ubicacionId)
                )
                cargarFiltrados()
            } catch (e: Exception) {
                _error.value = "Error al editar incidencia: ${e.message}"
            }
        }
    }

    fun cambiarEstadoIncidencia(id: Int, estado: String) {
        viewModelScope.launch {
            try {
                api.cambiarEstado(id, CambiarEstadoRequest(estado))
                _incidencias.value = _incidencias.value.map {
                    if (it.id == id) it.copy(estado = estado) else it
                }
            } catch (e: Exception) {
                _error.value = "Error al cambiar estado: ${e.message}"
            }
        }
    }

    // =========================================================================
    // OPCIONES
    // =========================================================================

    fun cargarOpciones() {
        viewModelScope.launch {
            try {
                _categorias.value = api.getCategorias()
                _ubicaciones.value = api.getUbicaciones()
                _urgencias.value = api.getUrgencias().map { it.urgencia }
                _estados.value = api.getEstados().map { it.estado }
                _roles.value = api.getRoles()
            } catch (e: Exception) {
                _error.value = "Error al cargar opciones: ${e.message}"
            }
        }
    }

    // =========================================================================
    // USUARIOS
    // =========================================================================

    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                val lista = api.getUsuarios()
                _todosUsuarios.value = lista
                _usuarios.value = lista
                actualizarOpcionesUsuarios(lista)
            } catch (e: Exception) {
                _error.value = "Error al cargar usuarios: ${e.message}"
            }
        }
    }

    private fun actualizarOpcionesUsuarios(lista: List<UsuarioListItem>) {
        idsUsuarios.value = lista.map { it.id.toString() }
        correos.value = lista.map { it.email }
        tiposUsuarios.value = lista.map { it.rol }.distinct()
    }

    fun seleccionarUsuario(usuario: UsuarioListItem) {
        _usuarioSeleccionado.value = usuario
    }

    fun cargarUsuariosFiltrados(id: String, correo: String, tipo: String) {
        _usuarios.value = _todosUsuarios.value.filter {
            (id == "-"     || it.id.toString() == id) &&
                    (correo == "-" || it.email == correo) &&
                    (tipo == "-"   || it.rol == tipo)
        }
    }

    fun crearUsuario(nombre: String, email: String, contrasena: String, rolId: Int?) {
        viewModelScope.launch {
            try {
                api.register(RegisterRequest(nombre, email, contrasena, rolId))
                cargarUsuarios()
            } catch (e: Exception) {
                _error.value = "Error al crear usuario: ${e.message}"
            }
        }
    }

    fun borrarUsuario(usuario: UsuarioListItem) {
        viewModelScope.launch {
            try {
                api.deleteUsuario(usuario.id)
                cargarUsuarios()
            } catch (e: Exception) {
                _error.value = "Error al borrar usuario: ${e.message}"
            }
        }
    }

    // =========================================================================
    // UTILIDADES
    // =========================================================================

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun limpiarError() {
        _error.value = null
    }
}