/*package com.appstocksmart.app.repository

import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.TipoProduto

object ProdutoRepository {

    private val produtos = mutableListOf(
        Produto(1, "Arroz 5kg", TipoProduto.ALIMENTO, 22.50, 29.90, 10),
        Produto(2, "Feijao 1kg", TipoProduto.ALIMENTO, 6.00, 8.50, 20),
        Produto(3, "Refrigerante 2L", TipoProduto.BEBIDA, 7.20, 9.99, 8),
        Produto(4, "Sabonete", TipoProduto.HIGIENE, 2.30, 3.99, 15)
    )

    fun listar(): List<Produto> {
        return produtos
    }

    fun adicionar(produto: Produto) {
        produtos.add(produto)
    }

    fun atualizar(produtoAtualizado: Produto) {
        val index = produtos.indexOfFirst { it.id == produtoAtualizado.id }
        if (index != -1) {
            produtos[index] = produtoAtualizado
        }
    }

    fun remover(id: Long) {
        produtos.removeIf { it.id == id }
    }

    fun buscarPorId(id: Long): Produto? {
        return produtos.find { it.id == id }
    }

    fun proximoId(): Long {
        return if (produtos.isEmpty()) 1 else produtos.maxOf { it.id ?: 0L } + 1
    }

    fun buscarPorNome(nome: String): List<Produto> {
        return produtos.filter { it.nome.contains(nome, ignoreCase = true) }
    }

    fun filtrarPorTipo(tipo: TipoProduto): List<Produto> {
        return produtos.filter { it.tipo == tipo }
    }

    fun buscarPorNomeETipo(nome: String, tipo: TipoProduto?): List<Produto> {
        return produtos.filter {
            val nomeOk = it.nome.contains(nome, ignoreCase = true)
            val tipoOk = tipo == null || it.tipo == tipo
            nomeOk && tipoOk
        }
    }

    fun baixarEstoque(idProduto: Long, quantidadeVendida: Int): Boolean {
        val produto = buscarPorId(idProduto)
        return if (produto != null && produto.quantidade >= quantidadeVendida) {
            produto.quantidade -= quantidadeVendida
            true
        } else {
            false
        }
    }

    fun adicionarEstoque(idProduto:  Long, quantidadeEntrada: Int): Boolean{
        val produto = buscarPorId(idProduto)
        return if (produto != null && quantidadeEntrada > 0) {
            produto.quantidade += quantidadeEntrada
            true
        } else {
            false
        }
    }

}*/