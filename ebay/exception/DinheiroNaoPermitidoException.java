package ebay.exception;

public class DinheiroNaoPermitidoException extends RuntimeException {

	public DinheiroNaoPermitidoException(int real) {
		super("Valor digitado não pode ser negativo, você digitou: " + real);
	}

}
