package com.appstocksmart.app.util

import android.app.Activity
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appstocksmart.app.model.ItemVenda
import com.appstocksmart.app.model.Venda
import java.text.NumberFormat
import java.util.Locale

/*
 * Objeto responsável por gerar e imprimir um cupom não fiscal.
 *
 * Ele monta um HTML com os dados da venda e envia para o sistema
 * de impressão nativo do Android.
 */
object CupomPrinter {

    // Locale do Brasil para formatar moeda em R$
    private val localeBr = Locale("pt", "BR")

    // Formatador de moeda brasileira
    private val moeda = NumberFormat.getCurrencyInstance(localeBr)

    /*
     * Função principal para imprimir o cupom.
     *
     * Recebe:
     * - activity: tela atual
     * - venda: venda que será usada para gerar o cupom
     */
    fun imprimirCupom(activity: Activity, venda: Venda) {
        // Cria um WebView apenas para renderizar o HTML do cupom
        val webView = WebView(activity)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Quando o HTML terminar de carregar, chama a impressão
                val printManager =
                    activity.getSystemService(Context.PRINT_SERVICE) as PrintManager

                val jobName = "cupom_venda_${venda.id ?: System.currentTimeMillis()}"

                val printAdapter = webView.createPrintDocumentAdapter(jobName)

                printManager.print(
                    jobName,
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }

        // Gera o HTML do cupom
        val html = gerarHtmlCupom(venda)

        // Carrega o HTML no WebView
        webView.loadDataWithBaseURL(
            null,
            html,
            "text/html",
            "UTF-8",
            null
        )
    }

    /*
     * Gera o HTML do cupom com base na venda recebida.
     */
    private fun gerarHtmlCupom(venda: Venda): String {
        val itensHtml = venda.itens.joinToString(separator = "") { item ->
            gerarLinhaItem(item)
        }

        val dataHoraTexto = venda.dataHora ?: "Data/Hora não informada"

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: monospace;
                        padding: 16px;
                        color: #000;
                    }

                    .centro {
                        text-align: center;
                    }

                    .titulo {
                        font-size: 18px;
                        font-weight: bold;
                        margin-bottom: 6px;
                    }

                    .subtitulo {
                        font-size: 14px;
                        margin-bottom: 14px;
                    }

                    .linha {
                        margin: 4px 0;
                        font-size: 13px;
                    }

                    .separador {
                        border-top: 1px dashed #000;
                        margin: 10px 0;
                    }

                    table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 12px;
                    }

                    th, td {
                        text-align: left;
                        padding: 4px 2px;
                        vertical-align: top;
                    }

                    .total {
                        font-size: 15px;
                        font-weight: bold;
                    }

                    .rodape {
                        text-align: center;
                        margin-top: 18px;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="centro titulo">CUPOM NÃO FISCAL</div>
                <div class="centro subtitulo">Comprovante da venda realizada</div>

                <div class="linha"><b>Venda:</b> ${venda.id ?: "-"}</div>
                <div class="linha"><b>Data/Hora:</b> ${escaparHtml(dataHoraTexto)}</div>
                <div class="linha"><b>Operador:</b> ${escaparHtml(venda.usuario.nome)}</div>

                <div class="separador"></div>

                <table>
                    <thead>
                        <tr>
                            <th>Produto</th>
                            <th>Qtd</th>
                            <th>Unit.</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        $itensHtml
                    </tbody>
                </table>

                <div class="separador"></div>

                <div class="linha"><b>Subtotal:</b> ${formatarMoeda(venda.subtotal)}</div>
                <div class="linha"><b>Desconto:</b> ${formatarMoeda(venda.desconto)}</div>
                <div class="linha"><b>Acréscimo:</b> ${formatarMoeda(venda.acrescimo)}</div>
                <div class="linha total"><b>Total Final:</b> ${formatarMoeda(venda.totalFinal)}</div>

                <div class="separador"></div>

                <div class="linha"><b>Pago em Dinheiro:</b> ${formatarMoeda(venda.pagamentoDinheiro)}</div>
                <div class="linha"><b>Pago em Pix:</b> ${formatarMoeda(venda.pagamentoPix)}</div>
                <div class="linha"><b>Pago em Débito:</b> ${formatarMoeda(venda.pagamentoDebito)}</div>
                <div class="linha"><b>Pago em Crédito:</b> ${formatarMoeda(venda.pagamentoCredito)}</div>

                <div class="separador"></div>

                <div class="linha">
                    <b>Desconto autorizado por gerente:</b>
                    ${if (venda.descontoAutorizadoPorGerente) "Sim" else "Não"}
                </div>

                <div class="rodape">
                    Obrigado pela preferência!<br><br>
                    Este cupom não é documento fiscal.
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    /*
     * Gera uma linha da tabela para cada item da venda.
     */
    private fun gerarLinhaItem(item: ItemVenda): String {
        // Se precoUnitario estiver nulo, usa o precoVenda do produto
        val precoUnitario = item.precoUnitario ?: item.produto.precoVenda

        // Se subtotal estiver nulo, calcula pelo método do item
        val subtotal = item.subtotal ?: item.calcularSubtotal()

        return """
            <tr>
                <td>${escaparHtml(item.produto.nome)}</td>
                <td>${item.quantidade}</td>
                <td>${formatarMoeda(precoUnitario)}</td>
                <td>${formatarMoeda(subtotal)}</td>
            </tr>
        """.trimIndent()
    }

    /*
     * Formata um valor Double no padrão monetário brasileiro.
     */
    private fun formatarMoeda(valor: Double): String {
        return moeda.format(valor)
    }

    /*
     * Escapa caracteres especiais para evitar problemas no HTML.
     */
    private fun escaparHtml(texto: String): String {
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}