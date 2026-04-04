package com.appstocksmart.app.model

/*
 * Model que representa um usuário no app.
 *
 * É usado para:
 * - cadastro
 * - edição
 * - login
 * - exibição de detalhes
 */
data class Usuario(

    /*
     * ID do usuário no banco.
     * Pode vir nulo antes de salvar.
     */
    val id: Long? = null,

    /*
     * Nome completo do usuário.
     */
    var nome: String,

    /*
     * Login usado para entrar no sistema.
     */
    var login: String,

    /*
     * Senha do usuário.
     */
    var senha: String,

    /*
     * Perfil do usuário.
     * Exemplo: GERENTE ou CAIXA.
     */
    var perfil: String,

    /*
     * Indica se o usuário está ativo no sistema.
     * true = ativo
     * false = inativo
     */
    var ativo: Boolean? = true
)