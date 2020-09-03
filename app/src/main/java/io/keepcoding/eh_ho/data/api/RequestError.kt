package io.keepcoding.eh_ho.data.api

import com.android.volley.VolleyError

// Clase para el modelo de respuesta del endpoint
class RequestError(
    val volleyError: VolleyError? = null,
    val message: String? = null,
    val messageResId: Int? = null
)
