package com.appstocksmart.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemProdutoVendaBinding
import com.appstocksmart.app.model.Produto
import java.text.NumberFormat
import java.util.Locale

class ProdutoVendaAdapter(
    private var lista: List<Produto>,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoVendaAdapter.ProdutoVendaViewHolder>() {

    inner class ProdutoVendaViewHolder(val binding: ItemProdutoVendaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoVendaViewHolder {
        val binding = ItemProdutoVendaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProdutoVendaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoVendaViewHolder, position: Int) {
        val produto = lista[position]
        val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        holder.binding.txtNomeProdutoVenda.text = produto.nome
        holder.binding.txtPrecoProdutoVenda.text = moeda.format(produto.precoVenda)
        holder.binding.txtQuantidadeProdutoVenda.text = "Estoque: ${produto.quantidade}"

        holder.itemView.setOnClickListener {
            onItemClick(produto)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Produto>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}