package com.appstocksmart.app.repository

import com.appstocksmart.app.model.EntradaEstoque

object EntradaEstoqueRepository {

    private val entradas = mutableListOf<EntradaEstoque>()

    fun adicionar(entrada: EntradaEstoque) {
        entradas.add(entrada)
    }

    fun listar(): List<EntradaEstoque> {
        return entradas
    }

    fun proximoId(): Int {
        return if (entradas.isEmpty()) 1 else entradas.maxOf { it.id } + 1
    }
}