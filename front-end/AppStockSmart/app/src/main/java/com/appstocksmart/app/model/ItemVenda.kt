package com.appstocksmart.app.model

data class ItemVenda(

    val produto: Produto,

    var quantidade: Int,

    var precoUnitario: Double? = null,

    var subtotal: Double? = null

) {

    fun calcularSubtotal(): Double {
        return produto.precoVenda * quantidade
    }

}