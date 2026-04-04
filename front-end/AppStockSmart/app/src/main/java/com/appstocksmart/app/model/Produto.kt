package com.appstocksmart.app.model

// Model que representa um produto no app
data class Produto(

    // Id do produto no banco
    // Quando vai cadastrar um produto novo, normalmente vem null
    val id: Long? = null,

    // Nome do produto
    var nome: String,

    // Tipo do produto (enum)
    var tipo: TipoProduto,

    // Preço de venda do produto
    var precoVenda: Double,

    // Preço de custo do produto
    var precoCusto: Double,

    // Quantidade disponível em estoque
    var quantidade: Int,

    // Fornecedor vinculado ao produto
    var fornecedor: Fornecedor,

    // Indica se o produto está ativo ou inativo
    // Por padrão, produto novo já nasce ativo
    var ativo: Boolean = true
)