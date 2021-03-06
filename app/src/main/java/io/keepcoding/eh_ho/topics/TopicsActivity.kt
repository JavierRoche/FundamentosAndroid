package io.keepcoding.eh_ho.topics

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.keepcoding.eh_ho.*
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.repos.UserRepo
import io.keepcoding.eh_ho.login.LoginActivity
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_ID
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_TITLE
import io.keepcoding.eh_ho.posts.PostsActivity

const val TRANSACTION_CREATE_TOPIC = "create_topic"

// AppCompatActivity es necesario para soportar funciones modernas en dispositivos con versiones de Android antiguas
// InteractionListener de cada clase nos permite capturar los eventos abstractos definidos en los protocolos de cada fragmento

class TopicsActivity : AppCompatActivity(), TopicsFragment.TopicsInteractionListener,
                                            TopicsFragmentRetry.TopicsRetryInteractionListener,
                                            CreateTopicFragment.CreateTopicInteractionListener {
    /**
     * LIFE CYCLE
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        // Al crearse la actividad llamamos al creador padre
        super.onCreate(savedInstanceState)

        // Indicamos cual es el fichero xml que manejara la actividad
        setContentView(R.layout.activity_topics)

        // Comprobamos que sea la primera vez que se instancia la clase
        if (isFirstTimeCreated(savedInstanceState))
            // Indicamos con que fragmento inicia la actividad dentro del fragment_container definido en la vista xml
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TopicsFragment())
                .commit()
    }


    /**
     * USER ACTIONS
     */

    // Metodo abstracto definido como protocolo en el fragmento para pasar al fragmento de creacion de nuevo topic
    override fun onCreateTopic() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CreateTopicFragment())
            // Con .addToBackStack le decimos a la pila de actividades/fragmentos que elimine de la pila solo el fragmento actual
            .addToBackStack(TRANSACTION_CREATE_TOPIC)
            .commit()
    }

    // Metodo abstracto definido como protocolo en el fragmento para ver detalle del topic
    override fun onShowPosts(topic: Topic) {
        // Declaramos el Intent y le pasamos el contexto de la actividad y la actividad a donde hay que ir
        val intent = Intent(this, PostsActivity::class.java)
        // Mediante el putExtra podemos pasar informacion a la siguiente actividad
        intent.putExtra(EXTRA_TOPIC_ID, topic.id)
        intent.putExtra(EXTRA_TOPIC_TITLE, topic.title)
        startActivity(intent)
    }

    // Sobreescritura del metodo de Logout
    override fun onLogout() {
        // Borramos los datos de preferencias
        UserRepo.logout(this.applicationContext)
        // Vamos a la actividad inicial
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // Al poner finish la actividad donde estaba se va a destruir y no se queda en el back de actividades
        finish()
    }

    // Sobreescritura del metodo que controla que ha habido un error al cargar los topics
    override fun loadingTopicsError() {
        // Mostramos el fragmento de feedback al usuario que posibilita la recarga
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TopicsFragmentRetry())
            .commit()
    }

    // Si el usuario pulsa en reintentar se vuelve a lanzar el segmento que carga la lista de topics
    override fun onRetry() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TopicsFragment())
            .commit()
    }

    // Metodo de comunicacion con el fragmento despues de la creacion del topic
    // Se hace desde aqui porque es mas recomendable. Imaginemos que no queremos volver a la lista sino que queremos que se muestre el detalle del topic
    override fun onTopicCreated() {
        // Conseguimos que al darle a enviar el topic y crearse volvamos a la lista de topics, quitando de la pila el fragmento de creacion
        supportFragmentManager.popBackStack()
    }
}