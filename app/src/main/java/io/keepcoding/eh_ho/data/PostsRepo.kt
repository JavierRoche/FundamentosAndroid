package io.keepcoding.eh_ho.data

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.R
import org.json.JSONObject

// Patron Singleton para crear un solo objeto de esta clase
object PostsRepo {
    val posts: MutableList<Post> = mutableListOf()

    fun getPosts(
        topicId: String,
        context: Context,
        onSuccess: (List<Post>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.getPostsOfTopic(topicId),
            null,
            {
                val list = Post.parsePostsList(it)
                onSuccess(list)
            },
            {
                it.printStackTrace()
                val requestError =
                    if (it is NetworkError)
                        RequestError(it, messageResId = R.string.error_no_internet)
                    else
                        RequestError(it)
                onError(requestError)
            }
        )

        // Hacemos el encolamiento de la request
        ApiRequestQueue.getRequestQueue(context).add(request)
    }

    // Metodo para añadir un post a un topic existente
    fun addPost(
        context: Context,
        model: AddPostModel,
        onSuccess: (AddPostModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        // Formamos la peticion para acceder al endpoint
        // Accedemos a las SharedReferences que es donde esta guardado el username
        val username = UserRepo.getUsername(context)
        val request = PostRequest(
            // Indicamos el metodo
            Request.Method.POST,
            // Indicamos la ruta
            ApiRoutes.createPost(),
            // Indicamos el body de la peticion
            model.toJson(),
            {
                onSuccess(model)
            },
            {
                it.printStackTrace()

                val requestError =
                    if (it is ServerError && it.networkResponse.statusCode == 422) {
                        // Vamos a obtener el body de la respuesta para mostrar un mensaje exacto
                        val body = String(it.networkResponse.data, Charsets.UTF_8)
                        val jsonError = JSONObject(body)
                        val errors = jsonError.getJSONArray("errors")
                        var errorMessage = ""
                        // Iteramos sobre el array del body para formar el mensaje
                        for (i in 0 until errors.length()) {
                            errorMessage += "${errors[i]} "
                        }
                        RequestError(it, message = errorMessage)

                    } else if (it is NetworkError)
                        RequestError(it, messageResId = R.string.error_no_internet)
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