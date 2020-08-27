package io.keepcoding.eh_ho.data

import android.icu.text.CaseMap
import org.json.JSONObject

// Creamos una data class para el modelo de datos de usuario
data class SignInModel(
    val username: String,
    val password: String
)

// Modelo de datos para el registro de usuario
data class SignUpModel (
    val username: String,
    val email: String,
    val password: String
) {
    // Definimos un metodo para la data class que nos devolvera un JSON que pasaremos en la request de alta de usuario en Discourse
    fun toJson(): JSONObject {
        return JSONObject()
            .put("name", username)  // {"name": "username"}
            .put("username", username)
            .put("email", email)
            .put("password", password)
            .put("active", true)
            .put("approved", true)
    }
}

// Modelo de datos para creacion de un nuevo topic
data class CreateTopicModel(
    val title: String,
    val content: String
) {
    fun toJson(): JSONObject {
        return JSONObject()
            .put("title", title)
            .put("raw", content)
    }
}

// Modelo de datos para creacion de un nuevo post
data class AddPostModel(
    val topicId: String,
    val content: String
) {
    fun toJson(): JSONObject {
        return JSONObject()
            .put("topic_id", topicId)
            .put("raw", content)
    }
}