package com.appstocksmart.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemVendaRelatorioBinding
import com.appstocksmart.app.model.Venda

class VendaRelatorioAdapter(
    private var lista: List<Venda>,
    private val onItemClick: (Venda) -> Unit
) : RecyclerView.Adapter<VendaRelatorioAdapter.VendaViewHolder>() {

    inner class VendaViewHolder(val binding: ItemVendaRelatorioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendaViewHolder {
        val binding = ItemVendaRelatorioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VendaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VendaViewHolder, position: Int) {
        val venda = lista[position]

        holder.binding.txtItemVendaId.text = "Venda #${venda.id ?: "-"}"
        holder.binding.txtItemVendaUsuario.text = "Operador: ${venda.usuario.nome}"
        holder.binding.txtItemVendaDataHora.text = venda.dataHora
        holder.binding.txtItemVendaTotal.text = "Total: R$ ${venda.totalFinal}"

        holder.itemView.setOnClickListener {
            onItemClick(venda)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Venda>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}