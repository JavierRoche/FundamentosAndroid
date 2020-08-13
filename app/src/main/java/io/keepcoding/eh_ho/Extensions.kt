package io.keepcoding.eh_ho

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

// Las extensiones nos permiten añadir metodos a las clases sin tener que tocar el fuente de la clase

// Funcion inline que podemos invocar desde cualquier actividad
fun AppCompatActivity.isFirstTimeCreated(savedInstanceState: Bundle?) : Boolean
        = savedInstanceState == null

// Funcion inline que extiende el container del ViewGroup
fun ViewGroup.inflate(idLayout: Int, attachToRoot: Boolean = false): View
        = LayoutInflater.from(this.context).inflate(idLayout, this, attachToRoot)