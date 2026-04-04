package com.appstocksmart.app.ui.fornecedor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appstocksmart.app.api.ApiClient
import com.appstocksmart.app.databinding.ActivityEditarFornecedorBinding
import com.appstocksmart.app.model.Fornecedor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarFornecedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarFornecedorBinding
    private var idFornecedor: Long = -1L

    // Serviço da API
    private val apiService by lazy { ApiClient.apiService }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout com ViewBinding
        binding = ActivityEditarFornecedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a barra superior
        supportActionBar?.title = "Editar Fornecedor"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Recupera o ID enviado pela tela anterior
        idFornecedor = intent.getLongExtra("idFornecedor", -1L)

        // Valida o ID
        if (idFornecedor == -1L) {
            Toast.makeText(this, "Fornecedor inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carrega os dados do fornecedor
        carregarDadosFornecedor()

        // Clique do botão salvar
        binding.btnSalvarEdicaoFornecedor.setOnClickListener {
            salvarEdicao()
        }
    }

    /*
     * Busca os dados do fornecedor pelo ID
     * e preenche os campos da tela.
     */
    private fun carregarDadosFornecedor() {
        apiService.buscarFornecedorPorId(idFornecedor)
            .enqueue(object : Callback<Fornecedor> {

                override fun onResponse(
                    call: Call<Fornecedor>,
                    response: Response<Fornecedor>
                ) {
                    if (response.isSuccessful) {
                        val fornecedor = response.body()

                        if (fornecedor == null) {
                            Toast.makeText(
                                this@EditarFornecedorActivity,
                                "Fornecedor não encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            return
                        }

                        // Preenche os campos do formulário
                        binding.edtEditarNomeFornecedor.setText(fornecedor.nome ?: "")
                        binding.edtEditarTelefoneFornecedor.setText(fornecedor.telefone ?: "")
                        binding.edtEditarEmailFornecedor.setText(fornecedor.email ?: "")
                    } else {
                        Toast.makeText(
                            this@EditarFornecedorActivity,
                            "Erro ao buscar fornecedor",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<Fornecedor>, t: Throwable) {
                    Toast.makeText(
                        this@EditarFornecedorActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
    }

    /*
     * Valida os campos e envia a atualização para o back-end.
     */
    private fun salvarEdicao() {
        val nome = binding.edtEditarNomeFornecedor.text.toString().trim()
        val telefone = binding.edtEditarTelefoneFornecedor.text.toString().trim()
        val email = binding.edtEditarEmailFornecedor.text.toString().trim()

        // Validação simples
        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fornecedorAtualizado = Fornecedor(
            id = idFornecedor,
            nome = nome,
            telefone = telefone,
            email = email
        )

        apiService.atualizarFornecedor(idFornecedor, fornecedorAtualizado)
            .enqueue(object : Callback<Fornecedor> {

                override fun onResponse(
                    call: Call<Fornecedor>,
                    response: Response<Fornecedor>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EditarFornecedorActivity,
                            "Fornecedor atualizado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditarFornecedorActivity,
                            "Erro ao atualizar: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Fornecedor>, t: Throwable) {
                    Toast.makeText(
                        this@EditarFornecedorActivity,
                        "Falha na conexão: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /*
     * Ação da seta de voltar da ActionBar.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}