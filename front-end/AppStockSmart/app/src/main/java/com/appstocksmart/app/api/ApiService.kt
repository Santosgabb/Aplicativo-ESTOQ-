package com.appstocksmart.app.api

import com.appstocksmart.app.model.Fornecedor
import com.appstocksmart.app.model.Produto
import com.appstocksmart.app.model.Usuario
import com.appstocksmart.app.model.Venda
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // Produtos
    @GET("produtos")
    fun listarProdutos(): Call<List<Produto>>

    @GET("produtos/{id}")
    fun buscarProdutoPorId(@Path("id") id: Long): Call<Produto>

    @POST("produtos")
    fun salvarProduto(@Body produto: Produto): Call<Produto>

    @PUT("produtos/{id}")
    fun atualizarProduto(
        @Path("id") id: Long,
        @Body produto: Produto
    ): Call<Produto>

    @DELETE("produtos/{id}")
    fun deletarProduto(@Path("id") id: Long): Call<Void>

    // Vendas
    @POST("vendas")
    fun salvarVenda(@Body venda: Venda): Call<Venda>

    @GET("vendas")
    fun listarVendas(): Call<List<Venda>>

    @GET("vendas/{id}")
    fun buscarVendasPorId(@Path("id") id: Long): Call<Venda>

    // Usuários
    @GET("usuarios")
    fun listarUsuarios(): Call<List<Usuario>>

    @GET("usuarios/{id}")
    fun buscarUsuarioPorId(@Path("id") id: Long): Call<Usuario>

    @POST("usuarios")
    fun salvarUsuario(@Body usuario: Usuario): Call<Usuario>

    @PUT("usuarios/{id}")
    fun atualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Call<Usuario>

    @DELETE("usuarios/{id}")
    fun deletarUsuario(@Path("id") id: Long): Call<Void>


    @POST("usuarios/login")
    fun login(@Body usuario: Usuario): Call<Usuario>

    //
    @GET("fornecedores")
    fun listarFornecedores(): Call<List<Fornecedor>>

    @GET("fornecedores/{id}")
    fun buscarFornecedorPorId(@Path("id") id: Long): Call<Fornecedor>

    @POST("fornecedores")
    fun salvarFornecedor(@Body fornecedor: Fornecedor): Call<Fornecedor>

    @PUT("fornecedores/{id}")
    fun atualizarFornecedor(
        @Path("id") id: Long,
        @Body fornecedor: Fornecedor
    ): Call<Fornecedor>

    @DELETE("fornecedores/{id}")
    fun deletarFornecedor(@Path("id") id: Long): Call<Void>


    }
