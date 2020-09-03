package io.keepcoding.eh_ho.posts

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.api.AddPostModel
import io.keepcoding.eh_ho.data.repos.PostsRepo
import io.keepcoding.eh_ho.data.api.RequestError
import io.keepcoding.eh_ho.topics.CreateTopicFragment
import kotlinx.android.synthetic.main.fragment_create_topic.container
import kotlinx.android.synthetic.main.fragment_create_topic.inputContent
import kotlinx.android.synthetic.main.fragment_reply_post.*

class ReplyPostFragment(private val topicId: String, private val topicTitle: String): Fragment() {
    //var topicId: String = topicId
    //var topicTitle: String = topicTitle
    // Variable para la comunicacion del listener
    var interactionListener: ReplyPostInteractionListener? = null

    /**
     * PROTOCOLS
     */

    // Creamos una interface para comunicar eventos con la actividad
    interface ReplyPostInteractionListener {
        fun onPostAdded()
    }


    /**
     * LIFE CYCLE
     */

    // Liga nuestro fragmento a su actividad
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ReplyPostInteractionListener)
            this.interactionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${CreateTopicFragment.CreateTopicInteractionListener::class.java.canonicalName}")
    }

    // Ejecuta mientras se esta creando la instancia del fragmento
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Ejecuta si se ha invocado previamente el metodo setHasOptionsMenu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Con ayuda del Inflater de menus que me llega inflamos el menu
        inflater.inflate(R.menu.menu_add_post, menu)

        // Invocamos el comportamiento por defecto siempre despues de inflar el menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reply_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Damos informacion del topic al que se esta añadiendo un post
        topicInfo.text = "Topic $topicId: $topicTitle"
    }

    /**
     * MENU USER ACTIONS
     */

    // Para identificar al item del menu al que se le dio clic sobreescribimos este metodo
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Recorremos los elementos del menu y valoramos
        when (item.itemId) {
            R.id.action_add -> addPost()
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * PRIVATE FUNCTIONS
     */

    // Metodo que añadira el post
    private fun addPost() {
        if (isValidForm()) {
            // Creamos el modelo de alta del topic
            val model = AddPostModel(
                topicId = topicId,
                content = inputContent.text.toString()
            )

            // Accedemos al repo donde esta nuestra estructura de datos para añadir el nuevo topic previa validacion del contexto
            context?.let {
                PostsRepo.addPost(
                    it.applicationContext,
                    model,
                    {
                        // Informamos a la actividad que la creacion del post ha terminado mediante el interactionListener
                        interactionListener?.onPostAdded()
                    },
                    { it ->
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
            when {
                error.messageResId != null ->
                    getString(error.messageResId)
                error.message != null ->
                    error.message
                else ->
                    getString(R.string.error_default)
            }

        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
    }

    // Funcion inline para limpieza y comprension de codigo
    private fun isValidForm() = inputContent.text.isNotEmpty()

    // Metodo para mostrar errores en las cajas de texto cuando esten vacias
    private fun showErrors() {
        if (inputContent.text.isEmpty())
            inputContent.error = getString(R.string.error_empty)
    }
}