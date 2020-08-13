package io.keepcoding.eh_ho.data

import android.app.DownloadManager
import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.R
import org.json.JSONObject

object TopicsRepo {
    val topics: MutableList<Topic> = mutableListOf()
//    get() {
//        if (field.isEmpty())
//            field.addAll(createDummyTopics())
//        return field
//    }

    fun getTopics(
        context: Context,
        onSuccess: (List<Topic>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.getTopics(),
            null,
            {
                val list = Topic.parseTopicsList(it)
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

    // Mediante este metodo podemos recuperar la informacion de un elemento desde cualquier punto de la app, pasandole el identificador del mismo
    // El operador .find itera sobre los datos y realiza una tarea sobre ellos
    fun getTopic(id: String): Topic? = topics.find {
        it.id == id
    }

//    fun createDummyTopics(count: Int = 20): List<Topic> =
//        (0..count).map {
//            Topic(
//                title = "Title $it"
//            )
//        }

    // Metodo para crear un topic y añadirlo a nuestra coleccion
    fun  addTopic(
        context: Context,
        model: CreateTopicModel,
        onSuccess: (CreateTopicModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        // Para hacer la peticion a Discourse
        // Primero accedemos a las SharedReferences que es donde esta guardado el username
        val username = UserRepo.getUsername(context)
        val request = PostRequest(
            // Indicamos el metodo
            Request.Method.POST,
            // Indicamos la ruta
            ApiRoutes.createTopic(),
            // Indicamos el body de la peticion
            model.toJson(),
            {
                onSuccess
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