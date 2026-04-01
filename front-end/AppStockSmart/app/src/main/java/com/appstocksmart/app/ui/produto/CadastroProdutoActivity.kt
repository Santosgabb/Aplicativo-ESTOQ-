package com.appstocksmart.app.ui.produto

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityCadastroProdutoBinding
import com.appstocksmart.app.model.Fornecedor
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.TipoProduto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroProdutoBinding
    private var salvando = false
    private var listaFornecedores: List<Fornecedor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerTipo()
        carregarFornecedores()

        binding.btnSalvarProduto.setOnClickListener {
            if (salvando) return@setOnClickListener

            salvando = true
            binding.btnSalvarProduto.isEnabled = false
            salvarProduto()
        }
    }

    private fun configurarSpinnerTipo() {
        val tipos = TipoProduto.values().map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerTipoProduto.adapter = adapter
    }

    private fun carregarFornecedores() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarFornecedores().enqueue(object : Callback<List<Fornecedor>> {
            override fun onResponse(
                call: Call<List<Fornecedor>>,
                response: Response<List<Fornecedor>>
            ) {
                if (response.isSuccessful) {
                    listaFornecedores = response.body() ?: emptyList()

                    val nomes = listaFornecedores.map { "${it.id} - ${it.nome}" }

                    val adapter = ArrayAdapter(
                        this@CadastroProdutoActivity,
                        android.R.layout.simple_spinner_item,
                        nomes
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerFornecedorProduto.adapter = adapter
                } else {
                    Toast.makeText(
                        this@CadastroProdutoActivity,
                        "Erro ao carregar fornecedores",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Fornecedor>>, t: Throwable) {
                Toast.makeText(
                    this@CadastroProdutoActivity,
                    "Falha ao carregar fornecedores: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun salvarProduto() {
        val nome = binding.edtNomeProduto.text.toString().trim()
        val precoCustoTexto = binding.edtPrecoCustoProduto.text.toString().trim()
        val precoVendaTexto = binding.edtPrecoVendaProduto.text.toString().trim()
        val quantidadeTexto = binding.edtQuantidadeProduto.text.toString().trim()
        val tipoSelecionado = binding.spinnerTipoProduto.selectedItem.toString()

        if (nome.isEmpty() || precoCustoTexto.isEmpty() || precoVendaTexto.isEmpty() || quantidadeTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            salvando = false
            binding.btnSalvarProduto.isEnabled = true
            return
        }

        if (listaFornecedores.isEmpty()) {
            Toast.makeText(this, "Nenhum fornecedor disponível", Toast.LENGTH_SHORT).show()
            salvando = false
            binding.btnSalvarProduto.isEnabled = true
            return
        }

        val precoCusto = precoCustoTexto.toDoubleOrNull()
        val precoVenda = precoVendaTexto.toDoubleOrNull()
        val quantidade = quantidadeTexto.toIntOrNull()

        if (precoCusto == null || precoVenda == null || quantidade == null) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show()
            salvando = false
            binding.btnSalvarProduto.isEnabled = true
            return
        }

        val posicaoFornecedor = binding.spinnerFornecedorProduto.selectedItemPosition

        if (posicaoFornecedor < 0 || posicaoFornecedor >= listaFornecedores.size) {
            Toast.makeText(this, "Fornecedor inválido", Toast.LENGTH_SHORT).show()
            salvando = false
            binding.btnSalvarProduto.isEnabled = true
            return
        }

        val fornecedorSelecionado = listaFornecedores[posicaoFornecedor]

        if (fornecedorSelecionado.id == null || fornecedorSelecionado.id == 0L) {
            Toast.makeText(this, "Fornecedor sem ID válido", Toast.LENGTH_SHORT).show()
            salvando = false
            binding.btnSalvarProduto.isEnabled = true
            return
        }

        Log.d(
            "CADASTRO_PRODUTO",
            "Fornecedor selecionado id=${fornecedorSelecionado.id}, nome=${fornecedorSelecionado.nome}"
        )

        val produtoRequest = Produto(
            id = null,
            nome = nome,
            tipo = TipoProduto.valueOf(tipoSelecionado),
            precoCusto = precoCusto,
            precoVenda = precoVenda,
            quantidade = quantidade,
            fornecedor = fornecedorSelecionado
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        Log.d("CADASTRO_PRODUTO", "salvarProduto chamado para: $nome")

        apiService.salvarProduto(produtoRequest).enqueue(object : Callback<Produto> {

            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                salvando = false
                binding.btnSalvarProduto.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CadastroProdutoActivity,
                        "Produto cadastrado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CADASTRO_PRODUTO", "Erro ${response.code()} - $errorBody")

                    Toast.makeText(
                        this@CadastroProdutoActivity,
                        "Erro ao cadastrar produto: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                salvando = false
                binding.btnSalvarProduto.isEnabled = true

                Log.e("CADASTRO_PRODUTO", "Falha", t)

                Toast.makeText(
                    this@CadastroProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}