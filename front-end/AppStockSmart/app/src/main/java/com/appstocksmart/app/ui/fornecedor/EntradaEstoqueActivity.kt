package com.appstocksmart.app.ui.fornecedor

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityEntradaEstoqueBinding
import com.appstocksmart.app.model.Fornecedor
import com.appstocksmart.app.model.Produto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EntradaEstoqueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntradaEstoqueBinding

    private var listaProdutos: List<Produto> = emptyList()
    private var listaFornecedores: List<Fornecedor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntradaEstoqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerProdutos()
        configurarSpinnerFornecedores()

        binding.btnSalvarEntradaEstoque.setOnClickListener {
            salvarEntrada()
        }
    }

    private fun configurarSpinnerProdutos() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(
                call: Call<List<Produto>>,
                response: Response<List<Produto>>
            ) {
                if (response.isSuccessful) {
                    listaProdutos = response.body() ?: emptyList()

                    val nomesProdutos = listaProdutos.map { "${it.id} - ${it.nome}" }

                    val adapter = ArrayAdapter(
                        this@EntradaEstoqueActivity,
                        android.R.layout.simple_spinner_item,
                        nomesProdutos
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerProdutoEntrada.adapter = adapter
                } else {
                    Toast.makeText(
                        this@EntradaEstoqueActivity,
                        "Erro ao carregar produtos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(
                    this@EntradaEstoqueActivity,
                    "Falha ao carregar produtos: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun configurarSpinnerFornecedores() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarFornecedores().enqueue(object : Callback<List<Fornecedor>> {
            override fun onResponse(
                call: Call<List<Fornecedor>>,
                response: Response<List<Fornecedor>>
            ) {
                if (response.isSuccessful) {
                    listaFornecedores = response.body() ?: emptyList()

                    val nomesFornecedores = listaFornecedores.map { "${it.id} - ${it.nome}" }

                    val adapter = ArrayAdapter(
                        this@EntradaEstoqueActivity,
                        android.R.layout.simple_spinner_item,
                        nomesFornecedores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerFornecedorEntrada.adapter = adapter
                } else {
                    Toast.makeText(
                        this@EntradaEstoqueActivity,
                        "Erro ao carregar fornecedores: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Fornecedor>>, t: Throwable) {
                Toast.makeText(
                    this@EntradaEstoqueActivity,
                    "Falha ao carregar fornecedores: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun salvarEntrada() {
        if (listaProdutos.isEmpty()) {
            Toast.makeText(this, "Não há produtos cadastrados", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaFornecedores.isEmpty()) {
            Toast.makeText(this, "Não há fornecedores cadastrados", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidadeTexto = binding.edtQuantidadeEntrada.text.toString().trim()

        if (quantidadeTexto.isEmpty()) {
            Toast.makeText(this, "Digite a quantidade", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = quantidadeTexto.toIntOrNull()

        if (quantidade == null || quantidade <= 0) {
            Toast.makeText(this, "Quantidade inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val produto = listaProdutos[binding.spinnerProdutoEntrada.selectedItemPosition]
        val fornecedor = listaFornecedores[binding.spinnerFornecedorEntrada.selectedItemPosition]

        val idProduto = produto.id
        if (idProduto == null) {
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val produtoAtualizado = produto.copy(
            quantidade = produto.quantidade + quantidade
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.atualizarProduto(idProduto, produtoAtualizado)
            .enqueue(object : Callback<Produto> {

                override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EntradaEstoqueActivity,
                            "Entrada de estoque registrada com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EntradaEstoqueActivity,
                            "Erro ao atualizar estoque: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Produto>, t: Throwable) {
                    Toast.makeText(
                        this@EntradaEstoqueActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}