package com.appstocksmart.app.ui.usuario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.UsuarioAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityUsuarioBinding
import com.appstocksmart.app.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuarioBinding
    private lateinit var adapter: UsuarioAdapter
    private var mostrandoInativos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarAcoes()
        carregarUsuariosAtivos()
    }

    override fun onResume() {
        super.onResume()

        if (mostrandoInativos) {
            carregarUsuariosInativos()
        } else {
            carregarUsuariosAtivos()
        }
    }

    private fun configurarRecyclerView() {
        adapter = UsuarioAdapter(emptyList()) { usuario ->
            val intent = Intent(this, DetalheUsuarioActivity::class.java)
            intent.putExtra("idUsuario", usuario.id)
            startActivity(intent)
        }

        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(this)
        binding.recyclerUsuarios.adapter = adapter
    }

    private fun configurarAcoes() {
        binding.btnNovoUsuario.setOnClickListener {
            startActivity(Intent(this, CadastroUsuarioActivity::class.java))
        }

        binding.btnMostrarUsuariosAtivos.setOnClickListener {
            carregarUsuariosAtivos()
        }

        binding.btnMostrarUsuariosInativos.setOnClickListener {
            carregarUsuariosInativos()
        }
    }

    private fun carregarUsuariosAtivos() {
        mostrandoInativos = false

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarUsuarios().enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.atualizarLista(lista)
                    binding.txtSubtituloUsuarios.text = "Exibindo usuários ativos"
                } else {
                    Toast.makeText(
                        this@UsuarioActivity,
                        "Erro ao buscar usuários ativos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(
                    this@UsuarioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun carregarUsuariosInativos() {
        mostrandoInativos = true

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarUsuariosInativos().enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(
                call: Call<List<Usuario>>,
                response: Response<List<Usuario>>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.atualizarLista(lista)
                    binding.txtSubtituloUsuarios.text = "Exibindo usuários inativos"
                } else {
                    Toast.makeText(
                        this@UsuarioActivity,
                        "Erro ao buscar usuários inativos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(
                    this@UsuarioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}