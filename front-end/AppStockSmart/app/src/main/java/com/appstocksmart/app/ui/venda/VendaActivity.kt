package com.appstocksmart.app.ui.venda

import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendaBinding
    private lateinit var adapter: ProdutoVendaAdapter

    private val carrinho = mutableListOf<ItemVenda>()
    private var produtoSelecionado: Produto? = null
    private var salvandoVenda = false

    private var idUsuarioLogado: Long = -1L
    private var nomeUsuarioLogado: String = ""
    private var perfilUsuarioLogado: String = ""

    // lista vinda da API para busca/filtro local
    private var listaProdutos: List<Produto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idUsuarioLogado = intent.getLongExtra("idUsuario", -1L)
        nomeUsuarioLogado = intent.getStringExtra("nomeUsuario") ?: "Usuário"
        perfilUsuarioLogado = intent.getStringExtra("perfilUsuario") ?: "CAIXA"

        configurarRecyclerView()
        carregarProdutosDisponiveis()

        binding.layoutPagamentoVenda.visibility = View.GONE

        binding.btnBuscarProdutoVenda.setOnClickListener {
            buscarProdutos()
        }

        binding.btnAdicionarCarrinho.setOnClickListener {
            adicionarAoCarrinho()
        }

        binding.btnFinalizarVenda.setOnClickListener {
            abrirAreaPagamento()
        }

        binding.btnCalcularPagamento.setOnClickListener {
            calcularPagamento()
        }

        binding.btnConfirmarPagamento.setOnClickListener {
            if (salvandoVenda) return@setOnClickListener
            confirmarVenda()
        }

        atualizarCarrinho()
        limparResumoPagamento()
    }

    private fun configurarRecyclerView() {
        adapter = ProdutoVendaAdapter(emptyList()) { produto ->
            produtoSelecionado = produto
            binding.txtProdutoSelecionadoVenda.text =
                "Selecionado: ${produto.nome} | Venda: R$ %.2f".format(produto.precoVenda)
        }

        binding.recyclerProdutosVenda.layoutManager = LinearLayoutManager(this)
        binding.recyclerProdutosVenda.adapter = adapter
    }

    private fun carregarProdutosDisponiveis() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.listarProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(
                call: Call<List<Produto>>,
                response: Response<List<Produto>>
            ) {
                if (response.isSuccessful) {
                    listaProdutos = response.body()?.filter { it.quantidade > 0 } ?: emptyList()
                    adapter.atualizarLista(listaProdutos)
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

    private fun buscarProdutos() {
        val nomeBusca = binding.edtBuscarProdutoVenda.text.toString().trim()

        val produtos = if (nomeBusca.isEmpty()) {
            listaProdutos
        } else {
            listaProdutos.filter {
                it.nome.contains(nomeBusca, ignoreCase = true)
            }
        }

        adapter.atualizarLista(produtos)

        if (produtos.isEmpty()) {
            Toast.makeText(this, "Nenhum produto encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun adicionarAoCarrinho() {
        val produto = produtoSelecionado

        if (produto == null) {
            Toast.makeText(this, "Selecione um produto", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidadeTexto = binding.edtQuantidadeVenda.text.toString().trim()

        if (quantidadeTexto.isEmpty()) {
            Toast.makeText(this, "Digite a quantidade", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = quantidadeTexto.toIntOrNull()

        if (quantidade == null || quantidade <= 0) {
            Toast.makeText(this, "Quantidade inválida", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantidade > produto.quantidade) {
            Toast.makeText(this, "Estoque insuficiente", Toast.LENGTH_SHORT).show()
            return
        }

        val itemExistente = carrinho.find { it.produto.id == produto.id }

        if (itemExistente != null) {
            if (itemExistente.quantidade + quantidade > produto.quantidade) {
                Toast.makeText(
                    this,
                    "Quantidade total no carrinho excede o estoque",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            itemExistente.quantidade += quantidade
        } else {
            carrinho.add(ItemVenda(produto, quantidade))
        }

        binding.edtQuantidadeVenda.setText("")
        atualizarCarrinho()
    }

    private fun atualizarCarrinho() {
        if (carrinho.isEmpty()) {
            binding.txtCarrinhoVenda.text = "Carrinho vazio"
            binding.txtTotalVenda.text = "Total: R$ 0,00"
            return
        }

        val texto = StringBuilder()

        for (item in carrinho) {
            texto.append("Produto: ${item.produto.nome}\n")
            texto.append("Qtd: ${item.quantidade}\n")
            texto.append("Subtotal: R$ %.2f\n\n".format(item.calcularSubtotal()))
        }

        val total = carrinho.sumOf { it.calcularSubtotal() }

        binding.txtCarrinhoVenda.text = texto.toString()
        binding.txtTotalVenda.text = "Total: R$ %.2f".format(total)
    }

    private fun abrirAreaPagamento() {
        if (carrinho.isEmpty()) {
            Toast.makeText(this, "Adicione itens ao carrinho", Toast.LENGTH_SHORT).show()
            return
        }

        binding.layoutPagamentoVenda.visibility = View.VISIBLE
    }

    private fun obterSubtotal(): Double {
        return carrinho.sumOf { it.calcularSubtotal() }
    }

    private fun obterDesconto(): Double {
        return binding.edtDescontoVenda.text.toString().trim().toDoubleOrNull() ?: 0.0
    }

    private fun obterAcrescimo(): Double {
        return binding.edtAcrescimoVenda.text.toString().trim().toDoubleOrNull() ?: 0.0
    }

    private fun obterTotalFinal(): Double {
        val subtotal = obterSubtotal()
        val desconto = obterDesconto()
        val acrescimo = obterAcrescimo()

        val total = subtotal - desconto + acrescimo
        return if (total < 0) 0.0 else total
    }

    private fun obterPagamentoDinheiro(): Double {
        return binding.edtPagamentoDinheiro.text.toString().trim().toDoubleOrNull() ?: 0.0
    }

    private fun obterPagamentoPix(): Double {
        return binding.edtPagamentoPix.text.toString().trim().toDoubleOrNull() ?: 0.0
    }

    private fun obterPagamentoDebito(): Double {
        return binding.edtPagamentoDebito.text.toString().trim().toDoubleOrNull() ?: 0.0
    }

    private fun obterPagamentoCredito(): Double {
        return binding.edtPagamentoCredito.text.toString().trim().toDoubleOrNull() ?: 0.0
    }

    private fun calcularPagamento(): Boolean {
        val subtotal = obterSubtotal()
        val desconto = obterDesconto()
        val acrescimo = obterAcrescimo()

        val totalFinal = if (subtotal - desconto + acrescimo < 0) {
            0.0
        } else {
            subtotal - desconto + acrescimo
        }

        val dinheiro = obterPagamentoDinheiro()
        val pix = obterPagamentoPix()
        val debito = obterPagamentoDebito()
        val credito = obterPagamentoCredito()

        val totalPago = dinheiro + pix + debito + credito
        val faltante = totalFinal - totalPago
        val troco = if (totalPago > totalFinal) totalPago - totalFinal else 0.0

        binding.txtValorPago.text = "Pago: R$ %.2f".format(totalPago)
        binding.txtValorFaltante.text = "Falta: R$ %.2f".format(if (faltante > 0) faltante else 0.0)
        binding.txtTroco.text = "Troco: R$ %.2f".format(troco)

        return faltante <= 0
    }

    private fun limparResumoPagamento() {
        binding.txtValorPago.text = "Pago: R$ 0,00"
        binding.txtValorFaltante.text = "Falta: R$ 0,00"
        binding.txtTroco.text = "Troco: R$ 0,00"
    }

    private fun confirmarVenda() {
        if (carrinho.isEmpty()) {
            Toast.makeText(this, "Adicione itens ao carrinho", Toast.LENGTH_SHORT).show()
            return
        }

        if (idUsuarioLogado == -1L) {
            Toast.makeText(this, "Usuário logado inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val pagamentoOk = calcularPagamento()

        if (!pagamentoOk) {
            Toast.makeText(
                this,
                "Ainda falta valor para finalizar a venda",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val subtotal = obterSubtotal()
        val desconto = obterDesconto()
        val acrescimo = obterAcrescimo()
        val totalFinal = obterTotalFinal()

        val dinheiro = obterPagamentoDinheiro()
        val pix = obterPagamentoPix()
        val debito = obterPagamentoDebito()
        val credito = obterPagamentoCredito()

        // enquanto não houver validação real do gerente via API,
        // mantém só o flag baseado em ter desconto
        val descontoAutorizado = desconto > 0

        val usuarioVenda = Usuario(
            id = idUsuarioLogado,
            nome = nomeUsuarioLogado,
            login = "",
            senha = "",
            perfil = perfilUsuarioLogado
        )

        val venda = Venda(
            usuario = usuarioVenda,
            itens = carrinho.toList(),
            subtotal = subtotal,
            desconto = desconto,
            acrescimo = acrescimo,
            totalFinal = totalFinal,
            pagamentoDinheiro = dinheiro,
            pagamentoPix = pix,
            pagamentoDebito = debito,
            pagamentoCredito = credito,
            descontoAutorizadoPorGerente = descontoAutorizado,
            dataHora = null
        )

        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        salvandoVenda = true
        binding.btnConfirmarPagamento.isEnabled = false

        apiService.salvarVenda(venda).enqueue(object : Callback<Venda> {

            override fun onResponse(call: Call<Venda>, response: Response<Venda>) {
                salvandoVenda = false
                binding.btnConfirmarPagamento.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@VendaActivity,
                        "Venda salva no servidor com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()

                    limparVenda()
                    carregarProdutosDisponiveis()
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e(
                        "VENDA_API",
                        "Erro ${response.code()} - $errorBody"
                    )

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

    private fun limparVenda() {
        carrinho.clear()
        produtoSelecionado = null

        binding.txtProdutoSelecionadoVenda.text = "Nenhum produto selecionado"
        binding.edtPagamentoDinheiro.setText("")
        binding.edtPagamentoPix.setText("")
        binding.edtPagamentoDebito.setText("")
        binding.edtPagamentoCredito.setText("")
        binding.edtDescontoVenda.setText("")
        binding.edtAcrescimoVenda.setText("")
        binding.edtLoginGerente.setText("")
        binding.edtSenhaGerente.setText("")
        binding.edtBuscarProdutoVenda.setText("")
        binding.edtQuantidadeVenda.setText("")

        binding.layoutPagamentoVenda.visibility = View.GONE

        atualizarCarrinho()
        limparResumoPagamento()
    }
}