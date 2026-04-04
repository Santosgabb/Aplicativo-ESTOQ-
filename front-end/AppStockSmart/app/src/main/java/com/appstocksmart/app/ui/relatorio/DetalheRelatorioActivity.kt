package com.appstocksmart.app.ui.relatorio

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityDetalheRelatorioBinding
import com.appstocksmart.app.model.Venda
import com.appstocksmart.app.util.CupomPrinter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetalheRelatorioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheRelatorioBinding
    private lateinit var apiService: ApiService
    private var idVenda: Long = -1L

    // Guarda a venda carregada para permitir impressão
    private var vendaAtual: Venda? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheRelatorioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa a API
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Recebe o ID da venda enviado pela RelatorioActivity
        idVenda = intent.getLongExtra("idVenda", -1L)

        // Botão de imprimir cupom
        binding.btnImprimirCupomRelatorio.setOnClickListener {
            val venda = vendaAtual

            if (venda == null) {
                Toast.makeText(this, "Venda ainda não carregada", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CupomPrinter.imprimirCupom(this, venda)
        }

        // Valida se o ID recebido é válido
        if (idVenda == -1L) {
            Toast.makeText(this, "Venda inválida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carrega os detalhes da venda
        carregarDetalhesVenda()
    }

    private fun carregarDetalhesVenda() {
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

                    // Guarda a venda atual para impressão
                    vendaAtual = venda

                    // Preenche os dados da tela
                    preencherDadosVenda(venda)

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

    private fun preencherDadosVenda(venda: Venda) {
        val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        // Dados principais da venda
        binding.txtVendaId.text = "Venda #${venda.id}"
        binding.txtVendaUsuario.text = "Operador: ${venda.usuario.nome}"
        binding.txtVendaPerfil.text = "Perfil: ${venda.usuario.perfil}"
        binding.txtVendaDataHora.text = "Data/Hora: ${formatarDataHora(venda.dataHora)}"

        // Totais da venda
        binding.txtVendaSubtotal.text = "Subtotal: ${moeda.format(venda.subtotal)}"
        binding.txtVendaDesconto.text = "Desconto: ${moeda.format(venda.desconto)}"
        binding.txtVendaAcrescimo.text = "Acréscimo: ${moeda.format(venda.acrescimo)}"
        binding.txtVendaTotalFinal.text = "Total final: ${moeda.format(venda.totalFinal)}"

        // Formas de pagamento
        binding.txtVendaDinheiro.text = "Dinheiro: ${moeda.format(venda.pagamentoDinheiro)}"
        binding.txtVendaPix.text = "Pix: ${moeda.format(venda.pagamentoPix)}"
        binding.txtVendaDebito.text = "Débito: ${moeda.format(venda.pagamentoDebito)}"
        binding.txtVendaCredito.text = "Crédito: ${moeda.format(venda.pagamentoCredito)}"

        // Informação sobre desconto autorizado
        binding.txtVendaDescontoAutorizado.text =
            "Desconto autorizado por gerente: ${if (venda.descontoAutorizadoPorGerente) "Sim" else "Não"}"

        // Monta o texto dos itens vendidos
        val textoItens = StringBuilder()

        if (venda.itens.isEmpty()) {
            textoItens.append("Nenhum item encontrado.")
        } else {
            for (item in venda.itens) {
                val subtotalItem = item.subtotal ?: item.calcularSubtotal()
                val precoUnitario = item.precoUnitario ?: item.produto.precoVenda

                textoItens.append("Produto: ${item.produto.nome}\n")
                textoItens.append("Quantidade: ${item.quantidade}\n")
                textoItens.append("Preço unitário: ${moeda.format(precoUnitario)}\n")
                textoItens.append("Subtotal item: ${moeda.format(subtotalItem)}\n\n")
            }
        }

        binding.txtItensVenda.text = textoItens.toString()
    }

    /**
     * Formata a data/hora para exibição.
     * Se não conseguir converter, mostra o texto original.
     */
    private fun formatarDataHora(texto: String?): String {
        if (texto.isNullOrBlank()) {
            return "Não informada"
        }

        val candidatos = linkedSetOf<String>()
        candidatos.add(texto.trim())

        var ajustado = texto.trim()
        ajustado = ajustado.replace("T", " ")
        ajustado = ajustado.replace(Regex("\\.\\d+"), "")
        ajustado = ajustado.replace(Regex("Z$"), "")
        ajustado = ajustado.replace(Regex("[+-]\\d{2}:\\d{2}$"), "")
        candidatos.add(ajustado)

        val formatosEntrada = listOf(
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm"
        )

        val formatoSaida = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (candidato in candidatos) {
            for (formato in formatosEntrada) {
                try {
                    val sdf = SimpleDateFormat(formato, Locale.getDefault())
                    sdf.isLenient = false
                    val data = sdf.parse(candidato)
                    if (data != null) {
                        return formatoSaida.format(data)
                    }
                } catch (_: Exception) {
                }
            }
        }

        return texto
    }
}