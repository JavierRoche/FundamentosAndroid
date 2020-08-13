package io.keepcoding.eh_ho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import kotlinx.android.synthetic.main.activity_posts.*

const val EXTRA_TOPIC_ID = "TOPIC_ID"

class PostsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        // Recibimos la informacion del topic que nos mando la actividad llamante. Solo el id del topic
        val topicId: String = intent.getStringExtra(EXTRA_TOPIC_ID) ?: ""
        // Mediante el metodo del Repo recuperamos la informacion completa del topic con el id que nos paso la actividad llamante
        val topic: Topic? = TopicsRepo.getTopic(topicId)

        // Informamos el componente de texto, previa validacion del posible nulo
        topic?.let {
            labelTitle.text = it.title
        }
    }
}