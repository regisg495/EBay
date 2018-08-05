package ebay.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ebay.exception.PublicacaoExcedeuONumeroDeFotosException;
import ebay.exception.PublicacaoSemFotoException;
import ebay.persistencia.AvaliacaoDAO;
import ebay.persistencia.FotoDAO;
import ebay.persistencia.IEntity;
import ebay.persistencia.ProdutoDAO;
import ebay.persistencia.UsuarioDAO;

public class Publicacao implements IEntity{
	
	private Integer 		ID;
	private Dinheiro 		valor; 
	private Integer 		quantidade;
	private String 			descricao;
	private LocalDateTime 	data;
	private Usuario 		usuario;
	private Produto 		produto;
	
	private List<Avaliacao> avaliacoes 		= new ArrayList<>();
	private List<Foto> 		fotos 			= new ArrayList<>();  

	private AvaliacaoDAO 	avaliacao 		= new AvaliacaoDAO();
	private FotoDAO 		foto 			= new FotoDAO();
	private UsuarioDAO 		user 			= new UsuarioDAO();
	private ProdutoDAO 		prod 			= new ProdutoDAO();
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;
	
	public Publicacao(Usuario usuario, Dinheiro valor, int quantidade, String descricao, Produto produto, List<Foto> fotos) {
		if(fotos.size() == 0 || fotos == null) throw new PublicacaoSemFotoException();
		if(fotos.size() > 12) throw new PublicacaoExcedeuONumeroDeFotosException();
		this.usuario = usuario;
		this.valor = valor;
		this.quantidade = quantidade;
		this.produto = produto;
		this.descricao = descricao;
		for(int i = 0; i < fotos.size(); i++) {
			Foto foto = new Foto(this, fotos.get(i).getFoto());
			this.fotos.add(foto);
		}
		this.data = LocalDateTime.now();
	}
	
	// Construtor para os SELECT's
	
	public Publicacao(Integer id, Integer idUsuario, double valor, int quantidade, String descricao, Integer idProduto, LocalDateTime data) {
		this.ID = id;
		this.usuario = user.find(idUsuario);
		this.valor = new Dinheiro((int)valor, (int)(valor*100%100));
		this.quantidade = quantidade;
		this.descricao = descricao;
		this.produto = prod.find(idProduto);
		this.data = data;
	}

	public Integer getId() {
		return this.ID;
	}
	
	public void setId(Integer id) {
		this.ID = id;
	}
	
	public Dinheiro getValor() {
		return this.valor;
	}
	
	public void ajustarValor(Dinheiro valor) {
		this.valor = valor;
	}
	
	public Integer getQuantidade() {
		return this.quantidade;
	}
	
	public void alterarQuantidade(int quantidade) {
		this.quantidade = quantidade;
//		this.data = LocalDateTime.now();
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void alterarDescricao(String descricao) {
		this.descricao = descricao;
//		this.data = LocalDateTime.now();
	}
	
	public LocalDateTime getData() {
		return this.data;
	}
	
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public Produto getProduto() {
		return this.produto;
	}

	public List<Avaliacao> getAvaliacoes(){
		this.avaliacoes = avaliacao.getAvaliacoesPublicacao(this.ID);
		return this.avaliacoes;
	}
	
	public List<Foto> getFotos(){
		if(this.fotos == null) {
			this.fotos = foto.loadFotos(this.ID);
		}
		return this.fotos;
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
		return "Publicacao [ID = " + this.ID + ", valor = " + this.valor.toString() 
			+ ", quantidade = " + this.quantidade + ", descricao = " + this.descricao
				+ ", data da publicação = " + this.data + ", nome do dono =" + this.usuario.getNome() 
				+ ", produto = " + this.produto + ", avaliacoes = " + this.avaliacoes
				+ ", dateTimeCreation = " + this.dateTimeCreation 
				+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
	
	
}