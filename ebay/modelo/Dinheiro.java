package ebay.modelo;

import ebay.exception.DinheiroNaoPermitidoException;

public class Dinheiro {

	private int centavos;
	
	public Dinheiro(int real) {
		if(real < 0) throw new DinheiroNaoPermitidoException(real);
		this.centavos = real * 100;
	}
	
	public Dinheiro(int real, int centavo) {
		this(real);
		if(centavo < 0) throw new DinheiroNaoPermitidoException(centavo);
		this.centavos += centavo;
	}
	
	public int getReal() {
		return this.centavos / 100;
	}
	
	public int getCentavos() {
		return this.centavos % 100;
	}
	
	public Double toDouble() {
		return (double) this.centavos/100;
	}
	
	@Override
	public String toString() {
		return "R$ " + String.format("%,d", getReal()) + (getCentavos() < 10 ? ",0" : ",") + this.getCentavos();
	}
}
