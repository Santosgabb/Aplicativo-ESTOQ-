package com.appstocksmart.app.ui.usuario

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityEditarUsuarioBinding
import com.appstocksmart.app.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarUsuarioBinding
    private var idUsuario: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerPerfil()

        idUsuario = intent.getLongExtra("idUsuario", -1L)

        if (idUsuario == -1L) {
            Toast.makeText(this, "Usuário inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDadosUsuario()

        binding.btnSalvarEdicaoUsuario.setOnClickListener {
            salvarEdicao()
        }
    }

    private fun configurarSpinnerPerfil() {
        val perfis = listOf("GERENTE", "CAIXA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, perfis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEditarPerfilUsuario.adapter = adapter
    }

    private fun carregarDadosUsuario() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.buscarUsuarioPorId(idUsuario).enqueue(object : Callback<Usuario> {

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    val usuario = response.body()

                    if (usuario == null) {
                        Toast.makeText(
                            this@EditarUsuarioActivity,
                            "Usuário não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    binding.edtEditarNomeUsuario.setText(usuario.nome)
                    binding.edtEditarLoginUsuario.setText(usuario.login)
                    binding.edtEditarSenhaUsuario.setText(usuario.senha)

                    val posicaoPerfil = if (usuario.perfil == "GERENTE") 0 else 1
                    binding.spinnerEditarPerfilUsuario.setSelection(posicaoPerfil)

                } else {
                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "Erro ao buscar usuário: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(
                    this@EditarUsuarioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

    private fun salvarEdicao() {
        val nome = binding.edtEditarNomeUsuario.text.toString().trim()
        val login = binding.edtEditarLoginUsuario.text.toString().trim()
        val senha = binding.edtEditarSenhaUsuario.text.toString().trim()
        val perfil = binding.spinnerEditarPerfilUsuario.selectedItem.toString()

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioAtualizado = Usuario(
            id = idUsuario,
            nome = nome,
            login = login,
            senha = senha,
            perfil = perfil
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.atualizarUsuario(idUsuario, usuarioAtualizado)
            .enqueue(object : Callback<Usuario> {

                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EditarUsuarioActivity,
                            "Usuário atualizado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditarUsuarioActivity,
                            "Erro ao atualizar usuário: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(
                        this@EditarUsuarioActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}