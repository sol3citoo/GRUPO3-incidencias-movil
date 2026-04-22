package com.example.myproyecto.data

import androidx.room.*

@Dao
interface ProDao {

    // ------------------ PROYECTOS ------------------

    @Query("SELECT * FROM Proyecto")
    suspend fun getAll(): List<Proyecto>

    @Insert
    suspend fun insertPro(proyecto: Proyecto)

    @Update
    suspend fun updateProyecto(proyecto: Proyecto)

    @Delete
    suspend fun deleteProyecto(proyecto: Proyecto)

    @Query(
        """
        SELECT * FROM Proyecto
        WHERE (:id = '-' OR id = :idInt)
        AND (:categoria = '-' OR categoria = :categoria)
        AND (:estado = '-' OR estado = :estado)
        AND (:urgencia = '-' OR urgencia = :urgencia)
        AND (:ubicacion = '-' OR ubicacion = :ubicacion)
    """
    )
    suspend fun filtrarProyectos(
        id: String,
        idInt: Int,
        categoria: String,
        estado: String,
        urgencia: String,
        ubicacion: String
    ): List<Proyecto>

    @Query("SELECT DISTINCT categoria FROM Proyecto")
    suspend fun getCategorias(): List<String>

    @Query("SELECT DISTINCT estado FROM Proyecto")
    suspend fun getEstados(): List<String>

    @Query("SELECT DISTINCT urgencia FROM Proyecto")
    suspend fun getUrgencias(): List<String>

    @Query("SELECT DISTINCT ubicacion FROM Proyecto")
    suspend fun getUbicaciones(): List<String>

    @Query("SELECT id FROM Proyecto")
    suspend fun getAllIds(): List<Int>

    @Query("SELECT MAX(id) FROM Proyecto")
    suspend fun obtenerUltimoIdProyecto(): Int?

    // ------------------ USUARIOS ------------------


    @Query("SELECT * FROM Usuarios")
    suspend fun getAllU(): List<Usuarios>


    @Insert
    suspend fun insertUsu(usuarios: Usuarios)

    @Query(
        """
        SELECT * FROM Usuarios
        WHERE correo = :correo
        AND contraseña = :contraseña
        LIMIT 1
    """
    )
    suspend fun login(correo: String, contraseña: String): Usuarios?


    @Query("SELECT MAX(id) FROM Usuarios")
    suspend fun obtenerUltimoId(): Int?


    @Update
    suspend fun updateUsuario(usuario: Usuarios)

    @Delete
    suspend fun deleteUsuario(usuario: Usuarios)

}
