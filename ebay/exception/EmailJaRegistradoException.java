package ebay.exception;

public class EmailJaRegistradoException extends RuntimeException {

	public EmailJaRegistradoException(String email) {
		super("O email: " + email + " ja está registrado.");
	}

}
