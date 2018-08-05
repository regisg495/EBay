package ebay.exception;

public class LoginJaRegistradoException extends RuntimeException {

	public LoginJaRegistradoException(String login) {
		super("O login: " + login + " ja está em uso.");
	}

}
