package com.example.myproyecto.api

import retrofit2.http.*

interface ApiService {

    // ─── USUARIOS ─────────────────────────────────────────────────────────────

    @POST("usuarios/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("usuarios")
    suspend fun getUsuarios(): List<UsuarioListItem>

    @POST("usuarios/register")
    suspend fun register(@Body body: RegisterRequest): MessageResponse

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): MessageResponse

    // ─── INCIDENCIAS ──────────────────────────────────────────────────────────

    @GET("incidencias")
    suspend fun getIncidencias(): List<IncidenciaResponse>

    @POST("incidencias/filter")
    suspend fun filtrarIncidencias(@Body body: FiltroIncidenciasRequest): List<IncidenciaResponse>

    @POST("incidencias")
    suspend fun crearIncidencia(@Body body: CrearIncidenciaRequest): MessageResponse

    @PUT("incidencias/{id}")
    suspend fun editarIncidencia(
        @Path("id") id: Int,
        @Body body: EditarIncidenciaRequest
    ): MessageResponse

    @PUT("incidencias/{id}/cambiarEstado")
    suspend fun cambiarEstado(
        @Path("id") id: Int,
        @Body body: CambiarEstadoRequest
    ): MessageResponse

    // ─── OPCIONES ─────────────────────────────────────────────────────────────

    @GET("categorias")
    suspend fun getCategorias(): List<CategoriaResponse>

    @GET("ubicaciones")
    suspend fun getUbicaciones(): List<UbicacionResponse>

    @GET("urgencias")
    suspend fun getUrgencias(): List<UrgenciaResponse>

    @GET("estados")
    suspend fun getEstados(): List<EstadoResponse>

    @GET("roles")
    suspend fun getRoles(): List<RolResponse>
}