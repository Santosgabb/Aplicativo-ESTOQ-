package com.appstocksmart.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.ui.login.LoginActivity

/*
 * MainActivity usada apenas como porta de entrada do app.
 * Ela abre a tela de login e encerra.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abre a tela de login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Fecha a MainActivity para não voltar nela
        finish()
    }
}