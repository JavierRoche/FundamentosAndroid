package io.keepcoding.eh_ho.data.repos

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.api.ApiRequestQueue
import io.keepcoding.eh_ho.data.api.PostRequest
import io.keepcoding.eh_ho.data.api.RequestError
import io.keepcoding.eh_ho.data.api.ApiRoutes
import io.keepcoding.eh_ho.data.api.SignInModel
import io.keepcoding.eh_ho.data.api.SignUpModel

const val PREFERENCES_SESSION = "session"
const val PREFERENCES_USERNAME = "username"

// Object estatico para los datos de la sesion
object UserRepo {

    // Metodo que prepara y lanza la peticion de login al servidor
    fun signIn(
        context: Context,
        signInModel: SignInModel,
        success: (SignInModel) -> Unit,
        error: (RequestError) -> Unit
    ) {
        // Formamos la request
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiRoutes.signIn(signInModel.username),
            // El body va vacio por ser un GET
            null,
            // Indicamos el listener para el success
            {
                // Guardamos la sesion en las SharedPreferences
                saveSession(
                    context,
                    signInModel.username
                )
                success(signInModel)
            },
            // Indicamos el listener para el error
            {
                it.printStackTrace()

                // Valoramos el tipo de error Volley
                val errorObject = if (it is ServerError && it.networkResponse.statusCode == 404) {
                    RequestError(
                        it,
                        messageResId = R.string.error_not_registered
                    )
                } else if (it is NetworkError) {
                    RequestError(
                        it,
                        messageResId = R.string.error_no_internet
                    )
                } else {
                    RequestError(it)
                }
                error(errorObject)
            }
        )

        // Hacemos el encolamiento de la request
        ApiRequestQueue.getRequestQueue(context).add(request)
    }

    // Metodo que prepara y lanza la peticion de alta de usuario al servidor
    fun signUp(
        context: Context,
        signUpModel: SignUpModel,
        success: (SignUpModel) -> Unit,
        error: (RequestError) -> Unit
    ) {
        // Para hacer el POST de nuevo usuario
        val request = PostRequest(
            Request.Method.POST,
            ApiRoutes.signUp(),
            signUpModel.toJson(),
            // Formamos la request
            {
                val successStatus = it?.getBoolean("success") ?: false

                if (successStatus) {
                    success(signUpModel)
                } else {
                    error(
                        RequestError(
                            message = it?.getString(
                                "message"
                            )
                        )
                    )
                }
            },
            // Indicamos el listener para el error
            {
                it.printStackTrace()

                val requestError =
                    if (it is NetworkError)
                        RequestError(
                            it,
                            messageResId = R.string.error_no_internet
                        )
                    else
                        RequestError(it)

                error(requestError)
            },
            // Indicamos el username, por defecto nulo
            null,
            true
        )

        // Hacemos el encolamiento de la request
        ApiRequestQueue.getRequestQueue(context).add(request)
    }


    /**
     * SHARE PREFERENCES ACCESS
     */

    // Metodo de acceso a las SharedPreferences para guardar el usuario de la sesion
    private fun saveSession(context: Context, username: String) {
        // Accede a las SharedPreferences con el contexto, indicando la key del dato y el modo de acceso
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        // Accedemos al archivo y lo editamos
        preferences
            .edit()
            .putString("username", username)
            .apply()
    }

    // Metodo de acceso a las SharedPreferences para recuperar el usuario de la sesion
    fun getUsername(context: Context): String? {
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        return preferences.getString(PREFERENCES_USERNAME, null)
    }

    // Metodo de acceso a las SharedPreferences a comprobar si el user esta logueado
    fun isLogged(context: Context): Boolean {
        // Accede a las SharedPreferences con el contexto, indicando la key del dato y el modo de acceso
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        // Devuelve el valor del usuario si existe
        val username = preferences.getString(PREFERENCES_USERNAME, null)
        return username != null
    }

    fun logout(context: Context) {
        // Accede a las SharedPreferences con el contexto, indicando la key del dato y el modo de acceso
        val preferences = context.getSharedPreferences(PREFERENCES_SESSION, Context.MODE_PRIVATE)
        // Accedemos al archivo y lo editamos
        preferences
            .edit()
            .putString("username", null)
            .apply()
    }
}