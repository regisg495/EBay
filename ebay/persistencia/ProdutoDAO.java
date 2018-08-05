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
import ebay.modelo.Produto;

public class ProdutoDAO extends AbstractDAO<Produto> {

	@Override
	public void save(Produto produto) {
		String sql;
		
		try (Connection connection = openConnection()) {
		
			if (produto.isTransient()) {
				sql = "INSERT INTO \"Produto\" (\"ID\", \"Nome\", \"Categoria\", \"Descricao\","
						+ " \"DateTimeCreation\", \"DateTimeLastUpdate\")"
						+ " VALUES (DEFAULT, ?, ?, ?, now(), now())";
			} else {
				sql = "UPDATE \"Produto\" SET \"Nome\" = ?, \"Categoria\" = ?, \"Descricao\" = ?,"
						+ " \"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (produto.getNome() == null) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, produto.getNome());
			}

			if (produto.getCategoria() == null) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, produto.getCategoria());
			}

			if (produto.getDescricao() == null) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, produto.getDescricao());
			}

			if (produto.isPersistent()) {
				stmt.setInt(4, produto.getId());
			}

			stmt.execute();
			
			if (produto.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();
				if (key.next()) {
					produto.setId(key.getInt(1));
				}
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Produto load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Produto\" WHERE \"ID\" = ?";

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
	public Produto find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Produto\" WHERE \"ID\" = ?";

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
	public List<Produto> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Produto\"";

			PreparedStatement stmt = connection.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Produto> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Produto\" LIMIT ? OFFSET ?";

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
	public void delete(Produto produto) throws EntityTransientException {
		if (!produto.isPersistent()) {
			throw new EntityTransientException();
		}

		try (Connection connection = openConnection()) {

			String sql = "DELETE FROM \"Produto\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, produto.getId());

			stmt.execute();

			produto.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Produto row(ResultSet result) throws SQLException {
		Produto p = new Produto(
			result.getInt("ID"),
			result.getString("Nome"),
			result.getString("Categoria"),
			result.getString("Descricao")
		);
		p.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		p.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return p;
	}

	private List<Produto> rows(ResultSet result) throws SQLException {
		List<Produto> produtos = new ArrayList<>();
		while (result.next()) {
			produtos.add(row(result));
		}
		return produtos;
	}
}
