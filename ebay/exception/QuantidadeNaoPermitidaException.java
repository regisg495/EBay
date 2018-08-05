package ebay.exception;

public class QuantidadeNaoPermitidaException extends RuntimeException {

	public QuantidadeNaoPermitidaException(int quantidade) {
		super("Quantidade não pode ser inferior a zero. Valor inserido: " + quantidade);
	}

}
