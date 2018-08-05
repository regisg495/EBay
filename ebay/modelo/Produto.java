package ebay.modelo;

import java.time.LocalDateTime;

import ebay.persistencia.IEntity;

public class Produto implements IEntity{
	
	private Integer ID;
	private String  nome;
	private String  descricao;
	private String  categoria;
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;
	
	public Produto(Integer id, String nome, String descricao, String categoria) {
		this.ID = id;
		this.nome = nome;
		this.descricao = descricao;
		this.categoria = categoria;
	}

	public Produto(String nome, String categoria) {
		this.nome = nome;
		this.categoria = categoria;
	}
	
	// Construtor para os SELECT's
	
	public Produto(String nome, String categoria, String descricao) {
		this(nome, descricao);
		this.descricao = descricao;
	}

	public Integer getId() {
		return this.ID;
	}

	public void setId(Integer id) {
		this.ID = id;
	}
	
	public String getNome() {
		return this.nome;
	}
	
	public void alterarNome(String nome) {
		this.nome = nome;
	}
	
	public String getDescricao() {
		return this.descricao;
	}
	
	public void alterarDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getCategoria() {
		return this.categoria;
	}
	
	public void alterarCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public void setDateTimeCreation(LocalDateTime data) {
		this.dateTimeCreation = data;
	}
	
	public void setDateTimeLastUpdate(LocalDateTime data) {
		this.dateTimeLastUpdate = data;
	}
	
	@Override
	public LocalDateTime getDateTimeCreation() {
		return this.dateTimeCreation;
	}

	@Override
	public LocalDateTime getDateTimeLastUpdate() {
		return this.dateTimeLastUpdate;
	}

	@Override
	public String toString() {
		return "Produto [ID = " + this.ID + ", nome = " + this.nome 
				+ ", descricao = " + this.descricao + ", categoria = " + this.categoria
				+ ", dateTimeCreation = " + this.dateTimeCreation 
				+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
}