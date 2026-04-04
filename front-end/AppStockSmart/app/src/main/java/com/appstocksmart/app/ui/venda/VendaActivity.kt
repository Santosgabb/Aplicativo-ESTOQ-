package com.appstocksmart.app.ui.venda

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appstocksmart.app.adapter.ProdutoVendaAdapter
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.api.ApiService
import com.appstocksmart.app.databinding.ActivityVendaBinding
import com.appstocksmart.app.model.ItemVenda
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.Usuario
import com.appstocksmart.app.model.Venda
import com.appstocksmart.app.util.CupomPrinter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class VendaActivity : AppCompatActivity() {

    // Binding da tela activity_venda.xml
    private lateinit var binding: ActivityVendaBinding

    // Adapter que mostra os produtos disponíveis para venda
    private lateinit var adapterProdutos: ProdutoVendaAdapter

    // Lista completa de produtos vindos da API
    private var listaProdutos: List<Produto> = emptyList()

    // Lista filtrada conforme a busca digitada
    private var listaProdutosFiltrada: List<Produto> = emptyList()

    // Carrinho da venda atual
    private val carrinho = mutableListOf<ItemVenda>()

    // Produto selecionado pelo usuário na lista
    private var produtoSelecionado: Produto? = null

    // Controle para evitar clicar várias vezes no botão de confirmar pagamento
    private var salvandoVenda = false

    // Dados do usuário logado recebidos pela Intent
    private var idUsuarioLogado: Long = -1L
    private var nomeUsuarioLogado: String = "Usuário"
    private var perfilUsuarioLogado: String = "CAIXA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding da tela
        binding = ActivityVendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera os dados do usuário logado enviados pela tela anterior
        idUsuarioLogado = intent.getLongExtra("idUsuario", -1L)
        nomeUsuarioLogado = intent.getStringExtra("nomeUsuario") ?: "Usuário"
        perfilUsuarioLogado = intent.getStringExtra("perfilUsuario") ?: "CAIXA"

        // Configura os componentes da tela
        configurarRecyclerView()
        configurarBusca()
        configurarAcoes()

        // Carrega os produtos disponíveis da API
        carregarProdutos()

        // Esconde a área de pagamento no início
        binding.layoutPagamentoVenda.visibility = View.GONE

        // Atualiza os textos iniciais da tela
        atualizarCarrinho()
        limparResumoPagamento()
    }

    /**
     * Configura o RecyclerView dos produtos.
     * Quando o usuário clicar em um produto, ele fica selecionado para adicionar ao carrinho.
     */
    private fun configurarRecyclerView() {
        adapterProdutos = ProdutoVendaAdapter(emptyList()) { produto ->
            produtoSelecionado = produto

            val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            binding.txtProdutoSelecionadoVenda.text =
                "Selecionado: ${produto.nome} | ${moeda.format(produto.precoVenda)}"
        }

        binding.recyclerProdutosVenda.layoutManager = LinearLayoutManager(this)
        binding.recyclerProdutosVenda.adapter = adapterProdutos
    }

    /**
     * Configura a busca de produtos.
     * Filtra conforme o usuário digita ou clica no botão de buscar.
     */
    private fun configurarBusca() {
        binding.edtBuscarProdutoVenda.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filtrarProdutos(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnBuscarProdutoVenda.setOnClickListener {
            filtrarProdutos(binding.edtBuscarProdutoVenda.text.toString())
        }
    }

    /**
     * Configura as ações dos botões da tela.
     */
    private fun configurarAcoes() {
        // Adiciona o produto selecionado ao carrinho
        binding.btnAdicionarCarrinho.setOnClickListener {
            adicionarAoCarrinho()
        }

        // Mostra a área de pagamento
        binding.btnFinalizarVenda.setOnClickListener {
            abrirAreaPagamento()
        }

        // Calcula valores pagos, faltante e troco
        binding.btnCalcularPagamento.setOnClickListener {
            calcularPagamento()
        }

        // Confirma e salva a venda
        binding.btnConfirmarPagamento.setOnClickListener {
            if (salvandoVenda) return@setOnClickListener
            confirmarVenda()
        }
    }

    /**
     * Carrega os produtos da API.
     * Apenas produtos com estoque maior que zero serão exibidos.
     */
    private fun carregarProdutos() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()

                    // Mostra apenas produtos com estoque disponível
                    listaProdutos = produtos.filter { it.quantidade > 0 }
                    listaProdutosFiltrada = listaProdutos

                    adapterProdutos.atualizarLista(listaProdutosFiltrada)

                    binding.txtProdutoSelecionadoVenda.text =
                        if (listaProdutos.isEmpty()) {
                            "Nenhum produto disponível"
                        } else {
                            "Selecione um produto da lista"
                        }
                } else {
                    Toast.makeText(
                        this@VendaActivity,
                        "Erro ao carregar produtos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(
                    this@VendaActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * Filtra os produtos pelo nome ou pelo tipo.
     */
    private fun filtrarProdutos(texto: String) {
        val termo = texto.trim().lowercase()

        listaProdutosFiltrada = if (termo.isEmpty()) {
            listaProdutos
        } else {
            listaProdutos.filter { produto ->
                produto.nome.lowercase().contains(termo) ||
                        produto.tipo.name.lowercase().contains(termo)
            }
        }

        adapterProdutos.atualizarLista(listaProdutosFiltrada)

        if (listaProdutosFiltrada.isEmpty()) {
            binding.txtProdutoSelecionadoVenda.text = "Nenhum produto encontrado"
        } else if (produtoSelecionado == null) {
            binding.txtProdutoSelecionadoVenda.text = "Selecione um produto da lista"
        }
    }

    /**
     * Adiciona o produto selecionado ao carrinho.
     * Também valida a quantidade e o estoque disponível.
     */
    private fun adicionarAoCarrinho() {
        val produto = produtoSelecionado

        if (produto == null) {
            Toast.makeText(this, "Selecione um produto", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = binding.edtQuantidadeVenda.text.toString().trim().toIntOrNull()

        if (quantidade == null || quantidade <= 0) {
            Toast.makeText(this, "Informe uma quantidade válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantidade > produto.quantidade) {
            Toast.makeText(this, "Quantidade maior que o estoque disponível", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Verifica se o produto já está no carrinho
        val itemExistente = carrinho.find { it.produto.id == produto.id }

        if (itemExistente != null) {
            val novaQuantidade = itemExistente.quantidade + quantidade

            if (novaQuantidade > produto.quantidade) {
                Toast.makeText(
                    this,
                    "Quantidade total no carrinho maior que o estoque",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Atualiza o item já existente no carrinho
            itemExistente.quantidade = novaQuantidade
            itemExistente.precoUnitario = produto.precoVenda
            itemExistente.subtotal = itemExistente.calcularSubtotal()
        } else {
            // Adiciona novo item ao carrinho
            carrinho.add(
                ItemVenda(
                    produto = produto,
                    quantidade = quantidade,
                    precoUnitario = produto.precoVenda,
                    subtotal = produto.precoVenda * quantidade
                )
            )
        }

        // Limpa o campo quantidade
        binding.edtQuantidadeVenda.setText("")

        // Atualiza o resumo do carrinho na tela
        atualizarCarrinho()

        Toast.makeText(this, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show()
    }

    /**
     * Atualiza o texto do carrinho e o total da venda.
     */
    private fun atualizarCarrinho() {
        val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        if (carrinho.isEmpty()) {
            binding.txtCarrinhoVenda.text = "Carrinho vazio"
            binding.txtTotalVenda.text = "Total: R$ 0,00"
            return
        }

        val texto = StringBuilder()

        carrinho.forEach { item ->
            texto.append("Produto: ${item.produto.nome}\n")
            texto.append("Qtd: ${item.quantidade}\n")
            texto.append("Subtotal: ${moeda.format(item.calcularSubtotal())}\n\n")
        }

        val total = carrinho.sumOf { it.calcularSubtotal() }

        binding.txtCarrinhoVenda.text = texto.toString()
        binding.txtTotalVenda.text = "Total: ${moeda.format(total)}"
    }

    /**
     * Mostra a área de pagamento apenas se houver itens no carrinho.
     */
    private fun abrirAreaPagamento() {
        if (carrinho.isEmpty()) {
            Toast.makeText(this, "Adicione itens ao carrinho", Toast.LENGTH_SHORT).show()
            return
        }

        binding.layoutPagamentoVenda.visibility = View.VISIBLE
    }

    /**
     * Retorna o subtotal da venda.
     */
    private fun obterSubtotal(): Double = carrinho.sumOf { it.calcularSubtotal() }

    /**
     * Retorna o desconto digitado.
     * Se estiver vazio, retorna 0.
     */
    private fun obterDesconto(): Double =
        binding.edtDescontoVenda.text.toString().trim().toDoubleOrNull() ?: 0.0

    /**
     * Retorna o acréscimo digitado.
     * Se estiver vazio, retorna 0.
     */
    private fun obterAcrescimo(): Double =
        binding.edtAcrescimoVenda.text.toString().trim().toDoubleOrNull() ?: 0.0

    /**
     * Calcula o total final: subtotal - desconto + acréscimo
     */
    private fun obterTotalFinal(): Double = obterSubtotal() - obterDesconto() + obterAcrescimo()

    /**
     * Retorna o valor pago em dinheiro.
     */
    private fun obterPagamentoDinheiro(): Double =
        binding.edtPagamentoDinheiro.text.toString().trim().toDoubleOrNull() ?: 0.0

    /**
     * Retorna o valor pago em pix.
     */
    private fun obterPagamentoPix(): Double =
        binding.edtPagamentoPix.text.toString().trim().toDoubleOrNull() ?: 0.0

    /**
     * Retorna o valor pago em débito.
     */
    private fun obterPagamentoDebito(): Double =
        binding.edtPagamentoDebito.text.toString().trim().toDoubleOrNull() ?: 0.0

    /**
     * Retorna o valor pago em crédito.
     */
    private fun obterPagamentoCredito(): Double =
        binding.edtPagamentoCredito.text.toString().trim().toDoubleOrNull() ?: 0.0

    /**
     * Calcula o pagamento total, faltante e troco.
     * Também exige login e senha do gerente caso haja desconto.
     *
     * Retorna true se o valor pago for suficiente.
     */
    private fun calcularPagamento(): Boolean {
        val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        val desconto = obterDesconto()
        val totalFinal = obterTotalFinal()

        // Se houver desconto, exige autenticação do gerente
        if (desconto > 0) {
            val loginGerente = binding.edtLoginGerente.text.toString().trim()
            val senhaGerente = binding.edtSenhaGerente.text.toString().trim()

            if (loginGerente.isEmpty() || senhaGerente.isEmpty()) {
                Toast.makeText(
                    this,
                    "Informe login e senha do gerente para aplicar desconto",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        // Soma todas as formas de pagamento
        val totalPago = obterPagamentoDinheiro() +
                obterPagamentoPix() +
                obterPagamentoDebito() +
                obterPagamentoCredito()

        val faltante = totalFinal - totalPago
        val troco = if (totalPago > totalFinal) totalPago - totalFinal else 0.0

        // Atualiza resumo na tela
        binding.txtValorPago.text = "Pago: ${moeda.format(totalPago)}"
        binding.txtValorFaltante.text =
            "Falta: ${moeda.format(if (faltante > 0) faltante else 0.0)}"
        binding.txtTroco.text = "Troco: ${moeda.format(troco)}"

        return faltante <= 0
    }

    /**
     * Limpa o resumo visual de pagamento.
     */
    private fun limparResumoPagamento() {
        binding.txtValorPago.text = "Pago: R$ 0,00"
        binding.txtValorFaltante.text = "Falta: R$ 0,00"
        binding.txtTroco.text = "Troco: R$ 0,00"
    }

    /**
     * Confirma a venda:
     * - valida carrinho
     * - valida usuário
     * - valida pagamento
     * - monta o objeto Venda
     * - envia para a API
     * - imprime o cupom quando salvar com sucesso
     */
    private fun confirmarVenda() {
        if (carrinho.isEmpty()) {
            Toast.makeText(this, "Adicione itens ao carrinho", Toast.LENGTH_SHORT).show()
            return
        }

        if (idUsuarioLogado == -1L) {
            Toast.makeText(this, "Usuário logado inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (!calcularPagamento()) {
            Toast.makeText(
                this,
                "Ainda falta valor para finalizar a venda",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val desconto = obterDesconto()

        // Marca se o desconto foi autorizado
        val descontoAutorizado = desconto <= 0 ||
                (
                        binding.edtLoginGerente.text.toString().trim().isNotEmpty() &&
                                binding.edtSenhaGerente.text.toString().trim().isNotEmpty()
                        )

        // Monta o usuário que será enviado na venda
        val usuarioVenda = Usuario(
            id = idUsuarioLogado,
            nome = nomeUsuarioLogado,
            login = "",
            senha = "",
            perfil = perfilUsuarioLogado
        )

        // Monta o objeto da venda
        val venda = Venda(
            usuario = usuarioVenda,
            itens = carrinho.toList(),
            subtotal = obterSubtotal(),
            desconto = desconto,
            acrescimo = obterAcrescimo(),
            totalFinal = obterTotalFinal(),
            pagamentoDinheiro = obterPagamentoDinheiro(),
            pagamentoPix = obterPagamentoPix(),
            pagamentoDebito = obterPagamentoDebito(),
            pagamentoCredito = obterPagamentoCredito(),
            descontoAutorizadoPorGerente = descontoAutorizado,
            dataHora = null
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Bloqueia múltiplos cliques
        salvandoVenda = true
        binding.btnConfirmarPagamento.isEnabled = false

        apiService.salvarVenda(venda).enqueue(object : Callback<Venda> {

            override fun onResponse(call: Call<Venda>, response: Response<Venda>) {
                salvandoVenda = false
                binding.btnConfirmarPagamento.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@VendaActivity,
                        "Venda salva com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()

                    /*
                     * Usa a venda retornada pelo back-end, se existir.
                     * Se o back-end não retornar corpo, usa a venda montada localmente.
                     */
                    val vendaParaImpressao = response.body() ?: venda

                    // Chama a impressão do cupom não fiscal
                    CupomPrinter.imprimirCupom(this@VendaActivity, vendaParaImpressao)

                    // Limpa a tela para uma nova venda
                    limparVenda()

                    // Recarrega os produtos para atualizar o estoque na tela
                    carregarProdutos()
                } else {
                    val erro = response.errorBody()?.string()
                    android.util.Log.e("VENDA_API", "Erro ${response.code()} - $erro")

                    Toast.makeText(
                        this@VendaActivity,
                        "Erro ao salvar venda: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Venda>, t: Throwable) {
                salvandoVenda = false
                binding.btnConfirmarPagamento.isEnabled = true

                Toast.makeText(
                    this@VendaActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * Limpa toda a venda atual para iniciar uma nova.
     */
    private fun limparVenda() {
        carrinho.clear()
        produtoSelecionado = null

        binding.txtProdutoSelecionadoVenda.text = "Selecione um produto da lista"
        binding.edtQuantidadeVenda.setText("")
        binding.edtBuscarProdutoVenda.setText("")
        binding.edtDescontoVenda.setText("")
        binding.edtAcrescimoVenda.setText("")
        binding.edtLoginGerente.setText("")
        binding.edtSenhaGerente.setText("")
        binding.edtPagamentoDinheiro.setText("")
        binding.edtPagamentoPix.setText("")
        binding.edtPagamentoDebito.setText("")
        binding.edtPagamentoCredito.setText("")

        binding.layoutPagamentoVenda.visibility = View.GONE

        atualizarCarrinho()
        limparResumoPagamento()
    }
}