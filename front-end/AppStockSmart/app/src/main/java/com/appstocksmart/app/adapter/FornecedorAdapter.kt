package com.appstocksmart.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemFornecedorBinding
import com.appstocksmart.app.model.Fornecedor

class FornecedorAdapter(
    private var lista: List<Fornecedor>,
    private val onItemClick: (Fornecedor) -> Unit
) : RecyclerView.Adapter<FornecedorAdapter.FornecedorViewHolder>() {

    inner class FornecedorViewHolder(val binding: ItemFornecedorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FornecedorViewHolder {
        val binding = ItemFornecedorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FornecedorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FornecedorViewHolder, position: Int) {
        val fornecedor = lista[position]

        holder.binding.txtItemNomeFornecedor.text = fornecedor.nome
        holder.binding.txtItemTelefoneFornecedor.text = "Telefone: ${fornecedor.telefone}"
        holder.binding.txtItemEmailFornecedor.text = "Email: ${fornecedor.email}"

        holder.itemView.setOnClickListener {
            onItemClick(fornecedor)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Fornecedor>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}