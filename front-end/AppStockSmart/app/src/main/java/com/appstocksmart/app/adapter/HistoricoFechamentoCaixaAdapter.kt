package com.appstocksmart.app.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemHistoricoFechamentoCaixaBinding
import com.appstocksmart.app.model.HistoricoFechamentoCaixa
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoricoFechamentoCaixaAdapter(
    private val lista: List<HistoricoFechamentoCaixa>
) : RecyclerView.Adapter<HistoricoFechamentoCaixaAdapter.HistoricoViewHolder>() {

    inner class HistoricoViewHolder(val binding: ItemHistoricoFechamentoCaixaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val binding = ItemHistoricoFechamentoCaixaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoricoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val item = lista[position]

        /*
         * Soma do total vendido em cartão:
         * crédito + débito
         */
        val totalCartao = item.totalCredito + item.totalDebito

        holder.binding.tvOperadorNome.text = "Operador: ${item.operadorNome ?: "-"}"
        holder.binding.tvGerenteNome.text = "Gerente: ${item.gerenteNome ?: "-"}"
        holder.binding.tvDataReferencia.text = "Data: ${formatarData(item.dataReferencia)}"
        holder.binding.tvValorInicial.text = "Valor inicial: ${formatarMoeda(item.valorInicialCaixa)}"
        holder.binding.tvQuantidadeVendas.text = "Qtd. vendas: ${item.quantidadeVendas}"

        holder.binding.tvTotalDinheiro.text =
            "Total em dinheiro: ${formatarMoeda(item.totalDinheiro)}"

        holder.binding.tvTotalPix.text =
            "Total em PIX: ${formatarMoeda(item.totalPix)}"

        holder.binding.tvTotalDebito.text =
            "Total em débito: ${formatarMoeda(item.totalDebito)}"

        holder.binding.tvTotalCredito.text =
            "Total em crédito: ${formatarMoeda(item.totalCredito)}"

        holder.binding.tvTotalCartao.text =
            "Total em cartão: ${formatarMoeda(totalCartao)}"

        holder.binding.tvTotalSaidas.text =
            "Total de saídas: ${formatarMoeda(item.totalSaidas)}"

        holder.binding.tvDinheiroFinal.text =
            "Dinheiro final: ${formatarMoeda(item.dinheiroFinalEmCaixa)}"

        holder.binding.tvObservacao.text = if (item.observacao.isNullOrBlank()) {
            "Observação: -"
        } else {
            "Observação: ${item.observacao}"
        }
    }

    override fun getItemCount(): Int = lista.size

    private fun formatarMoeda(valor: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
    }

    private fun formatarData(data: String?): String {
        if (data.isNullOrBlank()) return "-"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE)
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } else {
                data
            }
        } catch (e: Exception) {
            data
        }
    }
}