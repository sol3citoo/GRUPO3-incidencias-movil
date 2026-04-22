package com.example.myproyecto.Screens

import android.app.Application
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myproyecto.data.AppDataBase
import com.example.myproyecto.data.Proyecto
import com.example.myproyecto.data.Usuarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale



class ProViewModel(application: Application) : AndroidViewModel(application) {

    private val proDao = AppDataBase.getInstance(application).proDao()

    //  Usuario actual
    private val _usuarioActual = MutableStateFlow<Usuarios?>(null)
    val usuarioActual: StateFlow<Usuarios?> = _usuarioActual

    //  Login state
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    //  Proyectos
    private val _proyectos = MutableStateFlow<List<Proyecto>>(emptyList())
    val proyectos: StateFlow<List<Proyecto>> = _proyectos

    private val _proyectoSeleccionado = MutableStateFlow<Proyecto?>(null)
    val proyectoSeleccionado: StateFlow<Proyecto?> = _proyectoSeleccionado

    private val _categorias = MutableStateFlow<List<String>>(emptyList())
    val categorias: StateFlow<List<String>> = _categorias

    private val _estados = MutableStateFlow<List<String>>(emptyList())
    val estados: StateFlow<List<String>> = _estados

    private val _urgencias = MutableStateFlow<List<String>>(emptyList())
    val urgencias: StateFlow<List<String>> = _urgencias

    private val _ubicaciones = MutableStateFlow<List<String>>(emptyList())
    val ubicaciones: StateFlow<List<String>> = _ubicaciones

    private val _ultimoIdUsuario = MutableStateFlow(0)
    val ultimoIdUsuario: StateFlow<Int> = _ultimoIdUsuario

    private val _ultimoIdProyecto = MutableStateFlow(0)
    val ultimoIdProyecto: StateFlow<Int> = _ultimoIdProyecto

    //  Usuarios

    private val _usuarios = MutableStateFlow<List<Usuarios>>(emptyList())
    val usuarios: StateFlow<List<Usuarios>> = _usuarios

    private val _usuarioSeleccionado = MutableStateFlow<Usuarios?>(null)
    val usuarioSeleccionado: StateFlow<Usuarios?> = _usuarioSeleccionado

    //  Para ComboBox
    val idsUsuarios = MutableStateFlow<List<String>>(emptyList())
    val correos = MutableStateFlow<List<String>>(emptyList())
    val tiposUsuarios = MutableStateFlow<List<String>>(emptyList())


    private val _ids = MutableStateFlow<List<String>>(emptyList())
    val ids: StateFlow<List<String>> = _ids

    fun cargarTodosIds() {
        viewModelScope.launch {
            val todosIds = proDao.getAllIds() // ahora suspend
            _ids.value = todosIds.map { it.toString() }
        }
    }

    fun cargarUltimoId() {
        viewModelScope.launch {
            val ultimo = proDao.obtenerUltimoId() ?: 0
            _ultimoIdUsuario.value = ultimo
        }
    }

    fun cargarUltimoIdProyecto() {
        viewModelScope.launch {
            val ultimo = proDao.obtenerUltimoIdProyecto() ?: 0
            _ultimoIdProyecto.value = ultimo
        }
    }



    // ====================== USUARIOS ======================

    fun cargarUsuarios() {
        viewModelScope.launch {
            val lista = proDao.getAllU()
            _usuarios.value = lista
            actualizarOpcionesUsuarios(lista)
            _ultimoIdUsuario.value = lista.maxOfOrNull { it.id } ?: 0
        }
    }

    private fun actualizarOpcionesUsuarios(lista: List<Usuarios>) {
        idsUsuarios.value = lista.map { it.id.toString() }
        correos.value = lista.map { it.correo }
        tiposUsuarios.value = lista.map { it.tipo }.distinct()
    }

    fun seleccionarUsuario(usuario: Usuarios) {
        _usuarioSeleccionado.value = usuario
    }


    fun crearUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            proDao.insertUsu(usuario)
            cargarUsuarios()        // refresca la lista de usuarios
        }
    }

    fun actualizarUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            proDao.updateUsuario(usuario)
            cargarUsuarios()        // refresca la lista de usuarios
        }
    }


    fun borrarUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            proDao.deleteUsuario(usuario)
            cargarUsuarios()
        }
    }


    fun cargarUsuariosFiltrados(id: String, correo: String, tipo: String) {
        viewModelScope.launch {
            val lista = proDao.getAllU().filter {
                        (id == "-" || it.id.toString() == id) &&
                        (correo == "-" || it.correo == correo) &&
                        (tipo == "-" || it.tipo == tipo)
            }
            _usuarios.value = lista
        }
    }

    // ====================== PROYECTOS ======================

    fun cargarFiltrados(
        id: String,
        categoria: String,
        estado: String,
        urgencia: String,
        ubicacion: String
    ) {
        viewModelScope.launch {
            val idInt = if (id == "-" || id.isEmpty()) 0 else id.toInt()
            _proyectos.value = proDao.filtrarProyectos(id, idInt, categoria, estado, urgencia, ubicacion)
            _proyectoSeleccionado.value = null // 🔹 Limpiamos la selección al recargar
        }
    }

    fun crearProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proDao.insertPro(proyecto)
            cargarFiltrados("-", "-", "-", "-", "-")
            cargarOpciones()
        }
    }

    fun modificarProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proDao.updateProyecto(proyecto)
            _proyectos.value = _proyectos.value.map { if (it.id == proyecto.id) proyecto else it }
        }
    }


    fun borrarProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proDao.deleteProyecto(proyecto)
            _proyectos.value = _proyectos.value.filter { it.id != proyecto.id }
            _proyectoSeleccionado.value = null
            cargarOpciones()
        }
    }

    fun seleccionarProyecto(proyecto: Proyecto?) {
        _proyectoSeleccionado.value = proyecto
    }

    fun cargarOpciones() {
        viewModelScope.launch {
            _categorias.value = proDao.getCategorias()
            _estados.value = proDao.getEstados()
            _urgencias.value = proDao.getUrgencias()
            _ubicaciones.value = proDao.getUbicaciones()
            _ultimoIdProyecto.value = proDao.obtenerUltimoIdProyecto() ?: 0
        }
    }

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return sdf.format(Date())
    }

    // ====================== LOGIN ======================

    fun login(correo: String, contraseña: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = proDao.login(correo, contraseña)
            if (user != null) {
                _usuarioActual.value = user
                _loginState.value = LoginState.Success(user)
                cargarFiltrados("-", "-", "-", "-", "-")
            } else {
                _loginState.value = LoginState.Error
            }
        }
    }

    fun logout() {
        _usuarioActual.value = null
        _proyectos.value = emptyList()
        _proyectoSeleccionado.value = null
        _loginState.value = LoginState.Idle
    }

}
