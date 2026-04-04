package com.appstocksmart.app.ui.usuario

import android.content.Intent
import android.os.Bundle
import android.view.View
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

    // ID do usuário que está sendo visualizado
    private var idUsuario: Long = -1L

    // ID do usuário que está logado no sistema
    private var idUsuarioLogado: Long = -1L

    private var usuarioAtual: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalheUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idUsuario = intent.getLongExtra("idUsuario", -1L)
        idUsuarioLogado = intent.getLongExtra("idUsuarioLogado", -1L)

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
            inativarUsuario()
        }

        // Se você estiver usando botão de reativar, pode manter
        binding.btnReativarDetalheUsuario.setOnClickListener {
            reativarUsuario()
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

                    usuarioAtual = usuario

                    binding.txtDetalheNomeUsuario.text = "Nome: ${usuario.nome}"
                    binding.txtDetalheLoginUsuario.text = "Login: ${usuario.login}"
                    binding.txtDetalhePerfilUsuario.text = "Perfil: ${usuario.perfil}"

                    configurarBotoes(usuario)

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

    private fun configurarBotoes(usuario: Usuario) {
        val ativo = usuario.ativo ?: true

        // Se estiver ativo, mostra botão de inativar
        if (ativo) {
            binding.btnExcluirDetalheUsuario.visibility = View.VISIBLE
            binding.btnReativarDetalheUsuario.visibility = View.GONE
        } else {
            binding.btnExcluirDetalheUsuario.visibility = View.GONE
            binding.btnReativarDetalheUsuario.visibility = View.VISIBLE
        }

        // Se o usuário da tela for o mesmo que está logado,
        // bloqueia a auto-inativação
        if (usuario.id == idUsuarioLogado) {
            binding.btnExcluirDetalheUsuario.isEnabled = false
            binding.btnExcluirDetalheUsuario.alpha = 0.5f
            binding.btnExcluirDetalheUsuario.text = "Usuário logado não pode ser inativado"
        }
    }

    private fun inativarUsuario() {
        // Segurança extra:
        // mesmo que o botão apareça por engano, impede a ação
        if (idUsuario == idUsuarioLogado) {
            Toast.makeText(
                this,
                "Você não pode inativar o usuário que está logado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.inativarUsuario(idUsuario).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Usuário inativado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Erro ao inativar usuário: ${response.code()}",
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

    private fun reativarUsuario() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.reativarUsuario(idUsuario).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Usuário reativado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    carregarDetalhes()
                } else {
                    Toast.makeText(
                        this@DetalheUsuarioActivity,
                        "Erro ao reativar usuário: ${response.code()}",
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