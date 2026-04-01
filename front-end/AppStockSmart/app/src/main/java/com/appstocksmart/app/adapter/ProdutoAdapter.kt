package com.appstocksmart.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemProdutoBinding
import com.appstocksmart.app.model.Produto

class ProdutoAdapter(
    private var lista: List<Produto>,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    inner class ProdutoViewHolder(val binding: ItemProdutoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val binding = ItemProdutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = lista[position]

        holder.binding.txtItemNomeProduto.text = produto.nome
        holder.binding.txtItemTipoProduto.text = "Tipo: ${produto.tipo}"
        holder.binding.txtItemPrecoVendaProduto.text = "Venda: R$ ${produto.precoVenda}"
        holder.binding.txtItemQuantidadeProduto.text = "Qtd: ${produto.quantidade}"

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