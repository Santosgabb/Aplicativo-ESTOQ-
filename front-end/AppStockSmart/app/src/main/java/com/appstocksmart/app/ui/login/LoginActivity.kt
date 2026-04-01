package com.appstocksmart.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityLoginBinding
import com.appstocksmart.app.model.Usuario
import com.appstocksmart.app.ui.menu.MenuActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener {
            fazerLogin()
        }
    }

    private fun fazerLogin() {

        val login = binding.edtLogin.text.toString().trim()
        val senha = binding.edtSenha.text.toString().trim()

        if (login.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha login e senha", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        val usuarioRequest = Usuario(
            id = null,
            nome = "",
            login = login,
            senha = senha,
            perfil = "CAIXA"
        )

        apiService.login(usuarioRequest).enqueue(object : Callback<Usuario> {

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {

                if (response.isSuccessful) {

                    val usuario = response.body()

                    if (usuario != null) {

                        val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                        intent.putExtra("idUsuario", usuario.id ?: -1L)
                        intent.putExtra("nomeUsuario", usuario.nome)
                        intent.putExtra("perfilUsuario", usuario.perfil)

                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@LoginActivity, "Usuário inválido", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(this@LoginActivity, "Login inválido", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Erro de conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}