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

    private val _usuarioActual = MutableStateFlow<Usuarios?>(null)
    val usuarioActual: StateFlow<Usuarios?> = _usuarioActual

    private val _proyectos = MutableStateFlow<List<Proyecto>>(emptyList())
    val proyectos: StateFlow<List<Proyecto>> = _proyectos

    private val _proyectoSeleccionado = MutableStateFlow<Proyecto?>(null)
    val proyectoSeleccionado: StateFlow<Proyecto?> = _proyectoSeleccionado

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _categorias = MutableStateFlow<List<String>>(emptyList())
    val categorias: StateFlow<List<String>> = _categorias

    private val _estados = MutableStateFlow<List<String>>(emptyList())
    val estados: StateFlow<List<String>> = _estados

    private val _urgencias = MutableStateFlow<List<String>>(emptyList())
    val urgencias: StateFlow<List<String>> = _urgencias

    private val _ubicaciones = MutableStateFlow<List<String>>(emptyList())
    val ubicaciones: StateFlow<List<String>> = _ubicaciones


    fun seleccionarProyecto(proyecto: Proyecto?) {
        _proyectoSeleccionado.value = proyecto
    }

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

    fun borrarProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proDao.deleteProyecto(proyecto)
            _proyectos.value = _proyectos.value.filter { it.id != proyecto.id }
            _proyectoSeleccionado.value = null
        }
    }

    fun crearProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proDao.insertPro(proyecto)
            cargarFiltrados("-", "-", "-", "-", "-")
        }
    }


    fun modificarProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proDao.updateProyecto(proyecto)
            _proyectos.value = _proyectos.value.map { if (it.id == proyecto.id) proyecto else it }
        }
    }

    fun cargarOpciones() {
        viewModelScope.launch {
            _categorias.value = proDao.getCategorias()
            _estados.value = proDao.getEstados()
            _urgencias.value = proDao.getUrgencias()
            _ubicaciones.value = proDao.getUbicaciones()
        }
    }

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return sdf.format(Date())
    }

}
