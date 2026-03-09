package com.example.myproyecto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Proyecto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val estado: String,
    val urgencia: String,
    val fecha: String,
    val ubicacion: String
)



