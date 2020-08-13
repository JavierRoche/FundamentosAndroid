package io.keepcoding.eh_ho.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.SignInModel
import io.keepcoding.eh_ho.data.SignUpModel
import io.keepcoding.eh_ho.data.UserRepo
import io.keepcoding.eh_ho.topics.TopicsActivity
import io.keepcoding.eh_ho.isFirstTimeCreated
import kotlinx.android.synthetic.main.activity_login.*

// AppCompatActivity es necesario para soportar funciones modernas en dispositivos con versiones de Android antiguas
// Los InteractionListener de cada fragmento nos permite capturaren la actividad los eventos abstractos definidos en los protocolos de cada fragmento
// Como tenemos dos fragmentos para esta actividad incluimos ambos

class LoginActivity: AppCompatActivity(), SignInFragment.SignInInteractionListener, SignUpFragment.SignUpInteractionListener {
    // Definimos los objetos de cada fragmento que usara la actividad
    private val signInFragment = SignInFragment()
    private val signUpFragment = SignUpFragment()

    /**
     * LIFE CYCLE
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        // Al crearse la actividad llamamos al creador padre
        super.onCreate(savedInstanceState)
        // Indicamos cual es el fichero xml que manejara la actividad
        setContentView(R.layout.activity_login)

        // Comprobamos que sea la primera vez que se instancia la clase
        if (isFirstTimeCreated(savedInstanceState)) {
            // Si el usuario ya esta logueado vamos directamente a showTopics()
            if (UserRepo.isLogged(this.applicationContext)) {
                showTopics()

            } else {
                // Indicamos con que fragmento inicia la actividad dentro del fragment_container definido en la vista xml
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, signInFragment)
                    .commit()
            }
        }
    }


    /**
     * USER ACTIONS
     */

    // Metodo abstracto definido como protocolo en el fragmento para loguearse: SignInFragment
    override fun onSignIn(signInModel: SignInModel) {
        // Switcheamos entre la vista de carga
        enableLoading()
        // Ejecutamos la peticion al server
        // Le pasamos el contexto general de la app, el modelo de datos de user y una lambda que le diga que hacer en caso de exito en el login
        UserRepo.signIn(
            this.applicationContext,
            signInModel,
            { showTopics() },
            { error ->
                enableLoading(false)
                handleError(error)
            })
    }

    // Metodo abstracto definido como protocolo en el fragmento para loguearse: SignInFragment
    override fun onGoToSignUp() {
        // Indicamos con que fragmento inicia el fragmento de registro
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signUpFragment)
            .commit()
    }

    // Metodo abstracto definido como protocolo en el fragmento para registrarse: SignUpFragment
    override fun onSignUp(signUpModel: SignUpModel) {
        // Switcheamos entre la vista de carga
        enableLoading()
        // Mandamos la request al server
        UserRepo.signUp(this.applicationContext,
            signUpModel,
            {
                enableLoading(false)
                // Revisamos la respuesta del servidor
                Snackbar.make(container, R.string.message_sign_up, Snackbar.LENGTH_LONG).show()
            },
            {
                enableLoading(false)
                handleError(it)
            }
        )
    }

    // Metodo abstracto definido como protocolo en el fragmento para registrarse: SignUpFragment
    override fun onGoToSignIn() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signInFragment)
            .commit()
    }


    /**
     * PRIVATE FUNCTIONS
     */

    // Metodo privado que lanza un Intent, que no es mas que un push a otra pantalla o actividad
    private fun showTopics() {
        // Al Intent hay que pasarse el contexto, que es la propia actividad y la actividad destino
        val intent: Intent = Intent(this, TopicsActivity::class.java)
        // Despues, hacer el startActivity
        startActivity(intent)
        finish()
    }

    // Metodo privado que activa o desactiva la vista de carga
    private fun enableLoading(enabled: Boolean = true) {
        if (enabled) {
            // Escondemos el contenedor de los fragmentos, sea el de login o el de registro
            fragmentContainer.visibility = View.INVISIBLE
            // Accedemos a la vista de carga para hacerla visible
            viewLoading.visibility = View.VISIBLE

        } else {
            fragmentContainer.visibility = View.VISIBLE
            // Accedemos a la vista de carga para hacerla invisible
            viewLoading.visibility = View.INVISIBLE
        }
    }

    // Funcion que maneja el resultado del error
    private fun handleError(error: RequestError) {
        if (error.messageResId != null)
            Snackbar.make(container, error.messageResId, Snackbar.LENGTH_LONG).show()
        else if (error.message != null)
            Snackbar.make(container, error.message, Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(container, R.string.error_default, Snackbar.LENGTH_LONG).show()
    }


    /**
     * ASYNC TASKS METHODS
     */
    // El metodo de simulacion de la carga recibira el modelo de datos de los datos de usuario
//    private fun simulateLoading(signInModel: SignInModel) {
        /**
         * Tareas asincronas utilizando un Handler
         */

        // Creamos un Runnable que creara un thread alternativo donde se ejecutara su codigo
//        val runnable = Runnable {
//            Thread.sleep(3000)
//            // Con .post le indicamos que el codigo se ejecute en el thread principal, cuando termine el alternativo
//            viewLoading.post {
//                // Accedemos a guardar los datos del user
//                // No le pasamos el contexto como tal, asi que le pasamos un contexto que nunca muera como applicationContext, para evitar memory lik
//                UserRepo.signIn(this.applicationContext, signInModel.username)
//                //Mostramos la siguiente pantalla
//                showTopics()
//            }
//        }
//
//        Thread(runnable).start()

        /**
         * Tareas asincronas utilizando un AsyncTask
         */

        // Creamos una clase anonima que herede de AsyncTask
//        val task: AsyncTask<Long, Void, Boolean> = object : AsyncTask<Long, Void, Boolean>() {
//            // Se ejecuta en un nuevo hilo
//            // vararg es un listado de argumentos y accederemos a su primera posicion
//            override fun doInBackground(vararg time: Long?): Boolean {
//                Thread.sleep(time[0] ?: 3000)
//                return true
//            }
//
//            // Se ejecuta en el hilo principal al terminar la tarea asincrona doInBackground
//            override fun onPostExecute(result: Boolean?) {
//                super.onPostExecute(result)
//                showTopics()
//            }
//        }
//        // Ejecutamos la tarea asincrona
//        task.execute(5000)
//    }
}