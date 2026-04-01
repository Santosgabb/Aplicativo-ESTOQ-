package com.appstocksmart.app.ui.usuario

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityCadastroUsuarioBinding
import com.appstocksmart.app.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroUsuarioBinding
    private var salvandoUsuario = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerPerfil()

        binding.btnSalvarUsuario.setOnClickListener {
            if (salvandoUsuario) return@setOnClickListener
            salvarUsuario()
        }
    }

    private fun configurarSpinnerPerfil() {
        val perfis = listOf("GERENTE", "CAIXA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, perfis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPerfilUsuario.adapter = adapter
    }

    private fun salvarUsuario() {
        val nome = binding.edtNomeUsuario.text.toString().trim()
        val login = binding.edtLoginUsuario.text.toString().trim()
        val senha = binding.edtSenhaUsuario.text.toString().trim()
        val perfil = binding.spinnerPerfilUsuario.selectedItem.toString()

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(
            id = null,
            nome = nome,
            login = login,
            senha = senha,
            perfil = perfil
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        salvandoUsuario = true
        binding.btnSalvarUsuario.isEnabled = false

        apiService.salvarUsuario(usuario).enqueue(object : Callback<Usuario> {

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                salvandoUsuario = false
                binding.btnSalvarUsuario.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        "Usuário cadastrado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e(
                        "USUARIO_API",
                        "Erro ${response.code()} - $errorBody"
                    )

                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        "Erro ao cadastrar usuário: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                salvandoUsuario = false
                binding.btnSalvarUsuario.isEnabled = true

                android.util.Log.e("USUARIO_API", "Falha", t)

                Toast.makeText(
                    this@CadastroUsuarioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}