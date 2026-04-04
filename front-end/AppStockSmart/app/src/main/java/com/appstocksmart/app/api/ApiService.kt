package com.appstocksmart.app.api

// Models usados nas requisições e respostas
import com.appstocksmart.app.model.FechamentoCaixa
import com.appstocksmart.app.model.Fornecedor
import com.appstocksmart.app.model.HistoricoFechamentoCaixa
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.ResumoFechamentoCaixa
import com.appstocksmart.app.model.Usuario
import com.appstocksmart.app.model.Venda

// Classe do Retrofit para chamadas HTTP
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/*
 * Interface que define todos os endpoints da API.
 *
 * Cada função representa uma rota do back-end.
 */
interface ApiService {

    // =========================================================
    // PRODUTOS
    // =========================================================

    /*
     * Lista somente os produtos ativos.
     */
    @GET("produtos")
    fun listarProdutos(): Call<List<Produto>>

    /*
     * Busca um produto pelo ID.
     */
    @GET("produtos/{id}")
    fun buscarProdutoPorId(@Path("id") id: Long): Call<Produto>

    /*
     * Salva um novo produto.
     */
    @POST("produtos")
    fun salvarProduto(@Body produto: Produto): Call<Produto>

    /*
     * Atualiza um produto existente.
     */
    @PUT("produtos/{id}")
    fun atualizarProduto(
        @Path("id") id: Long,
        @Body produto: Produto
    ): Call<Produto>

    /*
     * Exclui um produto fisicamente.
     * Hoje essa rota não é a mais indicada,
     * porque no app estamos trabalhando com inativação.
     */
    @DELETE("produtos/{id}")
    fun deletarProduto(@Path("id") id: Long): Call<Void>

    /*
     * Lista todos os produtos, ativos e inativos.
     */
    @GET("produtos/todos")
    fun listarTodosProdutos(): Call<List<Produto>>

    /*
     * Lista apenas produtos inativos.
     */
    @GET("produtos/inativos")
    fun listarProdutosInativos(): Call<List<Produto>>

    /*
     * Inativa um produto.
     */
    @PUT("produtos/{id}/inativar")
    fun inativarProduto(@Path("id") id: Long): Call<Void>

    /*
     * Reativa um produto que estava inativo.
     */
    @PUT("produtos/{id}/ativar")
    fun ativarProduto(@Path("id") id: Long): Call<Void>

    // =========================================================
    // VENDAS
    // =========================================================

    /*
     * Salva uma venda.
     */
    @POST("vendas")
    fun salvarVenda(@Body venda: Venda): Call<Venda>

    /*
     * Lista todas as vendas.
     */
    @GET("vendas")
    fun listarVendas(): Call<List<Venda>>

    /*
     * Busca uma venda pelo ID.
     */
    @GET("vendas/{id}")
    fun buscarVendasPorId(@Path("id") id: Long): Call<Venda>

    // =========================================================
    // USUÁRIOS
    // =========================================================

    /*
     * Lista usuários ativos.
     */
    @GET("usuarios")
    fun listarUsuarios(): Call<List<Usuario>>

    /*
     * Lista usuários inativos.
     */
    @GET("usuarios/inativos")
    fun listarUsuariosInativos(): Call<List<Usuario>>

    /*
     * Busca um usuário pelo ID.
     */
    @GET("usuarios/{id}")
    fun buscarUsuarioPorId(@Path("id") id: Long): Call<Usuario>

    /*
     * Salva um novo usuário.
     */
    @POST("usuarios")
    fun salvarUsuario(@Body usuario: Usuario): Call<Usuario>

    /*
     * Atualiza um usuário existente.
     */
    @PUT("usuarios/{id}")
    fun atualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Call<Usuario>

    /*
     * Inativa um usuário.
     */
    @PUT("usuarios/{id}/inativar")
    fun inativarUsuario(@Path("id") id: Long): Call<Void>

    /*
     * Reativa um usuário.
     */
    @PUT("usuarios/{id}/reativar")
    fun reativarUsuario(@Path("id") id: Long): Call<Void>

    /*
     * Faz login.
     */
    @POST("usuarios/login")
    fun login(@Body usuario: Usuario): Call<Usuario>

    // =========================================================
    // FORNECEDORES
    // =========================================================

    /*
     * Lista fornecedores.
     */
    @GET("fornecedores")
    fun listarFornecedores(): Call<List<Fornecedor>>

    /*
     * Busca fornecedor pelo ID.
     */
    @GET("fornecedores/{id}")
    fun buscarFornecedorPorId(@Path("id") id: Long): Call<Fornecedor>

    /*
     * Salva um novo fornecedor.
     */
    @POST("fornecedores")
    fun salvarFornecedor(@Body fornecedor: Fornecedor): Call<Fornecedor>

    /*
     * Atualiza um fornecedor existente.
     */
    @PUT("fornecedores/{id}")
    fun atualizarFornecedor(
        @Path("id") id: Long,
        @Body fornecedor: Fornecedor
    ): Call<Fornecedor>

    /*
     * Exclui fornecedor fisicamente.
     */
    @DELETE("fornecedores/{id}")
    fun deletarFornecedor(@Path("id") id: Long): Call<Void>

    // =========================================================
    // FECHAMENTO DE CAIXA
    // =========================================================

    @GET("fechamentos-caixa/historico")
    fun listarHistoricoFechamentosCaixa(): Call<List<HistoricoFechamentoCaixa>>
    /*
     * Envia os dados do fechamento para o back-end.
     */
    @POST("fechamentos-caixa")
    fun fecharCaixa(@Body fechamentoCaixa: FechamentoCaixa): Call<ResumoFechamentoCaixa>



}

