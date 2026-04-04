package com.appstocksmart.app.ui.produto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityEditarProdutoBinding
import com.appstocksmart.app.model.Fornecedor
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.TipoProduto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarProdutoBinding

    private var idProduto: Long = -1L
    private var listaFornecedores: List<Fornecedor> = emptyList()
    private var produtoCarregado: Produto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSpinnerTipo()

        idProduto = intent.getLongExtra("idProduto", -1L)

        if (idProduto == -1L) {
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarFornecedores()

        binding.btnSalvarEdicaoProduto.setOnClickListener {
            salvarEdicao()
        }

        binding.btnInativarProduto.setOnClickListener {
            confirmarInativacao()
        }
    }

    private fun configurarSpinnerTipo() {
        val tipos = TipoProduto.values().map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerEditarTipoProduto.adapter = adapter
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

                    val nomesFornecedores = listaFornecedores.map { it.nome }

                    val adapter = ArrayAdapter(
                        this@EditarProdutoActivity,
                        android.R.layout.simple_spinner_item,
                        nomesFornecedores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerEditarFornecedorProduto.adapter = adapter

                    carregarDadosProduto()
                } else {
                    Toast.makeText(
                        this@EditarProdutoActivity,
                        "Erro ao carregar fornecedores: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Fornecedor>>, t: Throwable) {
                Toast.makeText(
                    this@EditarProdutoActivity,
                    "Falha na conexão ao carregar fornecedores: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun carregarDadosProduto() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.buscarProdutoPorId(idProduto).enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()

                    if (produto == null) {
                        Toast.makeText(
                            this@EditarProdutoActivity,
                            "Produto não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    produtoCarregado = produto

                    binding.edtEditarNomeProduto.setText(produto.nome)
                    binding.edtEditarPrecoCustoProduto.setText(produto.precoCusto.toString())
                    binding.edtEditarPrecoVendaProduto.setText(produto.precoVenda.toString())
                    binding.edtEditarQuantidadeProduto.setText(produto.quantidade.toString())
                    binding.spinnerEditarTipoProduto.setSelection(produto.tipo.ordinal)

                    val posicaoFornecedor = listaFornecedores.indexOfFirst {
                        it.id == produto.fornecedor.id
                    }

                    if (posicaoFornecedor != -1) {
                        binding.spinnerEditarFornecedorProduto.setSelection(posicaoFornecedor)
                    }
                } else {
                    if (response.code() == 404) {
                        Toast.makeText(
                            this@EditarProdutoActivity,
                            "Produto não encontrado ou foi inativado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditarProdutoActivity,
                            "Erro ao buscar produto: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Toast.makeText(
                    this@EditarProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun salvarEdicao() {
        val nome = binding.edtEditarNomeProduto.text.toString().trim()
        val precoCustoTexto = binding.edtEditarPrecoCustoProduto.text.toString().trim()
        val precoVendaTexto = binding.edtEditarPrecoVendaProduto.text.toString().trim()
        val quantidadeTexto = binding.edtEditarQuantidadeProduto.text.toString().trim()
        val tipoSelecionado = binding.spinnerEditarTipoProduto.selectedItem.toString()

        if (nome.isEmpty() || precoCustoTexto.isEmpty() || precoVendaTexto.isEmpty() || quantidadeTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaFornecedores.isEmpty()) {
            Toast.makeText(this, "Nenhum fornecedor disponível", Toast.LENGTH_SHORT).show()
            return
        }

        val precoCusto = precoCustoTexto.toDoubleOrNull()
        val precoVenda = precoVendaTexto.toDoubleOrNull()
        val quantidade = quantidadeTexto.toIntOrNull()

        if (precoCusto == null || precoVenda == null || quantidade == null) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        val fornecedorSelecionado =
            listaFornecedores[binding.spinnerEditarFornecedorProduto.selectedItemPosition]

        val produtoAtualizado = Produto(
            id = idProduto,
            nome = nome,
            tipo = TipoProduto.valueOf(tipoSelecionado),
            precoCusto = precoCusto,
            precoVenda = precoVenda,
            quantidade = quantidade,
            fornecedor = fornecedorSelecionado,
            ativo = true
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.atualizarProduto(idProduto, produtoAtualizado).enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@EditarProdutoActivity,
                        "Produto atualizado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@EditarProdutoActivity,
                        "Erro ao atualizar produto: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Toast.makeText(
                    this@EditarProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun confirmarInativacao() {
        AlertDialog.Builder(this)
            .setTitle("Inativar produto")
            .setMessage("Deseja realmente inativar este produto?")
            .setPositiveButton("Sim") { _, _ ->
                inativarProduto()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun inativarProduto() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.inativarProduto(idProduto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@EditarProdutoActivity,
                        "Produto inativado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@EditarProdutoActivity,
                        "Erro ao inativar produto: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@EditarProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}