package ebay.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException; 

public abstract class AbstractDAO <T extends IEntity> implements IDAO<T> {

	private final String url = "jdbc:postgresql://localhost/ebay";
	private final String username = "postgres";
	private final String password = "ijc4jrs90";
	
	private Connection connection; 
	
	public Connection openConnection() {
		try {
			connection = DriverManager.getConnection(url, username, password);
			return connection;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}	
