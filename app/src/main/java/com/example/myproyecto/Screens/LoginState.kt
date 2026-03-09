package com.example.myproyecto.Screens

import com.example.myproyecto.data.Usuarios

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: Usuarios) : LoginState()
    object Error : LoginState()
}
