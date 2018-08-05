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
import ebay.modelo.Foto;

public class FotoDAO extends AbstractDAO<Foto> {

	@Override
	public void save(Foto foto) {
		String sql;
		try (Connection connection = openConnection()) {
			if(foto.isTransient()) {
				sql = "INSERT INTO \"Foto\" (\"ID\", \"Foto\", \"IDPublicacao\", \"DateTimeCreation\","
						+ " \"DateTimeLastUpdate\") "
					+ "VALUES (DEFAULT, ?, ?, now(), now())";
			}
		
			else {
				sql = "UPDATE \"Foto\" SET \"Foto\" = ?, \"IDPublicaca\" = ?,"
						+ " \"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (foto.getFoto() == null) {
				stmt.setNull(1, Types.BINARY);
			} else {
				stmt.setBytes(1, foto.getFoto());
			}

			if (foto.getPublicacao().getId() == null) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, foto.getPublicacao().getId());
			}
			
			if (foto.getId() != null) {
				stmt.setInt(3, foto.getId());
			}

			stmt.execute();

			if(foto.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();
	
				if (key.next()) {
					foto.setId(key.getInt(1));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Foto load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Foto\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();
	
			if (result.next()) return row(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		throw new EntityNotFoundException();
	}

	@Override
	public Foto find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Foto\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();
	
			if (result.next()) return row(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

	@Override
	public List<Foto> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Foto\"";

			Statement stmt = connection.createStatement();

			ResultSet result = stmt.executeQuery(sql);

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Foto> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Foto\" LIMIT ? OFFSET ?";

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
	public void delete(Foto foto) throws EntityTransientException {
		if (!foto.isPersistent()) {
			throw new EntityTransientException();
		}
		try (Connection connection = openConnection()) {
			String sql = "DELETE FROM \"Foto\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, foto.getId());

			stmt.execute();

			foto.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private Foto row(ResultSet result) throws SQLException {
		Foto f = new Foto(
			result.getInt("ID"),
			result.getInt("IDPublicacao"),
			result.getBytes("Foto"),
			result.getTimestamp("DateTimeCreation").toLocalDateTime(),
			result.getTimestamp("DateTimeLastUpdate").toLocalDateTime()
		);
		return f;
	}
	
	private List<Foto> rows(ResultSet result) throws SQLException {
		List<Foto> fotos = new ArrayList<>();
		while (result.next()) {
			fotos.add(row(result));
		}
		return fotos;
	}

	public List<Foto> loadFotos(Integer id) {
		try (Connection connection = openConnection()) {

			String sql ="SELECT f.\"DateTimeCreation\", f.\"DateTimeLastUpdate\", "
			+ "f.\"ID\", f.\"Foto\", f.\"IDPublicacao\" FROM \"Publicacao\" AS p"
			+ " INNER JOIN \"Foto\" AS f ON f.\"IDPublicacao\" = p.\"ID\" WHERE p.\"ID\" = ?";
			
			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}