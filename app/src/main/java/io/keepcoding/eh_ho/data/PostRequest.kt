package io.keepcoding.eh_ho.data

import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import io.keepcoding.eh_ho.BuildConfig
import org.json.JSONObject
import javax.xml.transform.ErrorListener

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
    override fun getHeaders(): MutableMap<String, String> {
        val headers= mutableMapOf<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Accept"] = "application/json"

        if (useApiKey)
            headers["Api-Key"] = BuildConfig.DiscourseApiKey

        // Comprobamos si estamos en un POST de topic, en cuyo caso username no sera nulo
        username?.let {
            headers["Api-Username"] = it
        }

        return headers
    }
}