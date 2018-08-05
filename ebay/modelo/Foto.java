package ebay.modelo;

import java.time.LocalDateTime;

import ebay.persistencia.IEntity;
import ebay.persistencia.PublicacaoDAO;

public class Foto implements IEntity{
	
	private Integer 	ID;
	private byte[] 		foto;
	private Publicacao  publicacao;
	
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;

	private PublicacaoDAO publica = new PublicacaoDAO();
	
	public Foto(byte[] foto) {
		this.foto = foto;
	}
	
	public Foto(Publicacao publicacao, byte[] foto) {
		this.publicacao = publicacao;
		this.foto = foto;
	}
	
	// Construtor para os SELECT's
	
	public Foto(Integer id, Integer idPublicacao, byte[] foto, LocalDateTime dataCreation, LocalDateTime dataUpdate) {
		this.ID = id;
		this.publicacao = publica.find(idPublicacao);
		this.foto = foto;
		this.dateTimeCreation = dataCreation;
		this.dateTimeLastUpdate = dataUpdate;
	}
	
	public Integer getId() {
		return this.ID;
	}
	
	public void setId(Integer id) {
		this.ID = id;
	}
	
	public byte[] getFoto() {
		return this.foto;
	}
	
	public void alterarFoto(byte[] foto) {
		this.foto = foto;
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
		return "Foto [ ID = " + this.ID + ", id da publicacao = " + this.publicacao 
				+ ", dateTimeCreation = " + this.dateTimeCreation 
				+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}
}