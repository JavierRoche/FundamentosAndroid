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

// Object estatico para la lista de topics
object TopicsRepo {
    val topics: MutableList<Topic> = mutableListOf()

    // Metodo que prepara y lanza la peticion al servidor para recuperar los topics
    fun getTopics(
        context: Context,
        onSuccess: (List<Topic>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        // Formamos la request
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.getTopics(),
            // El body va vacio por ser un GET
            null,
            // Indicamos el listener para el success
            {
                val list =
                    Topic.parseTopicsList(it)
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


    // Metodo que prepara y lanza la peticion al servidor para añadir un topic a nuestra coleccion
    fun  addTopic(
        context: Context,
        model: CreateTopicModel,
        onSuccess: (CreateTopicModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        // Accedemos a las SharedReferences para recuperear el username necesario en la peticion
        val username = UserRepo.getUsername(context)

        // Formamos la request
        val request = PostRequest(
            Request.Method.POST,
            ApiRoutes.createTopic(),
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