package com.appstocksmart.app.model

/**
 * Representa uma venda realizada no sistema.
 *
 * Essa classe agrupa:
 * - o usuário que fez a venda
 * - os itens vendidos
 * - os valores financeiros
 * - as formas de pagamento
 * - a informação de autorização de desconto
 * - a data/hora da venda
 */
data class Venda(

    /**
     * Identificador único da venda.
     * Pode ser nulo antes da venda ser salva no banco.
     */
    val id: Long? = null,

    /**
     * Usuário responsável pela venda.
     */
    val usuario: Usuario,

    /**
     * Lista de itens da venda.
     */
    val itens: List<ItemVenda>,

    /**
     * Valor subtotal da venda.
     */
    val subtotal: Double,

    /**
     * Valor de desconto aplicado.
     */
    val desconto: Double,

    /**
     * Valor de acréscimo aplicado.
     */
    val acrescimo: Double,

    /**
     * Valor total final da venda.
     */
    val totalFinal: Double,

    /**
     * Valor pago em dinheiro.
     */
    val pagamentoDinheiro: Double,

    /**
     * Valor pago via Pix.
     */
    val pagamentoPix: Double,

    /**
     * Valor pago no débito.
     */
    val pagamentoDebito: Double,

    /**
     * Valor pago no crédito.
     */
    val pagamentoCredito: Double,

    /**
     * Indica se o desconto foi autorizado por gerente.
     */
    val descontoAutorizadoPorGerente: Boolean,

    /**
     * Data e hora da venda.
     * Pode ser preenchida pelo back-end.
     */
    val dataHora: String? = null
)