package io.keepcoding.eh_ho.data

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object ApiRequestQueue {
    // Creamos la cola de peticiones sino se creo ya, para que solo haya una para toda la app
    private var requestQueue: RequestQueue? = null

    fun getRequestQueue(context: Context): RequestQueue {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context)

        return requestQueue as RequestQueue
    }
}