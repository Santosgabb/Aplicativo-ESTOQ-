package com.appstocksmart.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemVendaRelatorioBinding
import com.appstocksmart.app.model.Venda
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class VendaRelatorioAdapter(
    // Lista de vendas exibida no RecyclerView
    private var lista: List<Venda>,

    // Função chamada quando o usuário clica em uma venda
    private val onItemClick: (Venda) -> Unit
) : RecyclerView.Adapter<VendaRelatorioAdapter.VendaViewHolder>() {

    /**
     * ViewHolder responsável por segurar o binding de cada item da lista.
     */
    inner class VendaViewHolder(val binding: ItemVendaRelatorioBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Cria o layout de cada item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendaViewHolder {
        val binding = ItemVendaRelatorioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VendaViewHolder(binding)
    }

    /**
     * Preenche os dados de cada item da lista.
     */
    override fun onBindViewHolder(holder: VendaViewHolder, position: Int) {
        val venda = lista[position]

        // Formatação de moeda no padrão brasileiro
        val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        // Preenche os dados principais do card
        holder.binding.txtItemVendaId.text = "Venda #${venda.id ?: "-"}"
        holder.binding.txtItemVendaUsuario.text = "Operador: ${venda.usuario.nome}"
        holder.binding.txtItemVendaDataHora.text =
            "Data/Hora: ${formatarDataHora(venda.dataHora)}"
        holder.binding.txtItemVendaTotal.text = formatoMoeda.format(venda.totalFinal)

        // Clique no item da lista
        holder.itemView.setOnClickListener {
            onItemClick(venda)
        }
    }

    /**
     * Retorna a quantidade de itens da lista.
     */
    override fun getItemCount(): Int = lista.size

    /**
     * Atualiza a lista do RecyclerView.
     */
    fun atualizarLista(novaLista: List<Venda>) {
        lista = novaLista
        notifyDataSetChanged()
    }

    /**
     * Formata a data/hora para ficar mais amigável na tela.
     * Se não conseguir converter, mostra o valor original.
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