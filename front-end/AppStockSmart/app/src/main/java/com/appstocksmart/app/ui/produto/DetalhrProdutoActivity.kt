package com.appstocksmart.app.ui.produto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityDetalheProdutoBinding
import com.appstocksmart.app.model.Produto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalheProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheProdutoBinding

    // Agora o id vem do back-end como Long
    private var idProduto: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe o id enviado pela ProdutoActivity
        idProduto = intent.getLongExtra("idProduto", -1L)

        // Se o id for inválido, fecha a tela
        if (idProduto == -1L) {
            Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carrega os detalhes do produto pela API
        carregarDetalhes()

        // Abre a tela de edição
        binding.btnEditarDetalheProduto.setOnClickListener {
            val intent = Intent(this, EditarProdutoActivity::class.java)
            intent.putExtra("idProduto", idProduto)
            startActivity(intent)
        }

        // Exclui o produto pela API
        binding.btnExcluirDetalheProduto.setOnClickListener {
            excluirProduto()
        }
    }

    override fun onResume() {
        super.onResume()

        // Sempre que voltar, atualiza os dados
        carregarDetalhes()
    }

    private fun carregarDetalhes() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Faz GET /produtos/{id}
        apiService.buscarProdutoPorId(idProduto).enqueue(object : Callback<Produto> {

            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()

                    if (produto == null) {
                        Toast.makeText(
                            this@DetalheProdutoActivity,
                            "Produto não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    // Mostra os dados na tela
                    binding.txtDetalheNome.text = "Nome: ${produto.nome}"
                    binding.txtDetalheTipo.text = "Tipo: ${produto.tipo}"
                    binding.txtDetalhePrecoCusto.text = "Preço de custo: R$ ${produto.precoCusto}"
                    binding.txtDetalhePrecoVenda.text = "Preço de venda: R$ ${produto.precoVenda}"
                    binding.txtDetalheQuantidade.text = "Quantidade: ${produto.quantidade}"

                } else {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Erro ao buscar produto: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Toast.makeText(
                    this@DetalheProdutoActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun excluirProduto() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Faz DELETE /produtos/{id}
        apiService.deletarProduto(idProduto).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Produto excluído com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@DetalheProdutoActivity,
                        "Erro ao excluir produto: ${response.code()}",
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