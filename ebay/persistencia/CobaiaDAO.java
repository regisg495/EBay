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
import ebay.modelo.Cobaia;

public class CobaiaDAO extends AbstractDAO<Cobaia> {

	@Override
	public void save(Cobaia cobaia) {
		String sql;
		try (Connection connection = openConnection()) {
			if (cobaia.isTransient()) {
				sql = "INSERT INTO \"Cobaia\" (\"ID\", \"Nome\", \"DateTimeCreation\", \"DateTimeLastUpdate\") "
						+ "VALUES (DEFAULT, ?, NOW(), NOW())";
			} else {
				sql = "UPDATE \"Cobaia\" SET \"Nome\" = ?, \"DateTimeLastUpdate\" = NOW() WHERE \"ID\" = ?";
			}

			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (cobaia.getNome() == null) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, cobaia.getNome());
			}

			if (cobaia.isPersistent()) {
				stmt.setInt(2, cobaia.getId());
			}

			stmt.execute();

			if (cobaia.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();

				if (key.next()) {
					cobaia.setId(key.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Cobaia load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Cobaia\" WHERE \"ID\" = ?";

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
	public Cobaia find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Cobaia\" WHERE \"ID\" = ?";

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
	public List<Cobaia> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Cobaia\"";

			Statement stmt = connection.createStatement();

			ResultSet result = stmt.executeQuery(sql);

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Cobaia> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Cobaia\" LIMIT ? OFFSET ?";

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
	public void delete(Cobaia cobaia) throws EntityTransientException {
		if (!cobaia.isPersistent()) {
			throw new EntityTransientException();
		}
		try (Connection connection = openConnection()) {
			String sql = "DELETE FROM \"Cobaia\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, cobaia.getId());

			stmt.execute();

			cobaia.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Cobaia row(ResultSet result) throws SQLException {
		Cobaia c = new Cobaia();
		c.setId(result.getInt("ID"));
		c.setNome(result.getString("Nome"));
		c.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		c.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return c;
	}
	
	private List<Cobaia> rows(ResultSet result) throws SQLException {
		List<Cobaia> cobaias = new ArrayList<>();
		while (result.next()) {
			cobaias.add(row(result));
		}
		return cobaias;
	}
}