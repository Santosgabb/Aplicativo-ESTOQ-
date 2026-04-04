package com.appstocksmart.app.ui.fornecedor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
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

    // Lista completa vinda da API
    private var listaProdutosCompleta: List<Produto> = emptyList()

    // Lista que será mostrada no spinner após aplicar filtro
    private var listaProdutosFiltrada: List<Produto> = emptyList()

    // Lista de fornecedores
    private var listaFornecedores: List<Fornecedor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntradaEstoqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicia o resumo com valores padrão
        limparResumoProduto()

        // Configura eventos do filtro e seleção do produto
        configurarEventosFiltros()

        // Carrega os dados da tela
        carregarProdutos()
        configurarSpinnerFornecedores()

        binding.btnSalvarEntradaEstoque.setOnClickListener {
            salvarEntrada()
        }
    }

    private fun configurarEventosFiltros() {
        // Quando mudar o tipo do produto, refaz o filtro
        binding.spinnerTipoProdutoEntrada.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    aplicarFiltroProdutos()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nada a fazer
                }
            }

        // Quando digitar no campo de pesquisa, refaz o filtro
        binding.edtPesquisarProdutoEntrada.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nada a fazer
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aplicarFiltroProdutos()
            }

            override fun afterTextChanged(s: Editable?) {
                // Nada a fazer
            }
        })

        // Quando selecionar um produto no spinner, atualiza o resumo visual
        binding.spinnerProdutoEntrada.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    atualizarResumoProdutoSelecionado()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    limparResumoProduto()
                }
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
                    listaProdutosCompleta = response.body() ?: emptyList()
                    listaProdutosFiltrada = listaProdutosCompleta

                    // Carrega os tipos disponíveis com base nos produtos cadastrados
                    configurarSpinnerTipoProduto()

                    // Aplica o filtro inicial
                    aplicarFiltroProdutos()
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

    private fun configurarSpinnerTipoProduto() {
        // Monta a lista de tipos a partir dos produtos cadastrados
        val tipos = mutableListOf("Todos")

        val tiposEncontrados = listaProdutosCompleta
            .map { formatarTipoProduto(it.tipo.toString()) }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        tipos.addAll(tiposEncontrados)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tipos
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoProdutoEntrada.adapter = adapter
    }

    private fun aplicarFiltroProdutos() {
        val tipoSelecionado =
            binding.spinnerTipoProdutoEntrada.selectedItem?.toString()?.trim() ?: "Todos"

        val textoPesquisa =
            binding.edtPesquisarProdutoEntrada.text.toString().trim().lowercase()

        listaProdutosFiltrada = listaProdutosCompleta.filter { produto ->
            val nomeProduto = produto.nome.toString().trim().lowercase()
            val tipoProdutoFormatado = formatarTipoProduto(produto.tipo.toString())

            val correspondeTipo =
                tipoSelecionado.equals("Todos", ignoreCase = true) ||
                        tipoProdutoFormatado.equals(tipoSelecionado, ignoreCase = true)

            val correspondeNome =
                nomeProduto.contains(textoPesquisa)

            correspondeTipo && correspondeNome
        }

        atualizarSpinnerProdutos()
    }

    private fun atualizarSpinnerProdutos() {
        val nomesProdutos = if (listaProdutosFiltrada.isEmpty()) {
            listOf("Nenhum produto encontrado")
        } else {
            listaProdutosFiltrada.map { "${it.id} - ${it.nome}" }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nomesProdutos
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProdutoEntrada.adapter = adapter

        atualizarResumoProdutoSelecionado()
    }

    private fun atualizarResumoProdutoSelecionado() {
        if (listaProdutosFiltrada.isEmpty()) {
            limparResumoProduto()
            return
        }

        val posicaoSelecionada = binding.spinnerProdutoEntrada.selectedItemPosition

        if (posicaoSelecionada < 0 || posicaoSelecionada >= listaProdutosFiltrada.size) {
            limparResumoProduto()
            return
        }

        val produtoSelecionado = listaProdutosFiltrada[posicaoSelecionada]

        binding.txtTipoProdutoSelecionadoEntrada.text =
            "Tipo: ${formatarTipoProduto(produtoSelecionado.tipo.toString())}"

        binding.txtEstoqueAtualEntrada.text =
            "Estoque atual: ${produtoSelecionado.quantidade}"
    }

    private fun limparResumoProduto() {
        binding.txtTipoProdutoSelecionadoEntrada.text = "Tipo: -"
        binding.txtEstoqueAtualEntrada.text = "Estoque atual: -"
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
        if (listaProdutosCompleta.isEmpty()) {
            Toast.makeText(this, "Não há produtos cadastrados", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaProdutosFiltrada.isEmpty()) {
            Toast.makeText(this, "Nenhum produto encontrado para esse filtro", Toast.LENGTH_SHORT)
                .show()
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

        val posicaoProduto = binding.spinnerProdutoEntrada.selectedItemPosition
        if (posicaoProduto < 0 || posicaoProduto >= listaProdutosFiltrada.size) {
            Toast.makeText(this, "Selecione um produto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val posicaoFornecedor = binding.spinnerFornecedorEntrada.selectedItemPosition
        if (posicaoFornecedor < 0 || posicaoFornecedor >= listaFornecedores.size) {
            Toast.makeText(this, "Selecione um fornecedor válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Produto selecionado com base na LISTA FILTRADA
        val produto = listaProdutosFiltrada[posicaoProduto]

        // Mantido para respeitar a lógica atual da tela
        val fornecedor = listaFornecedores[posicaoFornecedor]

        val idProduto = produto.id
        if (idProduto == null) {
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Atualmente a tela atualiza apenas a quantidade do produto.
        // O fornecedor continua sendo selecionado na interface para manter a regra atual da tela.
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

                        // Evita warning de variável não usada e mantém a seleção do fornecedor
                        fornecedor.nome

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

    private fun formatarTipoProduto(tipo: String): String {
        if (tipo.isBlank() || tipo.equals("null", ignoreCase = true)) {
            return ""
        }

        return tipo
            .trim()
            .replace("_", " ")
    }
}