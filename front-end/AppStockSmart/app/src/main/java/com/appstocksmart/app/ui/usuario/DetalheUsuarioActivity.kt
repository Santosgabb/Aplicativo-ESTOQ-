package com.appstocksmart.app.ui.usuario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityDetalheUsuarioBinding
import com.appstocksmart.app.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalheUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheUsuarioBinding
    private var idUsuario: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalheUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idUsuario = intent.getLongExtra("idUsuario", -1L)

        if (idUsuario == -1L) {
            Toast.makeText(this, "Usuário inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDetalhes()

        binding.btnEditarDetalheUsuario.setOnClickListener {
            val intent = Intent(this, EditarUsuarioActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)
        }

        binding.btnExcluirDetalheUsuario.setOnClickListener {
            excluirUsuario()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDetalhes()
    }

    private fun carregarDetalhes() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.buscarUsuarioPorId(idUsuario).enqueue(object : Callback<Usuario> {

            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    val usuario = response.body()

                    if (usuario == null) {
                        Toast.makeText(
                            this@DetalheUsuarioActivity,
                            "Usuário não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    binding.txtDetalheNomeUsuario.text = "Nome: ${usuario.nome}"
                    binding.txtDetalheLoginUsuario.text = "Login: ${usuario.login}"
                    binding.txtDetalhePerfilUsuario.text = "Perfil: ${usuario.perfil}"

                } else {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Erro ao buscar usuário: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(
                    this@DetalheUsuarioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

    private fun excluirUsuario() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.deletarUsuario(idUsuario).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Usuário excluído com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Erro ao excluir usuário: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@DetalheUsuarioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}