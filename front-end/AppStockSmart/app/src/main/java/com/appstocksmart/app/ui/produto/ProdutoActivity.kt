package com.appstocksmart.app.ui.produto

// Usado para abrir outra tela
import android.content.Intent

// Ciclo de vida da Activity
import android.os.Bundle

// Adapter simples para Spinner
import android.widget.ArrayAdapter

// Mensagens rápidas na tela
import android.widget.Toast

// Classe base de Activity
import androidx.appcompat.app.AppCompatActivity

// Layout da lista
import androidx.recyclerview.widget.LinearLayoutManager

// Adapter do RecyclerView
import com.appstocksmart.app.adapter.ProdutoAdapter

// Cliente Retrofit e interface da API
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService

// Binding do XML da tela de produtos
import com.appstocksmart.app.databinding.ActivityProdutoBinding

// Model do produto e enum dos tipos
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.TipoProduto

// Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdutoActivity : AppCompatActivity() {

    // Faz a ligação do Kotlin com o XML
    private lateinit var binding: ActivityProdutoBinding

    // Adapter que controla a lista do RecyclerView
    private lateinit var adapter: ProdutoAdapter

    // Lista completa de produtos carregados da API
    // Depois essa lista é filtrada por nome e tipo
    private var listaProdutos: List<Produto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding da tela
        binding = ActivityProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura os componentes visuais da tela
        configurarSpinnerFiltro()
        configurarSpinnerStatus()
        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()

        // Sempre que volta para essa tela,
        // recarrega os produtos para atualizar a lista
        carregarProdutos()
    }

    private fun configurarSpinnerFiltro() {
        // Cria uma lista de tipos para o spinner
        // O primeiro item será "TODOS"
        val listaTipos = mutableListOf("TODOS")

        // Adiciona todos os valores do enum TipoProduto
        listaTipos.addAll(TipoProduto.values().map { it.name })

        // Adapter do spinner
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listaTipos
        )

        // Define o layout da lista suspensa
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Liga o adapter ao spinner de tipo
        binding.spinnerFiltroTipoProduto.adapter = spinnerAdapter
    }

    private fun configurarSpinnerStatus() {
        // Lista fixa de status para filtrar os produtos
        val listaStatus = listOf("ATIVOS", "INATIVOS", "TODOS")

        // Adapter do spinner de status
        val statusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listaStatus
        )

        // Define o layout da lista suspensa
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Liga o adapter ao spinner de status
        binding.spinnerFiltroStatusProduto.adapter = statusAdapter
    }

    private fun configurarRecyclerView() {
        // Cria o adapter da lista de produtos
        // Quando clicar em um item, abre a tela de detalhes
        adapter = ProdutoAdapter(emptyList()) { produto ->
            val intent = Intent(this, DetalheProdutoActivity::class.java)
            intent.putExtra("idProduto", produto.id)
            startActivity(intent)
        }

        // Define o layout da lista
        binding.recyclerProdutos.layoutManager = LinearLayoutManager(this)

        // Define o adapter da lista
        binding.recyclerProdutos.adapter = adapter
    }

    private fun configurarEventos() {
        // Botão para abrir a tela de cadastro de produto
        binding.btnNovoProduto.setOnClickListener {
            startActivity(Intent(this, CadastroProdutoActivity::class.java))
        }

        // Botão para aplicar os filtros
        binding.btnFiltrarProdutos.setOnClickListener {
            carregarProdutos()
        }
    }

    private fun carregarProdutos() {
        // Cria a instância da API
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Pega o status selecionado no spinner
        val statusSelecionado = binding.spinnerFiltroStatusProduto.selectedItem.toString()

        // Escolhe qual endpoint usar com base no status
        val call = when (statusSelecionado) {
            "INATIVOS" -> apiService.listarProdutosInativos()
            "TODOS" -> apiService.listarTodosProdutos()
            else -> apiService.listarProdutos() // padrão: ativos
        }

        // Faz a chamada para a API
        call.enqueue(object : Callback<List<Produto>> {
            override fun onResponse(
                call: Call<List<Produto>>,
                response: Response<List<Produto>>
            ) {
                if (response.isSuccessful) {
                    // Salva a lista vinda do back-end
                    listaProdutos = response.body() ?: emptyList()

                    // Depois aplica os filtros locais por nome e tipo
                    filtrarProdutos()
                } else {
                    Toast.makeText(
                        this@ProdutoActivity,
                        "Erro ao buscar produtos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                // Erro de rede ou conexão
                Toast.makeText(
                    this@ProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun filtrarProdutos() {
        // Texto digitado no campo de busca
        val nomeBusca = binding.edtBuscarProduto.text.toString().trim()

        // Tipo selecionado no spinner
        val tipoSelecionado = binding.spinnerFiltroTipoProduto.selectedItem.toString()

        // Se o tipo for "TODOS", não filtra por tipo
        // Caso contrário, converte o texto para o enum
        val tipo = if (tipoSelecionado == "TODOS") {
            null
        } else {
            TipoProduto.valueOf(tipoSelecionado)
        }

        // Filtra a lista localmente por nome e tipo
        val produtosFiltrados = listaProdutos.filter { produto ->
            val nomeOk = produto.nome.contains(nomeBusca, ignoreCase = true)
            val tipoOk = tipo == null || produto.tipo == tipo
            nomeOk && tipoOk
        }

        // Atualiza a lista visual do RecyclerView
        adapter.atualizarLista(produtosFiltrados)
    }
}