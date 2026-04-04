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

    // Binding da tela
    private lateinit var binding: ActivityCadastroProdutoBinding

    // Controle para evitar clique duplo no botão salvar
    private var salvando = false

    // Lista de fornecedores carregada da API
    private var listaFornecedores: List<Fornecedor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout com ViewBinding
        binding = ActivityCadastroProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura os spinners e carrega os fornecedores
        configurarSpinnerTipo()
        carregarFornecedores()

        // Clique do botão salvar
        binding.btnSalvarProduto.setOnClickListener {
            if (salvando) return@setOnClickListener

            salvando = true
            binding.btnSalvarProduto.isEnabled = false
            salvarProduto()
        }
    }

    private fun configurarSpinnerTipo() {
        // Pega os valores do enum TipoProduto
        val tipos = TipoProduto.values().map { it.name }

        // Adapter do spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tipos
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Liga o adapter ao spinner
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

                    val nomesFornecedores = listaFornecedores.map { "${it.id} - ${it.nome}" }

                    val adapter = ArrayAdapter(
                        this@CadastroProdutoActivity,
                        android.R.layout.simple_spinner_item,
                        nomesFornecedores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerFornecedorProduto.adapter = adapter
                } else {
                    Toast.makeText(
                        this@CadastroProdutoActivity,
                        "Erro ao carregar fornecedores",
                        Toast.LENGTH_SHORT
                    ).show()

                    salvando = false
                    binding.btnSalvarProduto.isEnabled = true
                }
            }

            override fun onFailure(call: Call<List<Fornecedor>>, t: Throwable) {
                Toast.makeText(
                    this@CadastroProdutoActivity,
                    "Falha ao carregar fornecedores: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()

                salvando = false
                binding.btnSalvarProduto.isEnabled = true
            }
        })
    }

    private fun salvarProduto() {
        // Lê os valores dos campos
        val nome = binding.edtNomeProduto.text.toString().trim()
        val precoCustoTexto = binding.edtPrecoCustoProduto.text.toString().trim()
        val precoVendaTexto = binding.edtPrecoVendaProduto.text.toString().trim()
        val quantidadeTexto = binding.edtQuantidadeProduto.text.toString().trim()
        val tipoSelecionado = binding.spinnerTipoProduto.selectedItem.toString()

        // Validação de campos vazios
        if (
            nome.isEmpty() ||
            precoCustoTexto.isEmpty() ||
            precoVendaTexto.isEmpty() ||
            quantidadeTexto.isEmpty()
        ) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            reativarBotaoSalvar()
            return
        }

        // Verifica se existem fornecedores carregados
        if (listaFornecedores.isEmpty()) {
            Toast.makeText(this, "Nenhum fornecedor disponível", Toast.LENGTH_SHORT).show()
            reativarBotaoSalvar()
            return
        }

        // Converte os valores numéricos
        val precoCusto = precoCustoTexto.toDoubleOrNull()
        val precoVenda = precoVendaTexto.toDoubleOrNull()
        val quantidade = quantidadeTexto.toIntOrNull()

        if (precoCusto == null || precoVenda == null || quantidade == null) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show()
            reativarBotaoSalvar()
            return
        }

        // Pega o fornecedor selecionado
        val posicaoFornecedor = binding.spinnerFornecedorProduto.selectedItemPosition

        if (posicaoFornecedor < 0 || posicaoFornecedor >= listaFornecedores.size) {
            Toast.makeText(this, "Fornecedor inválido", Toast.LENGTH_SHORT).show()
            reativarBotaoSalvar()
            return
        }

        val fornecedorSelecionado = listaFornecedores[posicaoFornecedor]

        if (fornecedorSelecionado.id == null || fornecedorSelecionado.id == 0L) {
            Toast.makeText(this, "Fornecedor sem ID válido", Toast.LENGTH_SHORT).show()
            reativarBotaoSalvar()
            return
        }

        Log.d(
            "CADASTRO_PRODUTO",
            "Fornecedor selecionado id=${fornecedorSelecionado.id}, nome=${fornecedorSelecionado.nome}"
        )

        // Monta o objeto produto para enviar para a API
        val produtoRequest = Produto(
            id = null,
            nome = nome,
            tipo = TipoProduto.valueOf(tipoSelecionado),
            precoCusto = precoCusto,
            precoVenda = precoVenda,
            quantidade = quantidade,
            fornecedor = fornecedorSelecionado,
            ativo = true
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        Log.d("CADASTRO_PRODUTO", "salvarProduto chamado para: $nome")

        apiService.salvarProduto(produtoRequest).enqueue(object : Callback<Produto> {

            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                reativarBotaoSalvar()

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
                reativarBotaoSalvar()

                Log.e("CADASTRO_PRODUTO", "Falha", t)

                Toast.makeText(
                    this@CadastroProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun reativarBotaoSalvar() {
        salvando = false
        binding.btnSalvarProduto.isEnabled = true
    }
}