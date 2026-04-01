package com.appstocksmart.app.ui.relatorio

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityDetalheRelatorioBinding
import com.appstocksmart.app.model.Venda
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalheRelatorioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheRelatorioBinding
    private var idVenda: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheRelatorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idVenda = intent.getLongExtra("idVenda", -1L)

        if (idVenda == -1L) {
            Toast.makeText(this, "Venda inválida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDetalhesVenda()
    }

    private fun carregarDetalhesVenda() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.buscarVendasPorId(idVenda).enqueue(object : Callback<Venda> {

            override fun onResponse(call: Call<Venda>, response: Response<Venda>) {
                if (response.isSuccessful) {

                    val venda = response.body()

                    if (venda == null) {
                        Toast.makeText(
                            this@DetalheRelatorioActivity,
                            "Venda não encontrada",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    binding.txtVendaId.text = "Venda #${venda.id}"
                    binding.txtVendaUsuario.text = "Operador: ${venda.usuario.nome}"
                    binding.txtVendaPerfil.text = "Perfil: ${venda.usuario.perfil}"

                    binding.txtVendaSubtotal.text = "Subtotal: R$ ${venda.subtotal}"
                    binding.txtVendaDesconto.text = "Desconto: R$ ${venda.desconto}"
                    binding.txtVendaAcrescimo.text = "Acréscimo: R$ ${venda.acrescimo}"
                    binding.txtVendaTotalFinal.text = "Total final: R$ ${venda.totalFinal}"

                    binding.txtVendaDinheiro.text = "Dinheiro: R$ ${venda.pagamentoDinheiro}"
                    binding.txtVendaPix.text = "Pix: R$ ${venda.pagamentoPix}"
                    binding.txtVendaDebito.text = "Débito: R$ ${venda.pagamentoDebito}"
                    binding.txtVendaCredito.text = "Crédito: R$ ${venda.pagamentoCredito}"

                    binding.txtVendaDescontoAutorizado.text =
                        "Desconto autorizado por gerente: ${if (venda.descontoAutorizadoPorGerente) "Sim" else "Não"}"

                    val textoItens = StringBuilder()

                    for (item in venda.itens) {
                        textoItens.append("Produto: ${item.produto.nome}\n")
                        textoItens.append("Quantidade: ${item.quantidade}\n")
                        textoItens.append("Subtotal item: R$ ${item.calcularSubtotal()}\n\n")
                    }

                    binding.txtItensVenda.text = textoItens.toString()

                } else {
                    Toast.makeText(
                        this@DetalheRelatorioActivity,
                        "Erro ao buscar venda: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Venda>, t: Throwable) {
                Toast.makeText(
                    this@DetalheRelatorioActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        })
    }
}