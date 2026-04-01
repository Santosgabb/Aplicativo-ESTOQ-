package com.app.model;

public enum TipoProduto {

	// cada valor tem sua descricao
	ALIMENTO("Alimento"), BEBIDA("Bebida"), CACHAÇA("Cachaça"), CIGARRO("Cigarro"), DOCE("Doce"), TESTE("TESTE"),
	OUTROS("OUTROS"), LIMPEZA("Limpeza");

	// descricao do tipo
	private final String descricao;

	// construtor do enum
	TipoProduto(String descricao) {
		this.descricao = descricao;
	}

	// getters
	public String getDescricao() {
		return descricao;
	}
}
