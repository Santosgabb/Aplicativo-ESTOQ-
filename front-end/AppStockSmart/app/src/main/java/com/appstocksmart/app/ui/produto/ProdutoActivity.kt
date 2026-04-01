package com.appstocksmart.app.ui.produto

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.ProdutoAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityProdutoBinding
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.TipoProduto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdutoBinding
    private lateinit var adapter: ProdutoAdapter

    // Lista que guarda os produtos vindos da API
    private var listaProdutos: List<Produto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Faz a ligação entre XML e Kotlin
        binding = ActivityProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura spinner, recycler e eventos
        configurarSpinnerFiltro()
        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()

        // Sempre que voltar para a tela, recarrega os produtos
        carregarProdutos()
    }

    private fun configurarSpinnerFiltro() {
        val listaTipos = mutableListOf("TODOS")
        listaTipos.addAll(TipoProduto.values().map { it.name })

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listaTipos
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltroTipoProduto.adapter = spinnerAdapter
    }

    private fun configurarRecyclerView() {
        adapter = ProdutoAdapter(emptyList()) { produto ->
            val intent = Intent(this, DetalheProdutoActivity::class.java)
            intent.putExtra("idProduto", produto.id)
            startActivity(intent)
        }

        binding.recyclerProdutos.layoutManager = LinearLayoutManager(this)
        binding.recyclerProdutos.adapter = adapter
    }

    private fun configurarEventos() {
        binding.btnNovoProduto.setOnClickListener {
            startActivity(Intent(this, CadastroProdutoActivity::class.java))
        }

        binding.btnFiltrarProdutos.setOnClickListener {
            filtrarProdutos()
        }
    }

    private fun carregarProdutos() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarProdutos().enqueue(object : Callback<List<Produto>> {

            override fun onResponse(
                call: Call<List<Produto>>,
                response: Response<List<Produto>>
            ) {
                if (response.isSuccessful) {
                    listaProdutos = response.body() ?: emptyList()
                    adapter.atualizarLista(listaProdutos)
                } else {
                    Toast.makeText(
                        this@ProdutoActivity,
                        "Erro ao buscar produtos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(
                    this@ProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun filtrarProdutos() {
        val nomeBusca = binding.edtBuscarProduto.text.toString().trim()
        val tipoSelecionado = binding.spinnerFiltroTipoProduto.selectedItem.toString()

        val tipo = if (tipoSelecionado == "TODOS") {
            null
        } else {
            TipoProduto.valueOf(tipoSelecionado)
        }

        val produtosFiltrados = listaProdutos.filter { produto ->
            val nomeOk = produto.nome.contains(nomeBusca, ignoreCase = true)
            val tipoOk = tipo == null || produto.tipo == tipo
            nomeOk && tipoOk
        }

        adapter.atualizarLista(produtosFiltrados)
    }
}