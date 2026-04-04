package com.appstocksmart.app.model

/*
 * Data class que representa um item dentro de uma venda.
 * Cada item da venda corresponde a um produto escolhido,
 * com sua quantidade, preço unitário e subtotal.
 */
data class ItemVenda(

    /*
     * Produto que está sendo vendido.
     */
    val produto: Produto,

    /*
     * Quantidade desse produto na venda.
     */
    var quantidade: Int,

    /*
     * Preço unitário do produto no momento da venda.
     * Está como nullable porque pode não vir preenchido em alguns momentos.
     */
    var precoUnitario: Double? = null,

    /*
     * Subtotal do item.
     * Geralmente é quantidade * preço unitário.
     */
    var subtotal: Double? = null

) {

    /*
     * Calcula o subtotal do item com base no preço de venda atual do produto
     * multiplicado pela quantidade.
     */
    fun calcularSubtotal(): Double {
        return produto.precoVenda * quantidade
    }

}