package com.example.myproyecto.api

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Configuración central
//CAMBIAR POR IP DEL ORDENADOR
private const val BASE_URL = "http://10.0.2.2:3001/"

//DataStore
private val Context.dataStore by preferencesDataStore(name = "auth")
private val TOKEN_KEY = stringPreferencesKey("jwt_token")

object TokenStorage {
    suspend fun save(context: Context, token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun get(context: Context): String? =
        context.dataStore.data.map { it[TOKEN_KEY] }.first()

    suspend fun clear(context: Context) {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}

//Interceptor JWT
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = runBlocking { TokenStorage.get(context) }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}


//Retrofit singleton
object RetrofitClient {
    fun create(context: Context): ApiService {
        /*val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY   // cambia a NONE en producción
        }*/

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            //.addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}