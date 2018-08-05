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
import ebay.modelo.Publicacao;

public class PublicacaoDAO extends AbstractDAO<Publicacao> {
	
	@Override
	public void save(Publicacao publicacao) {
		String sql;
		try (Connection connection = openConnection()) {
			if(publicacao.isTransient()) {
				sql = "INSERT INTO \"Publicacao\""
						+ " (\"ID\", \"Valor\", \"Quantidade\", \"Descricao\""
						+ ", \"Data\", \"IDUsuario\", \"IDProduto\", "
						+ "\"DateTimeCreation\", \"DateTimeLastUpdate\") VALUES" 
						+ " (DEFAULT, ?, ?, ?, DEFAULT, ?, ?, now(), now())";
			} else {
				sql = "UPDATE \"Publicacao\" SET \"Valor\" = ?, \"Quantidade\" = ?,"
					+ " \"Descricao\" = ?, \"Data\" = now(), \"IDUsuario\" = ?,"
					+ " \"IDProduto\" = ?, \"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (publicacao.getValor().toDouble() == null) {
				stmt.setNull(1, Types.NUMERIC);
			} else {
				stmt.setDouble(1, publicacao.getValor().toDouble());
			}

			if (publicacao.getQuantidade() == null) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, publicacao.getQuantidade());
			}

			if (publicacao.getDescricao() == null) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, publicacao.getDescricao());
			}

			if (publicacao.getUsuario().getId() == null) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, publicacao.getUsuario().getId());
			}
 
			if (publicacao.getProduto().getId() == null) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, publicacao.getProduto().getId());
			}
			
			if(publicacao.isPersistent()) {
				stmt.setInt(6, publicacao.getId());
			}
			
			stmt.execute();
			if(publicacao.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();
	
				if (key.next()) {
					publicacao.setId(key.getInt(1));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Publicacao load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Publicacao\" WHERE \"ID\" = ?";

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
	public Publicacao find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Publicacao\" WHERE \"ID\" = ?";

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
	public List<Publicacao> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Publicacao\"";

			PreparedStatement stmt = connection.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Publicacao> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Publicacao\" LIMIT ? OFFSET ?";

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
	public void delete(Publicacao publicacao) throws EntityTransientException {
		if (!publicacao.isPersistent()) {
			throw new EntityTransientException();
		}

		try (Connection connection = openConnection()) {

			String sql = "DELETE FROM \"Publicacao\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, publicacao.getId());

			stmt.execute();

			publicacao.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Publicacao> loadPublicacoes(Integer id){
		try (Connection connection = openConnection()){
			String sql = "SELECT * FROM \"Publicacao\" WHERE \"IDUsuario\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet result = stmt.executeQuery();

			return rows(result);
			
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Publicacao row(ResultSet result) throws SQLException {
		Publicacao p = new Publicacao(
			result.getInt("ID"),
			result.getInt("IDUsuario"),
			result.getDouble("Valor"),
			result.getInt("Quantidade"),
			result.getString("Descricao"),
			result.getInt("IDProduto"),
			result.getTimestamp("Data").toLocalDateTime()
		);
		p.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		p.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return p;
	}

	private List<Publicacao> rows(ResultSet result) throws SQLException {
		List<Publicacao> publicacoes = new ArrayList<>();
		while (result.next()) {
			publicacoes.add(row(result));
		}
		return publicacoes;
	}
}