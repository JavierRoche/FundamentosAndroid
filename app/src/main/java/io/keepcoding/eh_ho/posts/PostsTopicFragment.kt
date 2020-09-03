package io.keepcoding.eh_ho.posts

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repos.PostsRepo
import io.keepcoding.eh_ho.inflate
import io.keepcoding.eh_ho.topics.TopicsFragment
import kotlinx.android.synthetic.main.fragment_create_topic.*
import kotlinx.android.synthetic.main.fragment_posts.*

const val ARG_TOPIC_ID = "topic_id"
const val ARG_TOPIC_TITLE = "topic_title"

class PostsTopicFragment: Fragment() {
    private var topicId: String = ""
    private var topicTitle: String = ""
    // Instancia del protocolo InteractionListener, que recogera los eventos de usuario sobre el fragmento
    private var topicInteractionListener: TopicInteractionListener? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null


    /**
     * STATIC INIT
     */

    // Este metodo estatico fija el argumento que le pasa la actividad
    companion object {
        fun newInstance(topicId: String?, topicTitle: String?): PostsTopicFragment {
            val fragment = PostsTopicFragment()
            val args = Bundle()
            // Recibimos el argumento desde la actividad a traves de un Bundle
            args.putString(ARG_TOPIC_ID, topicId)
            args.putString(ARG_TOPIC_TITLE, topicTitle)
            fragment.arguments = args
            // Devolvemos el fragmento ya con el argumnento instanciado
            return fragment
        }
    }


    /**
     * PROTOCOLS
     */

    // Protocolo con los metodos abstractos del fragmento que recogen las interaciones del usuario y que seran sobreescritos en la actividad
    interface TopicInteractionListener {
        fun onReplyTopic(topicId: String, topicTitle: String)
    }


    /**
     * LIFE CYCLE
     */

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Comprobamos que el contexto de la actividad implementa el protocolo
        if (context is TopicInteractionListener)
            topicInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${TopicsFragment.TopicsInteractionListener::class.java.canonicalName}")
    }

    // Ejecuta mientras se esta creando la instancia del fragmento
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fuerza la ejecucion de onCreateOptionsMenu indicando que habra un menu en el fragmento
        setHasOptionsMenu(true)
    }

    // Ejecuta si se ha invocado previamente el metodo setHasOptionsMenu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflamos nuestro archivo de recurso pasandole el menu contenedor donde se posiciona
        inflater.inflate(R.menu.menu_reply_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_posts)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Definimos el evento del elemento SwipeRefresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        swipeRefreshLayout?.setOnRefreshListener{
            // Cargamos los posts
            loadPosts()
        }

        // Guardamos en la variable local el numero de topic que se pulso
        topicId = this.arguments?.getString(ARG_TOPIC_ID).toString()
        topicTitle = this.arguments?.getString(ARG_TOPIC_TITLE).toString()
        // Fijamos el titulo del topic en la parte superior del fragmento
        activity?.title = topicTitle

        // Obtenemos los posts del topic en el que se ha hecho tap
        // Creamos el objeto adaptador
        val adapter = PostsAdapter()
        // Obtenemos los topics del repositorio
        adapter.setPosts(PostsRepo.posts)
        // El LayoutManager es el componente que decide como se van a distribuir los elementos en el ReciclerView
        listPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // Asigna el adaptador que se encarga de transformar los datos en vistas y presentarlas en el ReciclerView
        listPosts.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        // Cargamos los posts
        loadPosts()
    }

    // Desliga el fragmento de su actividad
    override fun onDetach() {
        super.onDetach()
        topicInteractionListener = null
    }


    /**
     * MENU USER ACTIONS
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Recorremos los items del menu
        when(item.itemId) {
            // Invocamos la aparicion del dialog de creacion de nuevo post
            R.id.action_reply -> this.topicInteractionListener?.onReplyTopic(topicId, topicTitle)
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * PRIVATE FUNCTIONS
     */

    private fun loadPosts() {
        // Accedemos al repositorio de posts con un context seguro
        context?.let { it ->
            PostsRepo.getPosts(topicId, it.applicationContext,
                {
                    // Rellenamos el ViewHolder con la lista de topics
                    (listPosts.adapter as PostsAdapter).setPosts(it)
                    swipeRefreshLayout!!.isRefreshing = false
                },
                {
                    // Mostramos indicacion de que algo no fue bien
                    val message =
                        when {
                            it.messageResId != null -> {
                                getString(it.messageResId)
                            }
                            it.message != null -> {
                                it.message
                            }
                            else ->
                                getString(R.string.error_default)
                        }

                    Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
                    swipeRefreshLayout!!.isRefreshing = false
                }
            )
        }
    }
}