package com.appstocksmart.app.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.databinding.ActivityMenuBinding
import com.appstocksmart.app.ui.fornecedor.FornecedorActivity
import com.appstocksmart.app.ui.produto.ProdutoActivity
import com.appstocksmart.app.ui.relatorio.RelatorioActivity
import com.appstocksmart.app.ui.usuario.UsuarioActivity
import com.appstocksmart.app.ui.venda.VendaActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idUsuario = intent.getLongExtra("idUsuario", -1L)
        val nomeUsuario = intent.getStringExtra("nomeUsuario") ?: "Usuário"
        val perfilUsuario = intent.getStringExtra("perfilUsuario") ?: "SEM PERFIL"

        binding.txtNomeUsuario.text = "Olá, $nomeUsuario"
        binding.txtPerfilUsuario.text = "Perfil: $perfilUsuario"

        if (perfilUsuario == "CAIXA") {
            binding.btnUsuarios.visibility = View.GONE
            binding.btnProdutos.visibility = View.GONE
            binding.btnRelatorios.visibility = View.GONE
            binding.btnFornecedor.visibility = View.GONE
        }

        binding.btnProdutos.setOnClickListener {
            val intent = Intent(this, ProdutoActivity::class.java)
            startActivity(intent)
        }

        binding.btnVendas.setOnClickListener {
            val intent = Intent(this, VendaActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            intent.putExtra("nomeUsuario", nomeUsuario)
            intent.putExtra("perfilUsuario", perfilUsuario)
            startActivity(intent)
        }

        binding.btnUsuarios.setOnClickListener {
            val intent = Intent(this, UsuarioActivity::class.java)
            startActivity(intent)
        }

        binding.btnRelatorios.setOnClickListener {
            val intent = Intent(this, RelatorioActivity::class.java)
            startActivity(intent)
        }

        binding.btnFornecedor.setOnClickListener {
            val intent = Intent(this, FornecedorActivity::class.java)
            startActivity(intent)
        }
    }
}