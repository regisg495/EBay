package ebay.modelo;

import java.time.LocalDateTime;

import ebay.exception.QuantidadeNaoPermitidaException;
import ebay.persistencia.CompraDAO;
import ebay.persistencia.IEntity;
import ebay.persistencia.PublicacaoDAO;

public class ItemVenda implements IEntity{

	private Integer 	ID;
	private Publicacao  publicacao;
	private Integer     quantidade;
	private Compra 	    compra;
	
	private CompraDAO 		com 	= new CompraDAO();
	private PublicacaoDAO   publica = new PublicacaoDAO();
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;
	
	// Construtor para compra
	
	public ItemVenda(Publicacao publicacao, int quantidade) {
		this.publicacao = publicacao;
		this.quantidade = quantidade;
	}
	
	// Construtor para insert no banco
	
	public ItemVenda(Publicacao publicacao, int quantidade, Compra compra) {
		this(publicacao, quantidade);
		this.compra = compra;
	}
	
	// Construtor para os SELECT's
	
	public ItemVenda(Integer id, int quantidade, Integer idCompra, Integer idPublicacao) {
		this.ID = id;
		this.quantidade = quantidade;
		this.compra = com.find(idCompra);
		this.publicacao = publica.find(idPublicacao);
	}
	
	public Integer getId() {
		return this.ID;
	}
	
	public void setId(Integer id) {
		this.ID = id;
	}
	
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void alterarQuantidade(int quantidade) {
		if(quantidade - this.quantidade < 0 ) throw new QuantidadeNaoPermitidaException(quantidade - this.quantidade);
		this.quantidade = quantidade;
	}

	public Publicacao getPublicacao() {
		return this.publicacao;
	}
	
	public Compra getCompra() {
		return this.compra;
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
		return "ItemVenda [ID = " + this.ID + ", id da publicacao = " + this.publicacao.getId() 
		+ ", quantidade = " + this.quantidade + ", compra = " + this.compra.getId() 
		+ ", dateTimeCreation = " + this.dateTimeCreation
		+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
	
	
}