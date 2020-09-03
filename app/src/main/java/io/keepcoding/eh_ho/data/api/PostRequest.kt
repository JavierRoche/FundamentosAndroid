package io.keepcoding.eh_ho.data.api

import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.BuildConfig
import org.json.JSONObject

// Clase que define una request a un API
class PostRequest(
    method: Int,
    url: String,
    body: JSONObject?,
    listener: (response: JSONObject?) -> Unit,
    errorListener: (errorResponse: VolleyError) -> Unit,
    // Argumentos opcionales para POST
    private val username: String? = null,
    private val useApiKey: Boolean = true
): JsonObjectRequest(method, url, body, listener, errorListener) {

    // Metodo que devuelve los Headers que necesitan algunos accesos a los endpoints
    override fun getHeaders(): MutableMap<String, String> {
        val headers= mutableMapOf<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Accept"] = "application/json"

        // Si la request requiere ApiKey la informamos
        if (useApiKey)
            headers["Api-Key"] = BuildConfig.DiscourseApiKey

        // Si la request requiere username (POST de topic), éste no sera nulo
        username?.let {
            headers["Api-Username"] = it
        }

        return headers
    }
}