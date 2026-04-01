package com.appstocksmart.app.repository

import com.appstocksmart.app.model.Venda

object VendaRepository {

    private val vendas = mutableListOf<Venda>()

    fun adicionar(venda: Venda) {
        vendas.add(venda)
    }

    fun listar(): List<Venda> {
        return vendas
    }

    fun buscarPorId(id: Long): Venda? {
        return vendas.find { it.id == id }
    }

    fun proximoId(): Long {
        return if (vendas.isEmpty()) {
            1L
        } else {
            (vendas.maxOf { it.id ?: 0L }) + 1L
        }
    }
}