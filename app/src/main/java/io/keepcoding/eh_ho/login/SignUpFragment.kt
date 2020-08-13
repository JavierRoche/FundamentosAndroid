package io.keepcoding.eh_ho.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.SignUpModel
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.inputPassword
import kotlinx.android.synthetic.main.fragment_sign_up.inputUsername
import kotlinx.android.synthetic.main.fragment_sign_up.labelCreateAccount

// Las clases que heredan de Fragment es porque se van a utilizar en una actividad como fragmentos
class SignUpFragment: Fragment() {
    // Definimos el InteractionListener que recogera los eventos de usuario sobre el fragmento
    var signUpInteractionListener: SignUpInteractionListener? = null


    /**
     * PROTOCOLS
     */

    // Protocolo con los metodos abstractos del fragmento que recogen las interaciones del usuario y que seran sobreescritos en la actividad
    interface SignUpInteractionListener {
        fun onSignUp(signUpModel: SignUpModel)
        fun onGoToSignIn()
    }


    /**
     * LIFE CYCLE
     */

    // Liga nuestro fragmento a su actividad
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is SignUpInteractionListener)
            signUpInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${SignUpInteractionListener::class.java.canonicalName}")
    }

    // Ejecuta una vez se ha creado la instancia del fragmento
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_sign_up)
    }

    // Marca la disponibilidad de la vista tras su creacion
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indica a la actividad del fragmento los metodos que seran llamados tras una interacion del usuario en el boton
        buttonSignUp.setOnClickListener {
            if (isValidForm()) {
                // Construimos la informacion para el cuerpo de los datos de registro
                val signUpModel = SignUpModel(
                    inputUsername.text.toString(),
                    inputEmail.text.toString(),
                    inputPassword.text.toString()
                )
                signUpInteractionListener?.onSignUp(signUpModel)

            } else {
                showErrors()
            }
        }

        // Indica a la actividad del fragmento los metodos que seran llamados tras una interacion del usuario en el label
        labelCreateAccount.setOnClickListener {
            signUpInteractionListener?.onGoToSignIn()
        }
    }

    // Desliga el fragmento de su actividad
    override fun onDetach() {
        super.onDetach()
        this.signUpInteractionListener = null
    }


    /**
     * PRIVATE FUNCTIONS
     */

    // Funcion inline para limpieza y comprension de codigo
    private fun isValidForm() = inputUsername.text.isNotEmpty() && inputEmail.text.isNotEmpty() &&
                                inputPassword.text.isNotEmpty() && inputConfirmPassword.text.isNotEmpty()

    // Metodo para mostrar errores en las cajas de texto cuando esten vacias
    private fun showErrors() {
        if (inputUsername.text.isEmpty())
            inputUsername.error = getString(R.string.error_empty)
        if (inputEmail.text.isEmpty())
            inputEmail.error = getString(R.string.error_empty)
        if (inputPassword.text.isEmpty())
            inputPassword.error = getString(R.string.error_empty)
        if (inputConfirmPassword.text.isEmpty())
            inputConfirmPassword.error = getString(R.string.error_empty)
    }
}