package com.example.myproyecto.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Proyecto::class, Usuarios::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun proDao(): ProDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "tasks_db"
                )

                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).proDao()

                                // Datos iniciales
                                // Datos iniciales
                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Teclado no responde",
                                        descripcion = "El teclado inalámbrico no se conecta al equipo.",
                                        categoria = "Hardware",
                                        estado = "En proceso",
                                        urgencia = "Media",
                                        fecha = "5-1-25",
                                        ubicacion = "B204"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Fallo en router principal",
                                        descripcion = "El router de la planta baja pierde conexión aleatoriamente.",
                                        categoria = "Conectividad",
                                        estado = "En espera",
                                        urgencia = "Alta",
                                        fecha = "24-1-25",
                                        ubicacion = "B103"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Actualización fallida",
                                        descripcion = "La instalación del último parche de seguridad no completó correctamente.",
                                        categoria = "Software",
                                        estado = "Reportada",
                                        urgencia = "Media",
                                        fecha = "30-1-25",
                                        ubicacion = "B104"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Servidor lento",
                                        descripcion = "El servidor de base de datos muestra tiempos de respuesta elevados.",
                                        categoria = "Servidor",
                                        estado = "En proceso",
                                        urgencia = "Alta",
                                        fecha = "4-2-25",
                                        ubicacion = "B201"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "PC se reinicia solo",
                                        descripcion = "El ordenador de la sala de diseño se reinicia sin aviso.",
                                        categoria = "Hardware",
                                        estado = "En espera",
                                        urgencia = "Alta",
                                        fecha = "15-2-25",
                                        ubicacion = "B202"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Problemas de WiFi",
                                        descripcion = "El WiFi se desconecta cada pocos minutos.",
                                        categoria = "Conectividad",
                                        estado = "En proceso",
                                        urgencia = "Media",
                                        fecha = "28-2-25",
                                        ubicacion = "B203"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Software bloqueado",
                                        descripcion = "El sistema de gestión se congela al abrir reportes.",
                                        categoria = "Software",
                                        estado = "Reportada",
                                        urgencia = "Alta",
                                        fecha = "3-3-25",
                                        ubicacion = "B102"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Corte en switch",
                                        descripcion = "El switch de la segunda planta dejó de funcionar.",
                                        categoria = "Conectividad",
                                        estado = "En proceso",
                                        urgencia = "Alta",
                                        fecha = "13-3-25",
                                        ubicacion = "PB201"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Permiso denegado en carpeta",
                                        descripcion = "El usuario no puede modificar archivos en la carpeta compartida.",
                                        categoria = "Permisos",
                                        estado = "En espera",
                                        urgencia = "Media",
                                        fecha = "25-3-25",
                                        ubicacion = "B101"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Servidor de correo caído",
                                        descripcion = "Los correos entrantes no llegan al servidor.",
                                        categoria = "Servidor",
                                        estado = "En proceso",
                                        urgencia = "Alta",
                                        fecha = "13-4-25",
                                        ubicacion = "CPD"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Pantalla con líneas",
                                        descripcion = "El monitor muestra líneas horizontales al encender.",
                                        categoria = "Hardware",
                                        estado = "Resuelta",
                                        urgencia = "Baja",
                                        fecha = "27-4-25",
                                        ubicacion = "B202"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "App no inicia",
                                        descripcion = "La aplicación de inventarios no abre tras el login.",
                                        categoria = "Software",
                                        estado = "En proceso",
                                        urgencia = "Media",
                                        fecha = "1-5-25",
                                        ubicacion = "B102"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "DNS no responde",
                                        descripcion = "La resolución de nombres falla para varios usuarios.",
                                        categoria = "Conectividad",
                                        estado = "Reportada",
                                        urgencia = "Alta",
                                        fecha = "12-5-25",
                                        ubicacion = "B204"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Reset de contraseña",
                                        descripcion = "Usuario necesita restablecer contraseña.",
                                        categoria = "Permisos",
                                        estado = "Resuelta",
                                        urgencia = "Baja",
                                        fecha = "20-5-25",
                                        ubicacion = "B202"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Servidor web inaccesible",
                                        descripcion = "No se puede acceder al sitio interno.",
                                        categoria = "Servidor",
                                        estado = "En espera",
                                        urgencia = "Alta",
                                        fecha = "28-5-25",
                                        ubicacion = "B104"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Equipo muy lento",
                                        descripcion = "La computadora tarda más de 10 minutos en iniciar.",
                                        categoria = "Hardware",
                                        estado = "Reportada",
                                        urgencia = "Media",
                                        fecha = "1-6-25",
                                        ubicacion = "Sala de profesores"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Error en instalación de software",
                                        descripcion = "No se puede instalar la nueva versión del ERP.",
                                        categoria = "Software",
                                        estado = "En proceso",
                                        urgencia = "Alta",
                                        fecha = "5-9-25",
                                        ubicacion = "Dpto de Informática"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Pérdida de paquetes",
                                        descripcion = "Varias estaciones están experimentando pérdida de paquetes en la red.",
                                        categoria = "Conectividad",
                                        estado = "En proceso",
                                        urgencia = "Media",
                                        fecha = "21-9-25",
                                        ubicacion = "B203"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Ratón no funciona",
                                        descripcion = "El ratón inalámbrico no responde correctamente.",
                                        categoria = "Hardware",
                                        estado = "En proceso",
                                        urgencia = "Media",
                                        fecha = "15-11-25",
                                        ubicacion = "B203"
                                    )
                                )

                                dao.insertPro(
                                    Proyecto(
                                        titulo = "Acceso restringido a aplicación",
                                        descripcion = "Usuario solicita permisos para nueva herramienta.",
                                        categoria = "Permisos",
                                        estado = "En espera",
                                        urgencia = "Baja",
                                        fecha = "10-12-25",
                                        ubicacion = "B104"
                                    )
                                )
                                dao.insertUsu(
                                    Usuarios(
                                        correo = "jorge@dam.com",
                                        contraseña = "jorge",
                                        tipo = "administrador"
                                    )
                                )
                                dao.insertUsu(
                                    Usuarios(
                                        correo = "deva@dam.com",
                                        contraseña = "deva",
                                        tipo = "administrador"
                                    )
                                )
                                dao.insertUsu(
                                    Usuarios(
                                        correo = "christian@dam.com",
                                        contraseña = "christian",
                                        tipo = "usuario"
                                    )
                                )
                                dao.insertUsu(
                                    Usuarios(
                                        correo = "arturo@dam.com",
                                        contraseña = "arturo",
                                        tipo = "usuario"
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
