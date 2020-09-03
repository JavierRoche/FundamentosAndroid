package io.keepcoding.eh_ho.posts

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Post
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.item_post.view.*


class PostsAdapter: RecyclerView.Adapter<PostsAdapter.PostHolder>() {
    // Lista de elementos interna mutable para añadir o quitar elementos
    private val posts = mutableListOf<Post>()

    // Indicara el numero de elementos que habra en la lista accediendo a la estructura de datos
    override fun getItemCount(): Int {
        return posts.size
    }

    // Devolvera el ViewHolder plantilla para cada elemento. La lista recibida es el RecyclerView
    override fun onCreateViewHolder(list: ViewGroup, viewType: Int): PostHolder {
        // Le pasamos el recurso xml al que tiene que acceder como la vista que contendra cada celda
        // El LayoutInflater consigue un elemento de tipo View a partir de nuestro recurso xml contenedor de celdas
        val view = list.inflate(R.layout.item_post)
        // Devolvemos el Holder indicando cual es la vista que manejara
        return PostHolder(view)
    }

    // Conectamos el modelo de datos con nuestro ViewHolder
    // Recibe la plantilla de la celda a la cual tiene que asignar informacion y la posicion
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        // Obtenemos el elemento por posicion de la lista de elementos
        val post = posts[position]
        // El holder es el contenedor de la vista que se creo en el onCreateViewHolder
        holder.post = post
    }

    // Metodo que recibe la lista de topics que se van a mostrar
    fun setPosts(posts: List<Post>) {
        // Para ello primero vaciamos la lista, luego la llenamos de nuevo, y notificamos a la vista
        this.posts.clear()
        // Carga en la mutableList la lista de topics que recibe el metodo
        this.posts.addAll(posts)
        // El adaptador vuelve a realizar el proceso de creacion de los ViewHolders
        notifyDataSetChanged()
    }


    /**
     * VIEW HOLDER
     */

    // Clase que tiene la logica para el contenedor de la vista
    // En el constructor de la clase ViewHolder pasamos la vista que tendra que administrar
    inner class PostHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var post: Post? = null
            // Asignamos
            @RequiresApi(Build.VERSION_CODES.N)
            set(value) {
                field = value
                // Al tag del elemento de la vista le indicamos cada elemento de celda
                itemView.tag = field

                field?.let {
                    itemView.postAuthor.text = it.author
                    itemView.postContent.text = Html.fromHtml(it.content, FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
                    itemView.postDate.text = it.postDate
                }
            }
    }
}