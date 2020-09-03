package io.keepcoding.eh_ho.data.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

// Object estatico para obtener la cola de peticiones al servidor
object ApiRequestQueue {
    private var requestQueue: RequestQueue? = null

    // Metodo para obtener la cola de peticiones al API de Discourse
    fun getRequestQueue(context: Context): RequestQueue {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context)

        return requestQueue as RequestQueue
    }
}