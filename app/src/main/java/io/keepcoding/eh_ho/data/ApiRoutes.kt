package io.keepcoding.eh_ho.data

import android.net.Uri
import io.keepcoding.eh_ho.BuildConfig

// Este modulo sirve para crear rutas estaticas accesibles desde to do el proyecto
object ApiRoutes {
    // Nos devuelve la ruta para el logueo
    fun signIn(username: String) =
        uriBuilder()
            .appendPath("users")
            .appendPath("${username}.json")
            .build()
            .toString()

    // Nos devuelve la ruta del alta de usuario
    fun signUp() =
        uriBuilder()
            .appendPath("users")
            .build()
            .toString()

    // Nos devuelve la ruta para la recuperacion de los latest topics
    fun getTopics() =
        uriBuilder()
            .appendPath("latest.json")
            .build()
            .toString()

    // Nos devuelve una ruta para la creacion de topics
    fun createTopic() =
        uriBuilder()
            .appendPath("posts.json")
            .build()
            .toString()

    // El uriBuilder no es mas que un constructor de rutas a los backend
    private fun uriBuilder() =
        Uri.Builder()
            .scheme("https")
            .authority(BuildConfig.DiscourseDomain)
}