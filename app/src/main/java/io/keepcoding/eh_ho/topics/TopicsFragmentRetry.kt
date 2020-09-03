package io.keepcoding.eh_ho.topics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_topics_retry.*

class TopicsFragmentRetry: Fragment() {
    // Definimos una instancia del protocolo InteractionListener, que recogera los eventos de usuario sobre el fragmento
    private var topicsInteractionListener: TopicsRetryInteractionListener? = null

    /**
     * PROTOCOLS
     */

    // Protocolo con los metodos abstractos del fragmento que recogen las interaciones del usuario y que seran sobreescritos en la actividad
    interface TopicsRetryInteractionListener {
        fun onRetry()
    }


    /**
     * LIFE CYCLE
     */

    // Liga nuestro fragmento a su actividad
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Comprobamos que el contexto de la actividad implementa el protocolo
        if (context is TopicsRetryInteractionListener)
            topicsInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${TopicsRetryInteractionListener::class.java.canonicalName}")
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_topics_retry)
    }

    // Marca la disponibilidad de la vista tras su creacion
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creamos el evento que recogera el tap en el boton Retry
        buttonRetry.setOnClickListener {
            this.topicsInteractionListener?.onRetry()
        }
    }

    // Desliga el fragmento de su actividad
    override fun onDetach() {
        super.onDetach()
        this.topicsInteractionListener = null
    }
}