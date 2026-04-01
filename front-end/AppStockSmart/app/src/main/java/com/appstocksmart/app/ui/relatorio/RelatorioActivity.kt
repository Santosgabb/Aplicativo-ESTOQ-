package com.appstocksmart.app.ui.relatorio

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.VendaRelatorioAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityRelatorioBinding
import com.appstocksmart.app.model.Venda
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RelatorioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRelatorioBinding
    private lateinit var adapter: VendaRelatorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        carregarRelatorio()
    }

    override fun onResume() {
        super.onResume()
        carregarRelatorio()
    }

    private fun configurarRecyclerView() {
        adapter = VendaRelatorioAdapter(emptyList()) { venda ->

            val idVenda = venda.id

            if (idVenda == null) {
                Toast.makeText(this, "Venda sem ID válido", Toast.LENGTH_SHORT).show()
                return@VendaRelatorioAdapter
            }

            val intent = Intent(this, DetalheRelatorioActivity::class.java)
            intent.putExtra("idVenda", idVenda)
            startActivity(intent)
        }

        binding.recyclerRelatorioVendas.layoutManager = LinearLayoutManager(this)
        binding.recyclerRelatorioVendas.adapter = adapter
    }

    private fun carregarRelatorio() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarVendas().enqueue(object : Callback<List<Venda>> {

            override fun onResponse(
                call: Call<List<Venda>>,
                response: Response<List<Venda>>
            ) {
                if (response.isSuccessful) {

                    val vendas = response.body() ?: emptyList()

                    val quantidadeVendas = vendas.size
                    val totalBruto = vendas.sumOf { it.subtotal }
                    val totalDescontos = vendas.sumOf { it.desconto }
                    val totalAcrescimos = vendas.sumOf { it.acrescimo }
                    val totalLiquido = vendas.sumOf { it.totalFinal }

                    val totalDinheiro = vendas.sumOf { it.pagamentoDinheiro }
                    val totalPix = vendas.sumOf { it.pagamentoPix }
                    val totalDebito = vendas.sumOf { it.pagamentoDebito }
                    val totalCredito = vendas.sumOf { it.pagamentoCredito }

                    val vendasComDesconto = vendas.count { it.desconto > 0.0 }

                    binding.txtQuantidadeVendas.text = "Quantidade de vendas: $quantidadeVendas"
                    binding.txtTotalBruto.text = "Total bruto: R$ $totalBruto"
                    binding.txtTotalDescontos.text = "Total de descontos: R$ $totalDescontos"
                    binding.txtTotalAcrescimos.text = "Total de acréscimos: R$ $totalAcrescimos"
                    binding.txtTotalLiquido.text = "Total líquido: R$ $totalLiquido"

                    binding.txtTotalDinheiro.text = "Total em dinheiro: R$ $totalDinheiro"
                    binding.txtTotalPix.text = "Total em Pix: R$ $totalPix"
                    binding.txtTotalDebito.text = "Total em débito: R$ $totalDebito"
                    binding.txtTotalCredito.text = "Total em crédito: R$ $totalCredito"

                    binding.txtVendasComDesconto.text = "Vendas com desconto: $vendasComDesconto"

                    adapter.atualizarLista(vendas)

                } else {
                    Toast.makeText(
                        this@RelatorioActivity,
                        "Erro ao buscar vendas: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Venda>>, t: Throwable) {
                Toast.makeText(
                    this@RelatorioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}