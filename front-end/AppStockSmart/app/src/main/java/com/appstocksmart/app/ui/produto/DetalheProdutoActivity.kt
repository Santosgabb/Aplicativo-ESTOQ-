package com.appstocksmart.app.ui.produto

// Usado para abrir outra Activity
import android.content.Intent

// Ciclo de vida da tela
import android.os.Bundle

// Mensagens rápidas na tela
import android.widget.Toast

// Caixa de confirmação
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

// Cliente Retrofit e interface da API
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService

// Binding do XML da tela de detalhes
import com.appstocksmart.app.databinding.ActivityDetalheProdutoBinding

// Model do produto
import com.appstocksmart.app.model.Produto

// Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalheProdutoActivity : AppCompatActivity() {

    // Faz a ligação entre o XML e o Kotlin
    private lateinit var binding: ActivityDetalheProdutoBinding

    // Guarda o id do produto recebido pela tela anterior
    private var idProduto: Long = -1L

    // Guarda o produto atual carregado da API
    // Isso ajuda a saber se ele está ativo ou inativo
    private var produtoAtual: Produto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding
        binding = ActivityDetalheProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe o id enviado pela tela de lista de produtos
        idProduto = intent.getLongExtra("idProduto", -1L)

        // Se o id não vier corretamente, fecha a tela
        if (idProduto == -1L) {
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carrega os dados do produto ao abrir a tela
        carregarDetalhes()

        // Botão para abrir a tela de edição do produto
        binding.btnEditarDetalheProduto.setOnClickListener {
            val intent = Intent(this, EditarProdutoActivity::class.java)
            intent.putExtra("idProduto", idProduto)
            startActivity(intent)
        }

        // Botão que muda de função conforme o status do produto
        // Se estiver ativo, inativa
        // Se estiver inativo, ativa novamente
        binding.btnExcluirDetalheProduto.setOnClickListener {
            val produto = produtoAtual

            // Se o produto ainda não carregou, evita erro
            if (produto == null) {
                Toast.makeText(
                    this,
                    "Produto ainda não carregado",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Decide qual ação executar de acordo com o status
            if (produto.ativo) {
                confirmarInativacao()
            } else {
                confirmarAtivacao()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Sempre que voltar para essa tela, recarrega os dados
        // Isso ajuda quando o produto foi editado em outra tela
        carregarDetalhes()
    }

    private fun carregarDetalhes() {
        // Cria a instância da API
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Busca o produto pelo id
        apiService.buscarProdutoPorId(idProduto).enqueue(object : Callback<Produto> {

            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()

                    // Se vier resposta de sucesso, mas sem corpo
                    if (produto == null) {
                        Toast.makeText(
                            this@DetalheProdutoActivity,
                            "Produto não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    // Guarda o produto carregado
                    produtoAtual = produto

                    // Exibe os dados na tela
                    binding.txtDetalheNome.text = "Nome: ${produto.nome}"
                    binding.txtDetalheTipo.text = "Tipo: ${produto.tipo}"
                    binding.txtDetalhePrecoCusto.text = "Preço de custo: R$ ${produto.precoCusto}"
                    binding.txtDetalhePrecoVenda.text = "Preço de venda: R$ ${produto.precoVenda}"
                    binding.txtDetalheQuantidade.text = "Quantidade: ${produto.quantidade}"

                    // Troca o texto do botão conforme o status do produto
                    if (produto.ativo) {
                        binding.btnExcluirDetalheProduto.text = "Inativar Produto"
                    } else {
                        binding.btnExcluirDetalheProduto.text = "Ativar Produto"
                    }

                } else {
                    // Se a API retornar 404
                    if (response.code() == 404) {
                        Toast.makeText(
                            this@DetalheProdutoActivity,
                            "Produto não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        // Outros erros HTTP
                        Toast.makeText(
                            this@DetalheProdutoActivity,
                            "Erro ao buscar produto: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                // Erro de conexão ou falha de rede
                Toast.makeText(
                    this@DetalheProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun confirmarInativacao() {
        // Mostra uma caixa de confirmação antes de inativar
        AlertDialog.Builder(this)
            .setTitle("Inativar produto")
            .setMessage("Deseja realmente inativar este produto?")
            .setPositiveButton("Sim") { _, _ ->
                inativarProduto()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun confirmarAtivacao() {
        // Mostra uma caixa de confirmação antes de ativar novamente
        AlertDialog.Builder(this)
            .setTitle("Ativar produto")
            .setMessage("Deseja realmente ativar este produto?")
            .setPositiveButton("Sim") { _, _ ->
                ativarProduto()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun inativarProduto() {
        // Cria a instância da API
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Chama o endpoint que inativa o produto
        apiService.inativarProduto(idProduto).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Produto inativado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Recarrega a tela para atualizar o botão e os dados
                    carregarDetalhes()
                } else {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Erro ao inativar produto: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@DetalheProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun ativarProduto() {
        // Cria a instância da API
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Chama o endpoint que ativa novamente o produto
        apiService.ativarProduto(idProduto).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Produto ativado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Recarrega a tela para atualizar o botão e os dados
                    carregarDetalhes()
                } else {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Erro ao ativar produto: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@DetalheProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}