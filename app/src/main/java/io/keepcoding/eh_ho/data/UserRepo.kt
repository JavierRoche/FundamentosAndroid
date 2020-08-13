package io.keepcoding.eh_ho.data

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.keepcoding.eh_ho.R

const val PREFERENCES_SESSION = "session"
const val PREFERENCES_USERNAME = "username"

object UserRepo {
    // Metodo para almacenar los datos de la sesion
    fun signIn(
        context: Context,
        signInModel: SignInModel,
        success: (SignInModel) -> Unit,
        error: (RequestError) -> Unit
    ) {
        // Creamos el request al servidor
        val request: JsonObjectRequest = JsonObjectRequest(
            // Indicamos el metodo que vamos a usar
            Request.Method.GET,
            // Indicamos la url
            ApiRoutes.signIn(signInModel.username),
            // El body va vacio por ser un GET
            null,
            // Indicamos el listener para el success
            { response ->
                // Notificamos que la peticion fue exitosa
                success(signInModel)
                // Guardamos la sesion
                saveSession(context, signInModel.username)
            },
            // Indicamos el listener para el error
            { e ->
                e.printStackTrace()
                // Valoramos el tipo de error Volley
                val errorObject = if (e is ServerError && e.networkResponse.statusCode == 404) {
                    RequestError(e, messageResId = R.string.error_not_registered)
                } else if (e is NetworkError) {
                    RequestError(e, messageResId = R.string.error_no_internet)
                } else {
                    RequestError(e)
                }
                // Devolvemos el error
                error(errorObject)
            }
        )

        // Encolamos la peticion en nuestro requestQueue estatico
        ApiRequestQueue.getRequestQueue(context).add(request)
    }

    fun signUp(
        context: Context,
        signUpModel: SignUpModel,
        success: (SignUpModel) -> Unit,
        error: (RequestError) -> Unit
    ) {
        // Para hacer el POST de nuevo usuario
        val request = PostRequest(
            // Indicamos el metodo
            Request.Method.POST,
            // Indicamos la ruta
            ApiRoutes.signUp(),
            // Indicamos el body de la peticion
            signUpModel.toJson(),
            // Indicamos los callback de Volley de success o error
            { response ->
                val successStatus = response?.getBoolean("success") ?: false

                if (successStatus) {
                    success(signUpModel)
                } else {
                    error(RequestError(message = response?.getString("message")))
                }
            },
            { e ->
                e.printStackTrace()

                val requestError =
                    if (e is NetworkError)
                        RequestError(e, messageResId = R.string.error_no_internet)
                    else
                        RequestError(e)

                error(requestError)
            },
            // Indicamos el username, por defecto nulo
            null,
            true
        )

        // Encolamos la peticion en nuestro requestQueue estatico
        ApiRequestQueue.getRequestQueue(context).add(request)
    }

    private fun saveSession(context: Context, username: String) {
        // Para acceder a las SharedPreferences lo hacemos mediante el contexto
        // Le pasamos una cadena de texto que indica cual sera el nombre del archivo que se creara, ademas del modo de acceso
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        // Accedemos al archivo y lo editamos
        preferences
            .edit()
            .putString("username", username)
            .apply()
    }

    // Accedemos al SharedPreferences a por el username
    fun getUsername(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        return preferences.getString(PREFERENCES_USERNAME, null)
    }

    fun logout(context: Context) {
        // Accedemos a las preferencias de la sesion en modo privado
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        // Accedemos al archivo y lo editamos
        preferences
            .edit()
            .putString("username", null)
            .apply()
    }

    // Metodo que valida si el user esta logueado
    // Necesitamos el contexto para acceder a los SharedPreferences
    fun isLogged(context: Context): Boolean {
        // Accedo a las preferencias en modo privado y recupero el username con ellas
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        val username = preferences.getString(PREFERENCES_USERNAME, null)
        return username != null
    }
}