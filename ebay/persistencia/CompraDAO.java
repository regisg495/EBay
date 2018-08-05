package ebay.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ebay.exception.EntityNotFoundException;
import ebay.exception.EntityTransientException;
import ebay.modelo.Compra;
import ebay.modelo.ItemVenda;

public class CompraDAO extends AbstractDAO<Compra> {
	
	private ItemVendaDAO itemVendaDao = new ItemVendaDAO();
	
	@Override
	public void save(Compra compra) {
		String sql;
		try (Connection connection = openConnection()) {
			if (compra.isTransient()) {
				sql = "INSERT INTO \"Compra\" " 
					+ "(\"ID\", \"Data\", \"IDUsuario\", \"DateTimeCreation\", \"DateTimeLastUpdate\")"
					+ " VALUES (DEFAULT, DEFAULT, ?, now(), now())";
			} else {
				sql = "UPDATE \"Compra\" SET \"Data\" = now(), \"IDUsuario\" = ?, " 
					+ "\"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			} 
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (compra.getUsuario().getId() == null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, compra.getUsuario().getId());
			}

			if (compra.isPersistent()) {
				stmt.setInt(2, compra.getId());
			}

			stmt.execute();

			if (compra.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();

				if (key.next()) {
					compra.setId(key.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void save(Compra compra, List<ItemVenda> itensVenda) throws SQLException {
		String sql;
		Connection connection = null;
		try {
			connection = openConnection();
			
			connection.setAutoCommit(false);
			
			if (compra.isTransient()) {
				sql = "INSERT INTO \"Compra\" " 
					+ "(\"ID\", \"Data\", \"IDUsuario\", \"DateTimeCreation\", \"DateTimeLastUpdate\")"
					+ " VALUES (DEFAULT, DEFAULT, ?, now(), now())";
			} else {
				sql = "UPDATE \"Compra\" SET \"Data\" = now(), \"IDUsuario\" = ?, " 
					+ "\"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			} 
			
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (compra.getUsuario().getId() == null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, compra.getUsuario().getId());
			}

			if (compra.isPersistent()) {
				stmt.setInt(2, compra.getId());
			}

			stmt.execute();

			if (compra.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();

				if (key.next()) {
					compra.setId(key.getInt(1));
				}
			}
			
			for(ItemVenda i : itensVenda) {
				ItemVenda iv = new ItemVenda(i.getPublicacao(), i.getQuantidade(), compra);
				itemVendaDao.save(iv, connection);
			}
			
			connection.commit();
			
			connection.close();
		} catch (SQLException e) {
			if(connection != null) {
				connection.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	public Compra load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Compra\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();

			if (result.next()) {
				return row(result);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		throw new EntityNotFoundException();

	}

	@Override
	public Compra find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Compra\" WHERE \"ID\" = ?";

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
	public List<Compra> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Compra\"";

			PreparedStatement stmt = connection.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Compra> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Compra\" LIMIT ? OFFSET ?";

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
	public void delete(Compra compra) throws EntityTransientException {
		if (compra.isTransient()) {
			throw new EntityTransientException();
		}
		try (Connection connection = openConnection()) {

			String sql = "DELETE FROM \"Compra\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, compra.getId());

			stmt.execute();

			compra.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Compra> loadCompras(Integer id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Compra\" WHERE \"IDUsuario\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private Compra row(ResultSet result) throws SQLException {
		Compra c = new Compra(
			result.getInt("ID"), 
			result.getTimestamp("Data").toLocalDateTime(),
			result.getInt("IDUsuario")
		);
		c.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		c.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return c;
	}

	private List<Compra> rows(ResultSet result) throws SQLException {
		List<Compra> compras = new ArrayList<>();
		while (result.next()) {
			compras.add(row(result));
		}
		return compras;
	}
}