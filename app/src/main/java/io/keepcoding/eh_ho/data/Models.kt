package io.keepcoding.eh_ho.data

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Topic(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    //val content: String,
    val date: Date = Date(),
    val posts: Int = 0,
    val views: Int = 0
) {

    // Añadimos un metodo que convierta objetos de JSON a Topic
    // Los metodos aqui incluidos estan declarados en un contexto estatico
    companion object {
        fun parseTopicsList(response: JSONObject): List<Topic> {
            val objectList = response.getJSONObject("topic_list")
                // Accedemos al array de topics con ...
                .getJSONArray("topics")

            val topics = mutableListOf<Topic>()
            // Iteramos los topics
            for (i in 0 until objectList.length()) {
                val parsedTopic = parseTopic(objectList.getJSONObject(i))
                topics.add(parsedTopic)
            }

            return topics
        }

        fun parseTopic(jsonObject: JSONObject): Topic {
            // La fecha recuperada en "created_at" viene como 2020-06-21T00:00:00:000Z
            // Para pasarla a nuestro formato necesitamos lo siguiente:
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()

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
    val MINUTE_MILLIS = 1000L * 60
    val HOUR_MILLIS = MINUTE_MILLIS * 60
    val DAY_MILLIS = HOUR_MILLIS * 24
    val MONTH_MILLIS = DAY_MILLIS * 30
    val YEAR_MILLIS = MONTH_MILLIS * 12

    // Esta clase nos sirve para devolver la tupla de informacion que devolvera el metodo getTimeOffSet
    data class TimeOffSet(val amount: Int, val unit: Int)

    // Metodo que nos devolvera la diferencia en tiempo con la fecha actual
    /**
     * Fecha de creación de topico '01/01/2020 10:00:00'
     * @param Date Fecha de consulta '01/01/2020 11:00:00'
     * @return { unit: "Hora", amount: 1 }
     **/
    fun getTimeOffSet(dateToCompare: Date = Date()) : TimeOffSet {
        // Necesitamos la fecha en milisegundos de la consulta
        val current = dateToCompare.time
        // La diferencia entre la current y la fecha del topic
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

        return TimeOffSet(0, Calendar.MINUTE)
    }
}


data class Post(
    val author: String,
    val content: String,
    val postDate: Date = Date()
){

    // Añadimos un metodo que convierta objetos de JSON a Post
    // Los metodos aqui incluidos estan declarados en un contexto estatico
    companion object {
        fun parsePostsList(response: JSONObject): List<Post> {
            val objectList = response.getJSONObject("post_stream")
                // Accedemos al array de topics con ...
                .getJSONArray("posts")

            val posts = mutableListOf<Post>()
            // Iteramos los topics
            for (i in 0 until objectList.length()) {
                val parsedPost = parsePost(objectList.getJSONObject(i))
                posts.add(parsedPost)
            }

            return posts
        }

        fun parsePost(jsonObject: JSONObject): Post {
            // La fecha recuperada en "created_at" viene como 2020-06-21T00:00:00:000Z
            // Para pasarla a nuestro formato necesitamos lo siguiente:
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()

            return Post(
                author = jsonObject.getString("username").toString(),
                content = jsonObject.getString("cooked").toString(),
                postDate = Date()
            )
        }
    }
}