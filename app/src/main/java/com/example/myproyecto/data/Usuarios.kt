package com.example.myproyecto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Usuarios(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val correo: String,
    val contraseña: String,
    val tipo: String
)
