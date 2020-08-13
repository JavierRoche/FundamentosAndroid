package io.keepcoding.eh_ho.topics

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.item_topic.view.*
import java.lang.IllegalArgumentException
import java.util.*

// El Adapter indica que elementos se van a pintar en la interface de usuario
// Al heredar de RecyclerView.Adapter nos obliga a sobreescribir los metodos getItemCount, onCreateViewHolder y onBindViewHolder
// La clase abstracta RecyclerView.Adapter, devuelve un generico de la inner class que definiremos en cada Adapter. Sera como una plantilla y se llama ViewHolder
// Tambien indicamos a la clase Adapter, que recibira un listener para controlar los clics en las celdas

class TopicsAdapter(val topicClickListener: ((Topic) -> Unit)? = null) : RecyclerView.Adapter<TopicsAdapter.TopicHolder>() {
    // Lista de elementos interna mutable para añadir o quitar elementos
    private val topics = mutableListOf<Topic>()

    // El adaptador comunica a su actividad mediante un listener que se ha pulsado en una celda
    private val listener : ((View) -> Unit) = {
        // Comprobamos que el casteo no falla
        if (it.tag is Topic) {
            // Invocamos al listener que declaramos en el Adapter y le pasamos el topic que se pulso
            topicClickListener?.invoke(it.tag as Topic)
        } else {
            throw IllegalArgumentException("Topic item view has not a Topic Data as a tag")
        }
    }

    // Indicara el numero de elementos que habra en la lista accediendo a la estructura de datos
    override fun getItemCount(): Int {
        return topics.size
    }

    // Devolvera el ViewHolder plantilla para cada elemento
    override fun onCreateViewHolder(list: ViewGroup, viewType: Int): TopicHolder {
        // Le pasamos el recurso xml al que tiene que acceder como la vista que contendra cada celda
        // El LayoutInflater consigue un elemento de tipo View a partir de nuestro recurso xml contenedor de celdas
        val view = list.inflate(R.layout.item_topic)
        return TopicHolder(view)
    }

    // Conectamos el modelo de datos con nuestro ViewHolder
    // Recibe la plantilla de la celda a la cual tiene que asignar informacion y la posicion
    override fun onBindViewHolder(holder: TopicHolder, position: Int) {
        // Obtenemos el elemento por posicion de la lista de elementos
        val topic = topics[position]
        // El holder es el contenedor de la vista
        holder.topic = topic
        // Ponemos un listener en el ViewHolder para cada celda y controlar los clics en ellas
        holder.itemView.setOnClickListener(listener)
    }

    // Metodo que recibe la lista de topics que se van a mostrar
    fun setTopics(topics: List<Topic>) {
        // Para ello primero vaciamos la lista, luego la llenamos de nuevo, y notificamos a la vista
        this.topics.clear()
        // Carga en la mutableList la lista de topics que recibe el metodo
        this.topics.addAll(topics)
        notifyDataSetChanged()
    }

    // La inner class hereda de ViewHolder y poder asi rellenar el contenedor de la vista
    inner class TopicHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var topic: Topic? = null
        set(value) {
            field = value
            // Al tag del elemento de la vista le indicamos cada elemento de celda
            itemView.tag = field

            field?.let {
                itemView.labelTitle.text = it.title
                // Fijamos el numero de posts
                itemView.labelPosts.text = it.posts.toString()
                itemView.labelViews.text = it.views.toString()
                // Invocamos la funcion que nos devuelve el tiempo que hace que se escribio el topic
                setTimeOffSet(it.getTimeOffSet())
            }
        }

        // Validamos la unidad del TimeOffSet
        private fun setTimeOffSet(timeOffSet: Topic.TimeOffSet) {

            // El when devolvera un valor, que sera el identificador de plurales de los años/meses/dias/horas/minutos
            val quantityString = when (timeOffSet.unit) {
                // Los quantities en base a una cantidad decide el formato que te devuelve
                Calendar.YEAR -> R.plurals.years
                Calendar.MONTH -> R.plurals.months
                Calendar.DAY_OF_MONTH -> R.plurals.days
                Calendar.HOUR -> R.plurals.hours
                else -> R.plurals.minutes
            }

            // Decidimos la etiqueta que se mostrara en el listado
            if (timeOffSet.amount == 0) {
                itemView.labelDate.text =
                    this.itemView.context.resources.getString(R.string.minutes_zero)
            } else {
                itemView.labelDate.text =
                    this.itemView.context.resources.getQuantityString(quantityString, timeOffSet.amount, timeOffSet.amount)
            }
        }
    }
}
