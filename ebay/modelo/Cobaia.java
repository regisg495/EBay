package ebay.modelo;

import java.time.LocalDateTime;

import ebay.persistencia.IEntity;

public class Cobaia implements IEntity{

	private String  nome;
	private Integer id;
	private LocalDateTime dateTimeCreation;
	private LocalDateTime dateTimeLastUpdate;
	
	public Cobaia() {}

	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
		return "Cobaia [nome = " + this.nome + ", id = " + this.id 
				+ ", dateTimeCreation = " + this.dateTimeCreation
				+ ", dateTimeLastUpdate = " + this.dateTimeLastUpdate + "]";
	}

}