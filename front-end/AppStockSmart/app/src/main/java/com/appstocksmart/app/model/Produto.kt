package com.appstocksmart.app.model

data class Produto(
    val id: Long? = null,
    var nome: String,
    var tipo: TipoProduto,
    var precoVenda: Double,
    var precoCusto: Double,
    var quantidade: Int,
    var fornecedor: Fornecedor
)