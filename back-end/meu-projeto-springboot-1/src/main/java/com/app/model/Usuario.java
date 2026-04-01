package com.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity // fala para o JPA que essa classe sera uma tabela no Banco
@Table(name = "usuario") // nome da tabela no Banco, nao e preciso colocar pois usa o nome da classe no
							// banco
public class Usuario {

	// Chave primaria e auto incremento
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Id autoincremento
	private Long id;

	/*
	 * NotEmpty nao aceita String vazia mas aceita ela com " " foi utilizado por
	 * causa de suas caracteristicas
	 */

	// nome do usuario
	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	// login usuario
	@NotBlank(message = "Login é obrigatório")
	private String login;

	// senha
	@NotBlank(message = "Senha é obrigatório")
	private String senha;

	/*
	 * Enum salvo como texto GERENTE / CAIXA
	 */
	@Enumerated(EnumType.STRING) //
	@NotNull(message = "Perfil é obrigatório")
	private PerfilUsuario perfil;

	// SobreCarga de Contrutores
	public Usuario() {

	}

	public Usuario(String nome, String login, String senha, PerfilUsuario pf) {
		this.nome = nome;
		this.login = login;
		this.senha = senha;
		this.perfil = pf;
	}

	// Getters e Setters dos Atributos
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public PerfilUsuario getPerfil() {
		return perfil;
	}

	public String getLogin() {
		return login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPerfil(PerfilUsuario perfil) {
		this.perfil = perfil;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}