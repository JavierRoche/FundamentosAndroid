package io.keepcoding.eh_ho.data

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * TOPIC DATA MODEL
 */

// Modelo de datos para un Topic
data class Topic(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    //val content: String,
    val date: Date = Date(),
    val posts: Int = 0,
    val views: Int = 0
) {

    // Los metodos aqui incluidos estan declarados en un contexto estatico
    companion object {
        
        // Metodo que transforma una lista de JSONObject recibido de un API en un modelo de datos Topic
        fun parseTopicsList(response: JSONObject): List<Topic> {
            // Accedemos al objeto que internamente contiene un array de elementos con .getJSONObject
            val objectList = response.getJSONObject("topic_list")
                // Accedemos al array de elementos con .getJSONArray
                .getJSONArray("topics")

            // Definimos la lista mutable del modelo de datos
            val topics = mutableListOf<Topic>()
            
            // Iteramos los topics del array
            for (i in 0 until objectList.length()) {
                // Parseamos cada elemento al modelo de datos y lo añadimos a la lista mutable
                val parsedTopic = parseTopic(objectList.getJSONObject(i))
                topics.add(parsedTopic)
            }

            return topics
        }

        // Metodo que transforma cada JSONObject que se esta iterando en un modelo de datos Topic
        private fun parseTopic(jsonObject: JSONObject): Topic {
            // La fecha recuperada en "created_at" llega en un formato (2020-06-21T00:00:00:000Z) que necesitamos transformar
            val date = jsonObject.getString("created_at").replace("Z", "+0000")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()

            // Devolvemos el modelo de datos del topic con los datos extraidos del JSONObject
            return Topic(
                id = jsonObject.getInt("id").toString(),
                title = jsonObject.getString("title"),
                date = dateFormatted,
                posts = jsonObject.getInt("posts_count"),
                views = jsonObject.getInt("views")
            )
        }
    }

    // Constantes de referencia equivalentes. La L transforma el Int en Long para que no se desborde a partir del año
    private val MINUTE_MILLIS = 1000L * 60
    private val HOUR_MILLIS = MINUTE_MILLIS * 60
    private val DAY_MILLIS = HOUR_MILLIS * 24
    private val MONTH_MILLIS = DAY_MILLIS * 30
    private val YEAR_MILLIS = MONTH_MILLIS * 12

    // Este modelo de datos sera devuelto por el metodo getTimeOffSet
    data class TimeOffSet(
        val amount: Int, 
        val unit: Int
    )

    /**
     * Fecha de creación de topico '01/01/2020 10:00:00'
     * param:  Fecha de consulta '01/01/2020 11:00:00'
     * return: { unit: amount: 1, "Hora" }
     **/

    // Metodo que nos devolvera la diferencia en tiempo con la fecha actual
    fun getTimeOffSet(dateToCompare: Date = Date()): TimeOffSet {
        // Fecha del instante de la consulta en milisegundos
        val current = dateToCompare.time
        // Diferencia entre la current y la fecha del topic en milisegundos
        val diff = current - this.date.time

        // Valoramos si hay años de diferencia
        val years = diff / YEAR_MILLIS
        if (years > 0) return TimeOffSet(
            years.toInt(),
            Calendar.YEAR
        )

        // Valoramos si hay meses de diferencia
        val months = diff / MONTH_MILLIS
        if (months > 0) return TimeOffSet(
            months.toInt(),
            Calendar.MONTH
        )

        // Valoramos si hay dias de diferencia
        val days = diff / DAY_MILLIS
        if (days > 0) return TimeOffSet(
            days.toInt(),
            Calendar.DAY_OF_MONTH
        )

        // Valoramos si hay horas de diferencia
        val hours = diff / HOUR_MILLIS
        if (hours > 0) return TimeOffSet(
            hours.toInt(),
            Calendar.HOUR
        )

        // Valoramos si hay minutos de diferencia
        val minutes = diff / MINUTE_MILLIS
        if (minutes > 0) return TimeOffSet(
            minutes.toInt(),
            Calendar.MINUTE
        )

        // Ni siquiera hay minutos de diferencia
        return TimeOffSet(0, Calendar.MINUTE)
    }
}


/**
 * POST DATA MODEL
 */

// Modelo de datos para un Post
data class Post(
    val author: String,
    val content: String,
    val postDate: String
){

    // Los metodos aqui incluidos estan declarados en un contexto estatico
    companion object {

        // Metodo que transforma una lista de JSONObject recibido de un API en un modelo de datos Post
        fun parsePostsList(response: JSONObject): List<Post> {
            // Accedemos al objeto que internamente contiene un array de elementos con .getJSONObject
            val objectList = response.getJSONObject("post_stream")
                // Accedemos al array de elementos con .getJSONArray
                .getJSONArray("posts")

            // Definimos la lista mutable del modelo de datos
            val posts = mutableListOf<Post>()

            // Iteramos los posts del array
            for (i in 0 until objectList.length()) {
                // Parseamos cada elemento al modelo de datos y lo añadimos a la lista mutable
                val parsedPost = parsePost(objectList.getJSONObject(i))
                posts.add(parsedPost)
            }

            return posts
        }

        // Metodo que transforma cada JSONObject que se esta iterando en un modelo de datos Post
        private fun parsePost(jsonObject: JSONObject): Post {
            // La fecha recuperada en "created_at" llega en un formato (2020-06-21T00:00:00:000Z) que necesitamos transformar
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()
            // Damos a la fecha un formato mas friendly
            val dateCleanFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
            val dateCleanFormatted = dateCleanFormat.format(dateFormatted)

            // Devolvemos el modelo de datos del post con los datos extraidos del JSONObject
            return Post(
                author = jsonObject.getString("username").toString(),
                content = jsonObject.getString("cooked").toString(),
                postDate = dateCleanFormatted
            )
        }
    }
}