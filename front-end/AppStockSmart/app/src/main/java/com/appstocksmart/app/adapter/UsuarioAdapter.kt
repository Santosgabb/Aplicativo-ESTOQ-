package com.appstocksmart.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appstocksmart.app.databinding.ItemUsuarioBinding
import com.appstocksmart.app.model.Usuario

class UsuarioAdapter(
    private var lista: List<Usuario>,
    private val onItemClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = lista[position]

        holder.binding.txtItemNomeUsuario.text = usuario.nome
        holder.binding.txtItemLoginUsuario.text = "Login: ${usuario.login}"
        holder.binding.txtItemPerfilUsuario.text = "Perfil: ${usuario.perfil}"

        holder.itemView.setOnClickListener {
            onItemClick(usuario)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Usuario>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}