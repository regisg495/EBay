package ebay.modelo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ebay.exception.CompraJaRealizadaException;
import ebay.exception.ProdutoNaoEncontradoException;
import ebay.exception.PublicacaoNaoExisteException;
import ebay.persistencia.CompraDAO;
import ebay.persistencia.IEntity;
import ebay.persistencia.UsuarioDAO;

public class Compra implements IEntity{

	private Integer 		ID;
	private Usuario 		usuario;
	private LocalDateTime 	data;
	private Boolean			realizada;
	
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;

	private List<ItemVenda> itemVenda = new ArrayList<>();

	private CompraDAO compra = new CompraDAO();
	private UsuarioDAO user = new UsuarioDAO();
	
	public Compra(Publicacao publicacao, Usuario usuario, Integer quantidade) {
		this.usuario = usuario;
		this.data = LocalDateTime.now();
		ItemVenda iv = new ItemVenda(publicacao, quantidade);
		itemVenda.add(iv);
		this.realizada = false;
	}
	
	// Construtor para os SELECT's
	
	public Compra(Integer id, LocalDateTime data, Integer idUsuario) {
		this.ID = id;
		this.data = data;
		this.usuario = user.find(idUsuario);
	}
	
	public Integer getId() {
		return this.ID;
	}
	 
	public void setId(Integer id) {
		this.ID = id;
	}
	
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public List<ItemVenda> getItemVenda() {
		return this.itemVenda;
	}
	
	public LocalDateTime getData() {
		return this.data;
	}

	public Boolean isRealizada() {
		return realizada;
	}

	public void adicionarCompra(Publicacao publicacao, Integer quantidade) {
		if(publicacao.isTransient()) throw new PublicacaoNaoExisteException();
		check(this.realizada);
		for(int i = 0; i < this.itemVenda.size(); i++) {
			if(this.itemVenda.get(i).getPublicacao().getUsuario().getLogin() == publicacao.getUsuario().getLogin() 
			   && this.itemVenda.get(i).getPublicacao().getProduto().getNome() == publicacao.getProduto().getNome()) {
				this.itemVenda.get(i).alterarQuantidade(this.itemVenda.get(i).getQuantidade() + quantidade);
				return;
			}
		}
		ItemVenda iv = new ItemVenda(publicacao, quantidade);
		this.itemVenda.add(iv);
	}
	
	public void removerCompra(Publicacao publicacao) {
		if(publicacao.isTransient()) throw new PublicacaoNaoExisteException();
		check(this.realizada);
		for(int i = 0; i < this.itemVenda.size(); i++) {
			if(this.itemVenda.get(i).getPublicacao().getUsuario().getLogin() == publicacao.getUsuario().getLogin() 
			   && this.itemVenda.get(i).getPublicacao().getProduto().getNome() == publicacao.getProduto().getNome()) {
				this.itemVenda.remove(i);
				return;
			}
		}
		throw new ProdutoNaoEncontradoException();
	}
	
	public void realizar() throws SQLException {
		check(this.realizada);
		compra.save(this, itemVenda);
		this.realizada = true;
	}
	
	private void check(Boolean realizada) {
		if(realizada == true) throw new CompraJaRealizadaException();
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
		return "Compra [ID = " + this.ID + ", nome do usuario = " + this.usuario.getNome() 
			+ ", data da compra = " + this.data + ", dateTimeCreation = " + this.dateTimeCreation 
			+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
}