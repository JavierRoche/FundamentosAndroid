package io.keepcoding.eh_ho.topics

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.fragmentContainer
import kotlinx.android.synthetic.main.activity_login.viewLoading
import kotlinx.android.synthetic.main.activity_topics.*
import kotlinx.android.synthetic.main.fragment_topics.*

class TopicsFragment: Fragment() {
    // Definimos una instancia del protocolo InteractionListener, que recogera los eventos de usuario sobre el fragmento
    var topicsInteractionListener: TopicsInteractionListener? = null

    /**
     * PROTOCOLS
     */

    // Protocolo con los metodos abstractos del fragmento que recogen las interaciones del usuario y que seran sobreescritos en la actividad
    interface TopicsInteractionListener {
        fun onCreateTopic()
        fun onShowPosts(topic: Topic)
        fun onLogout()
        fun loadingTopicsError()
    }


    /**
     * LIFE CYCLE
     */

    // Liga nuestro fragmento a su actividad
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Comprobamos que el contexto de la actividad implementa el protocolo
        if (context is TopicsInteractionListener)
            topicsInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${TopicsInteractionListener::class.java.canonicalName}")
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
        inflater.inflate(R.menu.menu_topics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_topics)
    }

    // Marca la disponibilidad de la vista tras su creacion
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creamos el adaptador que le dira al fragmento que hacer cuando se le de a un elemento de la lista
        val adapter = TopicsAdapter {
            this.topicsInteractionListener?.onShowPosts(it)
        }

        // Creamos el adaptador que le dira al fragmento que hacer cuando se le de al boton nuevo topic
        buttonCreate.setOnClickListener {
            this.topicsInteractionListener?.onCreateTopic()
        }

        // Obtenemos los topics del repositorio
        adapter.setTopics(TopicsRepo.topics)
        // El LayoutManager es el componente que decide como se van a distribuir los elementos en el ReciclerView
        listTopics.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // Añade una linea horizontal que divide cada topic
        listTopics.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        // Asigna el adaptador que se encarga de transformar los datos en vistas y presentarlas en el ReciclerView
        listTopics.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        // Accedemos al repositorio de topics con un context seguro
        context?.let {
            TopicsRepo.getTopics(it.applicationContext,
                {
                    // Ocultamos la vista de Loading que se muestra al arrancar el fragmento
                    viewLoading.visibility = View.INVISIBLE

                    // Rellenamos el ViewHolder con la lista de topics
                    (listTopics.adapter as TopicsAdapter).setTopics(it)
                },
                {
                    // Ocultamos la vista de Loading que se muestra al arrancar el fragmento
                    viewLoading.visibility = View.INVISIBLE

                    // Devolvemos a la actividad el control sobre el error
                    topicsInteractionListener?.loadingTopicsError()
                }
            )
        }
    }

    // Desliga el fragmento de su actividad
    override fun onDetach() {
        super.onDetach()
        topicsInteractionListener = null
    }


    /**
     * MENU USER ACTIONS
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Recorremos los items del menu
        when(item.itemId) {
            // Invocamos la ejecucion del logout borrando datos y regresando a la actividad inicial
            R.id.action_logout -> this.topicsInteractionListener?.onLogout()
        }
        return super.onOptionsItemSelected(item)
    }
}