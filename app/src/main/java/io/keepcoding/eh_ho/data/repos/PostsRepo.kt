package io.keepcoding.eh_ho.data.repos

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.*
import io.keepcoding.eh_ho.data.api.*
import org.json.JSONObject

// Object estatico para la lista de posts
object PostsRepo {
    val posts: MutableList<Post> = mutableListOf()

    // Metodo que prepara y lanza la peticion al servidor para recuperar los post de un topic
    fun getPosts(
        topicId: String,
        context: Context,
        onSuccess: (List<Post>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        // Formamos la request
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.getPostsOfTopic(topicId),
            // El body va vacio por ser un GET
            null,
            // Indicamos el listener para el success
            {
                val list =
                    Post.parsePostsList(it)
                onSuccess(list)
            },
            // Indicamos el listener para el error
            {
                it.printStackTrace()

                // Valoramos el tipo de error Volley
                val requestError =
                    if (it is NetworkError)
                        RequestError(
                            it,
                            messageResId = R.string.error_no_internet
                        )
                    else
                        RequestError(it)
                onError(requestError)
            }
        )

        // Hacemos el encolamiento de la request
        ApiRequestQueue.getRequestQueue(context).add(request)
    }

    // Metodo que prepara y lanza la peticion al servidor para añadir un post a un topic
    fun addPost(
        context: Context,
        model: AddPostModel,
        onSuccess: (AddPostModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        // Accedemos a las SharedReferences para recuperear el username necesario en la peticion
        val username = UserRepo.getUsername(context)

        // Formamos la request
        val request = PostRequest(
            Request.Method.POST,
            ApiRoutes.createPost(),
            model.toJson(),
            {
                onSuccess(model)
            },
            {
                it.printStackTrace()

                val requestError =
                    if (it is ServerError && it.networkResponse.statusCode == 422) {
                        // Obtenemos el body de la respuesta para mostrar un mensaje exacto
                        val body = String(it.networkResponse.data, Charsets.UTF_8)
                        val jsonError = JSONObject(body)
                        val errors = jsonError.getJSONArray("errors")
                        var errorMessage = ""
                        // Iteramos sobre el array del body para formar el mensaje
                        for (i in 0 until errors.length()) {
                            errorMessage += "${errors[i]} "
                        }
                        RequestError(
                            it,
                            message = errorMessage
                        )

                    } else if (it is NetworkError)
                        RequestError(
                            it,
                            messageResId = R.string.error_no_internet
                        )
                    else
                        RequestError(it)

                onError(requestError)
            },
            username,
            true
        )

        // Encolamos la peticion a la cola de peticiones
        ApiRequestQueue.getRequestQueue(context).add(request)
    }
}