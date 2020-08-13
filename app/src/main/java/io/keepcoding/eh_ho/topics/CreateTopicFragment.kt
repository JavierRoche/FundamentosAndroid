package io.keepcoding.eh_ho.topics

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.LoadingDialogFragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.CreateTopicModel
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_create_topic.*

const val TAG_LOADING_DIALOG = "loading_dialog"

class CreateTopicFragment : Fragment() {
    // Variable para la comunicacion del listener
    var interactionListener: CreateTopicInteractionListener? = null
    // Cuadro de dialogo que se puede mostrar en el fragmento
    val loadingDialogFragment: LoadingDialogFragment by lazy {
        val message = getString(R.string.label_creating_topic)
        // Para poder pasar argumentos al dialog en el constructor lo indicamos
        LoadingDialogFragment.newInstance(message)
    }


    /**
     * PROTOCOLS
     */

    // Creamos una interface para comunicar eventos con la actividad
    interface CreateTopicInteractionListener {
        fun onTopicCreated()
    }


    /**
     * LIFE CYCLE
     */

    // Liga nuestro fragmento a su actividad
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CreateTopicInteractionListener)
            this.interactionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${CreateTopicInteractionListener::class.java.canonicalName}")
    }

    // Ejecuta mientras se esta creando la instancia del fragmento
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Ejecuta si se ha invocado previamente el metodo setHasOptionsMenu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Con ayuda del Inflater de menus que me llega inflamos el menu
        inflater.inflate(R.menu.menu_create_topic, menu)

        // Invocamos el comportamiento por defecto siempre despues de inflar el menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_create_topic)
    }


    /**
     * MENU USER ACTIONS
     */

    // Para identificar al item del menu al que se le dio clic sobreescribimos este metodo
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Recorremos los elementos del menu y valoramos
        when (item.itemId) {
            R.id.action_send -> createTopic()
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * PRIVATE FUNCTIONS
     */

    // Metodo que creara el topic
    private fun createTopic() {
        if (isValidForm()) {
            // Mostramos el dialogo de loading indicando quien sera el manejador de fragmentos
            fragmentManager?.let {
                loadingDialogFragment.show(it, TAG_LOADING_DIALOG)
            }

            // Creamos el modelo de alta de topic
            val model = CreateTopicModel(
                inputTitle.text.toString(),
                inputContent.text.toString()
            )
            // Accedemos al repo donde esta nuestra estructura de datos para añadir el nuevo topic previa validacion del contexto
            context?.let {
                TopicsRepo.addTopic(
                    it.applicationContext,
                    model,
                    {
                        // Ocultamos el dialogo de loading del fragmento
                        loadingDialogFragment.dismiss()
                        // Informamos a la actividad que la creacion del topic ha terminado mediante el interactionListener
                        interactionListener?.onTopicCreated()
                    },
                    {
                        // Ocultamos el dialogo de loading del fragmento
                        loadingDialogFragment.dismiss()

                        handleError(it)
                    }
                )
            }

        } else {
            showErrors()
        }
    }

    private fun handleError(error: RequestError) {
        val message =
            if (error.messageResId != null)
                getString(error.messageResId)
            else if (error.message != null)
                error.message
            else
                getString(R.string.error_default)

        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
    }

    // Funcion inline para limpieza y comprension de codigo
    private fun isValidForm() = inputTitle.text.isNotEmpty() && inputContent.text.isNotEmpty()

    // Metodo para mostrar errores en las cajas de texto cuando esten vacias
    private fun showErrors() {
        if (inputTitle.text.isEmpty())
            inputTitle.error = getString(R.string.error_empty)
        if (inputContent.text.isEmpty())
            inputContent.error = getString(R.string.error_empty)
    }
}