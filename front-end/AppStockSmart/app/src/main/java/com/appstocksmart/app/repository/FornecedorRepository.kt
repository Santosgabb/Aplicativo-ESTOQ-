/*package com.appstocksmart.app.repository

import com.appstocksmart.app.model.Fornecedor

object FornecedorRepository {

    private val fornecedores = mutableListOf(
        Fornecedor(1, "Distribuidora Central", "61999999999", "central@email.com"),
        Fornecedor(2, "Atacado Brasil", "61888888888", "atacado@email.com")
    )

    fun listar(): List<Fornecedor> {
        return fornecedores
    }

    fun adicionar(fornecedor: Fornecedor) {
        fornecedores.add(fornecedor)
    }

    fun atualizar(fornecedorAtualizado: Fornecedor) {
        val index = fornecedores.indexOfFirst { it.id == fornecedorAtualizado.id }
        if (index != -1) {
            fornecedores[index] = fornecedorAtualizado
        }
    }

    fun remover(id: Long) {
        fornecedores.removeIf { it.id == id }
    }

    fun buscarPorId(id: Long): Fornecedor? {
        return fornecedores.find { it.id == id }
    }

    fun proximoId(): Long {
        return if (fornecedores.isEmpty()) 1 else fornecedores.maxOf {it.id.toLong()  } + 1
    }
}
*/
