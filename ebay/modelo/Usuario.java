package ebay.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ebay.exception.EmailJaRegistradoException;
import ebay.exception.EntityTransientException;
import ebay.exception.LoginJaRegistradoException;
import ebay.exception.NomeJaRegistradoException;
import ebay.exception.PublicacaoNaoExisteException;
import ebay.persistencia.IEntity;
import ebay.persistencia.UsuarioDAO;

public class Usuario implements IEntity{

	private Integer ID;
	private String 	nome;
	private String 	email;
	private String 	login;
	private String 	senha;
	private String 	endereco;
	 
	private List<Publicacao> publicacoes = new ArrayList<>();
	private List<Compra> 	 compras 	 = new ArrayList<>();
	private List<Avaliacao>  avaliacoes  = new ArrayList<>();

	private UsuarioDAO 		 user 		 = new UsuarioDAO();
	private List<Usuario>	 usuarios 	 = new ArrayList<>();
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;
	
	public Usuario(String nome, String email, String login, String senha, String endereco) {
		this.usuarios = user.loadAll();
		alterarNome(nome);
		alterarEmail(email);
		alterarLogin(login);
		this.senha = senha;
		this.endereco = endereco;
	}
	
	// Construtor para os SELECT's
	
	public Usuario(Integer id, String nome, String email, String login, String senha, String endereco) {
		this.ID = id;
		this.nome = nome;
		this.email = email;
		this.login = login;
		this.senha = senha;
		this.endereco = endereco;
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
		for(Usuario u : usuarios) {
			if(u.getNome().equals(nome)) {
				throw new NomeJaRegistradoException(nome);
			}
		}
		this.nome = nome;
	}
 
	public String getEmail() {
		return this.email;
	}
	
	public void alterarEmail(String email) {
		for(Usuario u : usuarios) {
			if(u.getEmail().equals(email)) {
				throw new EmailJaRegistradoException(email);
			}
		}
		this.email = email;
	}

	public String getLogin() {
		return this.login;
	}
	
	public void alterarLogin(String login) {
		for(Usuario u : usuarios) {
			if(u.getLogin().equals(login)) {
				throw new LoginJaRegistradoException(login);
			}
		}
		this.login = login;
	}
	
	public String getSenha() {
		return this.senha;
	}
	
	public String receberSenhaTemporaria() {
		String senhaTemporaria = "";
		for(int i = 0; i < 8; i++ ) {
			senhaTemporaria += (short) (int)(Math.random()*91 + 32);
		}
		this.senha = senhaTemporaria;
		return this.senha;
	}
	
	public void alterarSenha(String senha) {
		this.senha = senha;
	}
	
	public String getEndereco() {
		return this.endereco;
	}
	
	public void alterarEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	public void cancelarConta() throws EntityTransientException {
		user.delete(this);
	}
	
	public List<Avaliacao> getAvaliacoes(){
		return this.avaliacoes;
	}
	
	public List<Publicacao> getPublicacoes(){
		return this.publicacoes;
	}
	 
	public List<Compra> getCompras(){
		return this.compras;
	}
	
	public void avaliar(Publicacao publicacao, int pontos) {
		Avaliacao av = new Avaliacao(publicacao, this, pontos);
		this.avaliacoes.add(av);
	}
	
	public void avaliar(Publicacao publicacao, int pontos, String comentario) {
		Avaliacao av = new Avaliacao(publicacao, this, pontos, comentario);
		this.avaliacoes.add(av);
	}
	
	public void publicar(Publicacao publicacao){
		this.publicacoes.add(publicacao);
	}
	
	public void comprar(Publicacao publicacao, int quantidade) {
		if(publicacao.isTransient()) throw new PublicacaoNaoExisteException();
		Compra c = new Compra(publicacao, this, quantidade);
		this.compras.add(c);
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
		return "Usuario [ID = " + this.ID + ", nome = " + this.nome 
				+ ", email = " + this.email + ", login = " + this.login 
				+ ", endereco = " + this.endereco 
				+ ", dateTimeCreation = " + this.dateTimeCreation
				+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
	
	
}