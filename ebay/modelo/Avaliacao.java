package ebay.modelo;

import java.time.LocalDateTime;
import java.util.List;

import ebay.exception.PontuacaoInvalidaException;
import ebay.exception.UsuarioJaAvaliouException;
import ebay.exception.UsuarioNaoPodeAvaliarSuasPublicacoesException;
import ebay.persistencia.AvaliacaoDAO;
import ebay.persistencia.IEntity;
import ebay.persistencia.PublicacaoDAO;
import ebay.persistencia.UsuarioDAO;

public class Avaliacao implements IEntity{

	private Integer 	ID;
	private Integer 	pontuacao;
	private String 		comentario;
	private Usuario 	usuario;
	private Publicacao 	publicacao;
	
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;

	private PublicacaoDAO publica 	= new PublicacaoDAO();
	private UsuarioDAO 	  user 		= new UsuarioDAO();

	private AvaliacaoDAO  avalia	= new AvaliacaoDAO();
	
	public Avaliacao(Publicacao publicacao, Usuario usuario, int pontuacao) {
		List<Avaliacao> av = avalia.getAvaliacoesPublicacao(publicacao.getId());
		for(Avaliacao a : av) {
			if(a.getUsuario().getLogin().equals(usuario.getLogin())) {
				throw new UsuarioJaAvaliouException();
			}
		}
		if(pontuacao <= 0 || pontuacao > 5) throw new PontuacaoInvalidaException();
		if(usuario.getLogin().equals(publicacao.getUsuario().getLogin())) throw new UsuarioNaoPodeAvaliarSuasPublicacoesException();
		this.pontuacao = pontuacao;
		this.publicacao = publicacao;
		this.usuario = usuario;
	}
	
	public Avaliacao(Publicacao publicacao, Usuario usuario, int pontuacao, String comentario) {
		this(publicacao, usuario, pontuacao);
		this.comentario = comentario;
	}
	
	// Construtor para os SELECT's
	
	public Avaliacao(Integer id, Integer pontuacao, String comentario, Integer idPublicacao, Integer idUsuario) {
		this.ID = id;
		this.pontuacao = pontuacao;
		this.comentario = comentario;
		this.publicacao = publica.find(idPublicacao);
		this.usuario = user.find(idUsuario);
	}

	public Integer getId() {
		return this.ID;
	}

	public void setId(Integer id) {
		this.ID = id;
	}
	
	public Integer getPontuacao() {
		return this.pontuacao;
	}
	
	public String getComentario() {
		return this.comentario;
	}
	
	public void alterarPontuacao(Integer pontuacao) {
		if(pontuacao <= 0 || pontuacao > 5) throw new PontuacaoInvalidaException();
		this.pontuacao = pontuacao;
	}
	
	public void alterarComentario(String comentario) {
		this.comentario = comentario;
	}
	
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public Publicacao getPublicacao() {
		return this.publicacao;
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
		return "Avaliacao [ID = " + this.ID + ", pontuacao = " + this.pontuacao 
				+ ", comentario = " + this.comentario + ", nome do usuario = " + this.usuario.getNome() 
				+ ", id da publicação = " + this.publicacao.getId() 
				+ ", dateTimeCreation = " + this.dateTimeCreation
				+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
}