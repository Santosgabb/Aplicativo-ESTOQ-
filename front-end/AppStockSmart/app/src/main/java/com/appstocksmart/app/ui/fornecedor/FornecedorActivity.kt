package com.appstocksmart.app.ui.fornecedor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.FornecedorAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityFornecedorBinding
import com.appstocksmart.app.model.Fornecedor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FornecedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFornecedorBinding
    private lateinit var adapter: FornecedorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFornecedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()

        binding.btnNovoFornecedor.setOnClickListener {
            startActivity(Intent(this, CadastroFornecedorActivity::class.java))
        }

        binding.btnEntradaEstoque.setOnClickListener {
            startActivity(Intent(this, EntradaEstoqueActivity::class.java))
        }

        carregarFornecedores()
    }

    override fun onResume() {
        super.onResume()
        carregarFornecedores()
    }

    private fun configurarRecyclerView() {
        adapter = FornecedorAdapter(emptyList()) { fornecedor ->
            val intent = Intent(this, DetalheFornecedorActivity::class.java)
            intent.putExtra("idFornecedor", fornecedor.id ?: -1L)
            startActivity(intent)
        }

        binding.recyclerFornecedores.layoutManager = LinearLayoutManager(this)
        binding.recyclerFornecedores.adapter = adapter
    }

    private fun carregarFornecedores() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarFornecedores().enqueue(object : Callback<List<Fornecedor>> {

            override fun onResponse(
                call: Call<List<Fornecedor>>,
                response: Response<List<Fornecedor>>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.atualizarLista(lista)
                } else {
                    Toast.makeText(
                        this@FornecedorActivity,
                        "Erro ao buscar fornecedores: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Fornecedor>>, t: Throwable) {
                Toast.makeText(
                    this@FornecedorActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}