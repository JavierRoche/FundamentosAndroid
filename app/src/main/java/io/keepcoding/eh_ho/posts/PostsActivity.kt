package io.keepcoding.eh_ho.posts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.isFirstTimeCreated

const val EXTRA_TOPIC_ID = "TOPIC_ID"
const val EXTRA_TOPIC_TITLE = "TOPIC_TITLE"
const val TRANSACTION_ADD_POST = "add_post"

class PostsActivity: AppCompatActivity(), PostsTopicFragment.TopicInteractionListener,
                                          ReplyPostFragment.ReplyPostInteractionListener {

    /**
     * LIFE CYCLE
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        // Al crearse la actividad llamamos al creador padre
        super.onCreate(savedInstanceState)
        // Indicamos cual es el fichero xml que manejara la actividad
        setContentView(R.layout.activity_posts)

        // Recibimos la informacion del topic que nos mando la actividad llamante. Solo el id y el titulo del topic
        val topicId: String? = intent.getStringExtra(EXTRA_TOPIC_ID)
        val topicTitle: String? = intent.getStringExtra(EXTRA_TOPIC_TITLE)

        // Iniciamos una instancia del fragmento que contendra los posts con el numero de topic selecionado
        val postsTopicFragment: PostsTopicFragment = PostsTopicFragment.newInstance(topicId, topicTitle)

        // Comprobamos que sea la primera vez que se instancia la clase
        if (isFirstTimeCreated(savedInstanceState))
        // Indicamos con que fragmento inicia la actividad dentro del fragment_container definido en la vista xml
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, postsTopicFragment)
                .commit()
    }


    /**
     * USER ACTIONS
     */

    override fun onReplyTopic(topicId: String, topicTitle: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ReplyPostFragment(topicId, topicTitle))
            .addToBackStack(TRANSACTION_ADD_POST)
            .commit()
    }

    override fun onPostAdded() {
        // Conseguimos que al darle a enviar el post y crearse volvamos al topic, quitando de la pila el fragmento de creacion
        supportFragmentManager.popBackStack()
    }
}
