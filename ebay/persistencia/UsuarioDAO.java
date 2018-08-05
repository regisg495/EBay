package ebay.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ebay.exception.EntityNotFoundException;
import ebay.exception.EntityTransientException;
import ebay.modelo.Usuario;

public class UsuarioDAO extends AbstractDAO<Usuario> {

	@Override
	public void save(Usuario usuario) {
		String sql;
		try (Connection connection = openConnection()) {
			if (usuario.isTransient()) {
				sql = "INSERT INTO \"Usuario\""
						+ " (\"ID\", \"Nome\", \"Email\", \"Login\", \"Senha\", \"Endereco\","
						+ " \"DateTimeCreation\", \"DateTimeLastUpdate\") VALUES"
						+ " (DEFAULT, ?, ?, ?, ?, ?, now(), now())";
			} else {
				sql = "UPDATE \"Usuario\" SET "
						+ "\"Nome\" = ?, \"Email\" = ?, \"Login\" = ?, \"Senha\" = ?, \"Endereco\" = ?, "
						+ "\"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (usuario.getNome() == null) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, usuario.getNome());
			}

			if (usuario.getEmail() == null) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, usuario.getEmail());
			}

			if (usuario.getLogin() == null) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, usuario.getLogin());
			}

			if (usuario.getSenha() == null) {
				stmt.setNull(4, Types.VARCHAR);
			} else {
				stmt.setString(4, usuario.getSenha());
			}

			if (usuario.getEndereco() == null) {
				stmt.setNull(5, Types.VARCHAR);
			} else {
				stmt.setString(5, usuario.getEndereco());
			}

			if (usuario.isPersistent()) {
				stmt.setInt(6, usuario.getId());
			}

			stmt.execute();

			if (usuario.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();

				if (key.next()) {
					usuario.setId(key.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Usuario load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Usuario\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();

			if (result.next())
				return row(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		throw new EntityNotFoundException();
	}

	@Override
	public Usuario find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Usuario\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();

			if (result.next()) {
				return row(result);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	@Override
	public List<Usuario> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Usuario\"";

			PreparedStatement stmt = connection.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Usuario> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Usuario\" LIMIT ? OFFSET ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, limit);

			stmt.setInt(2, offset);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Usuario usuario) throws EntityTransientException {
		if (!usuario.isPersistent()) {
			throw new EntityTransientException();
		}

		try (Connection connection = openConnection()) {

			String sql = "DELETE FROM \"Usuario\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, usuario.getId());

			stmt.execute();

			usuario.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Usuario row(ResultSet result) throws SQLException {
		Usuario u = new Usuario(
			result.getInt("ID"), 
			result.getString("Nome"), 
			result.getString("Email"),
			result.getString("Login"), 
			result.getString("Senha"), 
			result.getString("Endereco")
		);
		u.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		u.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return u;
	}

	private List<Usuario> rows(ResultSet result) throws SQLException {
		List<Usuario> usuarios = new ArrayList<>();
		while (result.next()) {
			usuarios.add(row(result));
		}
		return usuarios;
	}
}