package io.keepcoding.eh_ho.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.api.SignInModel
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_sign_in.*

// Las clases que heredan de Fragment es porque se van a utilizar en una actividad como fragmentos
class SignInFragment: Fragment() {
    // Definimos el InteractionListener que recogera los eventos de usuario sobre el fragmento
    private var signInInteractionListener: SignInInteractionListener? = null


    /**
     * PROTOCOLS
     */

    // Protocolo con los metodos abstractos del fragmento que recogen las interaciones del usuario y que seran sobreescritos en la actividad
    interface SignInInteractionListener {
        fun onSignIn(signInModel: SignInModel)
        fun onGoToSignUp()
    }


    /**
     * LIFE CYCLE
     */

    // Liga nuestro fragmento a su actividad
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Comprobamos que el contexto de la actividad implementa el protocolo
        if (context is SignInInteractionListener)
            signInInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${SignInInteractionListener::class.java.canonicalName}")
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_sign_in)
    }

    // Marca la disponibilidad de la vista tras su creacion
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indica a la actividad del fragmento los metodos que seran llamados tras una interacion del usuario en el boton
        buttonLogin.setOnClickListener {
            if (isValidForm()) {
                // Cuando se pulsa en login creamos el modelo de datos para user/pass
                val signInModel = SignInModel(
                    inputUsername.text.toString(),
                    inputPassword.text.toString()
                )
                signInInteractionListener?.onSignIn(signInModel)

            } else {
                showErrors()
            }
        }

        // Indica a la actividad del fragmento los metodos que seran llamados tras una interacion del usuario en el label
        labelCreateAccount.setOnClickListener {
            signInInteractionListener?.onGoToSignUp()
        }
    }

    // Desliga el fragmento de su actividad
    override fun onDetach() {
        super.onDetach()
        this.signInInteractionListener = null
    }


    /**
     * PRIVATE FUNCTIONS
     */

    // Funcion inline para limpieza y comprension de codigo
    private fun isValidForm() = inputUsername.text.isNotEmpty() && inputPassword.text.isNotEmpty()

    // Metodo para mostrar errores en las cajas de texto cuando esten vacias
    private fun showErrors() {
        if (inputUsername.text.isEmpty())
            inputUsername.error = getString(R.string.error_empty)
        if (inputPassword.text.isEmpty())
            inputPassword.error = getString(R.string.error_empty)
    }
}