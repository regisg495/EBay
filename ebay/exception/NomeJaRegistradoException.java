package ebay.exception;

public class NomeJaRegistradoException extends RuntimeException {

	public NomeJaRegistradoException(String nome) {
		super("O nome: " + nome + " já está registrado.");
	}

}
