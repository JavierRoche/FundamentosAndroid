package io.keepcoding.eh_ho.data.api

import android.net.Uri
import io.keepcoding.eh_ho.BuildConfig

// Object estatico para obtener los endpoints al servidor de Discourse
object ApiRoutes {
    // Ruta para el logueo: https://mdiscourse.keepcoding.io/users/{username}.json
    fun signIn(username: String) =
        uriBuilder()
            .appendPath("users")
            .appendPath("${username}.json")
            .build()
            .toString()

    // Ruta del alta de usuario: https://mdiscourse.keepcoding.io/users
    fun signUp() =
        uriBuilder()
            .appendPath("users")
            .build()
            .toString()

    // Ruta de recuperacion de latest topics: https://mdiscourse.keepcoding.io/latest.json
    fun getTopics() =
        uriBuilder()
            .appendPath("latest.json")
            .build()
            .toString()

    // Ruta de creacion de topics: https://mdiscourse.keepcoding.io/posts.json
    fun createTopic() =
        uriBuilder()
            .appendPath("posts.json")
            .build()
            .toString()

    // Ruta de recuperacion de los posts de un topic: https://mdiscourse.keepcoding.io/t/{topicId}.json
    fun getPostsOfTopic(topicId: String) =
        uriBuilder()
            .appendPath("t")
            .appendPath("${topicId}.json")
            .build()
            .toString()

    // Ruta de creacion de posts: https://mdiscourse.keepcoding.io/posts.json
    fun createPost() =
        uriBuilder()
            .appendPath("posts.json")
            .build()
            .toString()

    // El constructor de rutas a los endpoints que contiene el domino: https://mdiscourse.keepcoding.io
    private fun uriBuilder() =
        Uri.Builder()
            .scheme("https")
            .authority(BuildConfig.DiscourseDomain)
}