package com.appstocksmart.app.model

import com.google.gson.annotations.SerializedName

/*
 * Modelo enviado para o back-end no fechamento de caixa.
 *
 * Campos esperados pelo back-end:
 * - operadorId
 * - gerenteResponsavelId
 * - dataReferencia
 * - valorInicialCaixa
 * - saidas
 * - observacao
 */
data class FechamentoCaixa(
    val operadorId: Long,
    val gerenteResponsavelId: Long,
    val dataReferencia: String,
    val valorInicialCaixa: Double,
    val saidas: List<SaidaCaixa> = emptyList(),
    val observacao: String? = null
)

/*
 * Modelo retornado pelo back-end.
 *
 * Usei SerializedName em alguns campos para manter
 * compatibilidade com a Activity que você já tinha.
 */
data class ResumoFechamentoCaixa(
    val operadorId: Long? = null,
    val operadorNome: String? = null,

    @SerializedName("dataReferencia")
    val data: String? = null,

    @SerializedName("quantidadeVendas")
    val quantidadeVendas: Int = 0,

    /*
     * Na Activity antiga você mostrava "totalVendas".
     * Aqui mapeamos esse nome para o total líquido do dia.
     */
    @SerializedName("totalLiquido")
    val totalVendas: Double = 0.0,

    val valorInicialCaixa: Double = 0.0,
    val totalBruto: Double = 0.0,
    val totalDescontos: Double = 0.0,
    val totalAcrescimos: Double = 0.0,
    val totalDinheiro: Double = 0.0,
    val totalPix: Double = 0.0,
    val totalDebito: Double = 0.0,
    val totalCredito: Double = 0.0,
    val totalSaidas: Double = 0.0,

    @SerializedName("dinheiroFinalCaixa")
    val dinheiroFinalEmCaixa: Double = 0.0,

    val observacao: String? = null
)