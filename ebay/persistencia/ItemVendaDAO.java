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
import ebay.modelo.ItemVenda;

public class ItemVendaDAO extends AbstractDAO<ItemVenda> {
	
	@Override
	public void save(ItemVenda itemVenda) {
		String sql;
		try (Connection connection = openConnection()) {
			if(itemVenda.isTransient()) {
				sql = "INSERT INTO \"ItemVenda\" (\"ID\", \"Quantidade\", \"IDCompra\","
						+ " \"IDPublicacao\", \"DateTimeCreation\", \"DateTimeLastUpdate\")"
						+ " VALUES (DEFAULT, ?, ?, ?, now(), now())";
			} else {
				sql = "UPDATE \"ItemVenda\" SET \"Quantidade\" = ?, \"IDCompra\" = ?,"
						+ " \"IDPublicacao\" = ?, \"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (itemVenda.getQuantidade() == null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, itemVenda.getQuantidade());
			}

			if (itemVenda.getCompra().getId() == null) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, itemVenda.getCompra().getId());
			}

			if (itemVenda.getPublicacao().getId() == null) {
				stmt.setNull(3, Types.INTEGER);
			} else {
				stmt.setInt(3, itemVenda.getPublicacao().getId());
			}

			if(itemVenda.isPersistent()) {
				stmt.setInt(4, itemVenda.getId());
			}
			
			stmt.execute();

			if(itemVenda.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();
	
				if (key.next()) {
					itemVenda.setId(key.getInt(1));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void save(ItemVenda itemVenda, Connection connection) {
		String sql;
		try {
			if(itemVenda.isTransient()) {
				sql = "INSERT INTO \"ItemVenda\" (\"ID\", \"Quantidade\", \"IDCompra\","
						+ " \"IDPublicacao\", \"DateTimeCreation\", \"DateTimeLastUpdate\")"
						+ " VALUES (DEFAULT, ?, ?, ?, now(), now())";
			} else {
				sql = "UPDATE \"ItemVenda\" SET \"Quantidade\" = ?, \"IDCompra\" = ?,"
						+ " \"IDPublicacao\" = ?, \"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (itemVenda.getQuantidade() == null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, itemVenda.getQuantidade());
			}

			if (itemVenda.getCompra().getId() == null) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, itemVenda.getCompra().getId());
			}

			if (itemVenda.getPublicacao().getId() == null) {
				stmt.setNull(3, Types.INTEGER);
			} else {
				stmt.setInt(3, itemVenda.getPublicacao().getId());
			}

			if(itemVenda.isPersistent()) {
				stmt.setInt(4, itemVenda.getId());
			}
			
			stmt.execute();

			if(itemVenda.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();
	
				if (key.next()) {
					itemVenda.setId(key.getInt(1));
				}
			}
			
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ItemVenda load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {
			
			String sql = "SELECT * FROM \"ItemVenda\" WHERE \"ID\" = ?";

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
	public ItemVenda find(int id) {
		try (Connection connection = openConnection()) {
			
			String sql = "SELECT * FROM \"ItemVenda\" WHERE \"ID\" = ?";

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
	public List<ItemVenda> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"ItemVenda\"";

			PreparedStatement stmt = connection.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ItemVenda> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"ItemVenda\" LIMIT ? OFFSET ?";

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
	public void delete(ItemVenda itemVenda) throws EntityTransientException {
		if (!itemVenda.isPersistent()) {
			throw new EntityTransientException();
		}
		try (Connection connection = openConnection()) {

			String sql = "DELETE FROM \"ItemVenda\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, itemVenda.getId());

			stmt.execute();

			itemVenda.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
	
	private ItemVenda row(ResultSet result) throws SQLException {
		ItemVenda iv = new ItemVenda(
			result.getInt("ID"),
			result.getInt("Quantidade"),
			result.getInt("IDCompra"),
			result.getInt("IDPublicacao")
		);
		iv.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		iv.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return iv;
	}

	private List<ItemVenda> rows(ResultSet result) throws SQLException {
		List<ItemVenda> itensVenda = new ArrayList<>();
		while (result.next()) {
			itensVenda.add(row(result));
		}
		return itensVenda;
	}
}