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
import ebay.modelo.Avaliacao;
 
public class AvaliacaoDAO extends AbstractDAO<Avaliacao> {

	@Override
	public void save(Avaliacao avaliacao) {
		String sql;
		try (Connection connection = openConnection()) {
			if(avaliacao.isTransient()) {
				sql = "INSERT INTO \"Avaliacao\""
					+ " (\"ID\", \"Pontuacao\", \"Comentario\", \"IDPublicacao\", \"IDUsuario\","
					+ " \"DateTimeCreation\", \"DateTimeLastUpdate\")"
					+ " VALUES (DEFAULT, ?, ?, ?, ?, now(), now())";
			} else	{
				sql = "UPDATE \"Avaliacao\" SET \"Pontuacao\" = ?,"
						+ " \"Comentario\" = ?,"
						+ " \"IDPublicacao\" = ?,"
						+ " \"IDUsuario\" = ?,"
						+ " \"DateTimeLastUpdate\" = now() WHERE \"ID\" = ?";
			}
			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (avaliacao.getPontuacao() == null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, avaliacao.getPontuacao());
			}

			if (avaliacao.getComentario() == null) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, avaliacao.getComentario());
			}

			if (avaliacao.getPublicacao().getId() == null) {
				stmt.setNull(3, Types.INTEGER);
			} else {
				stmt.setInt(3, avaliacao.getPublicacao().getId());
			}

			if (avaliacao.getUsuario().getId() == null) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, avaliacao.getUsuario().getId());
			}

			if(avaliacao.isPersistent()) {
				stmt.setInt(5, avaliacao.getId());
			}
			
			stmt.execute();
			
			if(avaliacao.isTransient()) {
				ResultSet key = stmt.getGeneratedKeys();
	
				if (key.next()) {
					avaliacao.setId(key.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Avaliacao load(int id) throws EntityNotFoundException {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Avaliacao\" WHERE \"ID\" = ?";

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
	public Avaliacao find(int id) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Avaliacao\" WHERE \"ID\" = ?";

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
	public List<Avaliacao> loadAll() {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Avaliacao\"";

			PreparedStatement stmt = connection.prepareStatement(sql);

			ResultSet result = stmt.executeQuery();

			return rows(result);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Avaliacao> loadPage(int offset, int limit) {
		try (Connection connection = openConnection()) {

			String sql = "SELECT * FROM \"Avaliacao\" LIMIT ? OFFSET ?";

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
	public void delete(Avaliacao avaliacao) throws EntityTransientException {
		if (!avaliacao.isPersistent()) {
			throw new EntityTransientException();
		}
		
		try (Connection connection = openConnection()) {

			String sql = "DELETE FROM \"Avaliacao\" WHERE \"ID\" = ?";

			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, avaliacao.getId());

			stmt.execute();

			avaliacao.setId(null);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Avaliacao> getAvaliacoesUsuario(Integer id){
		try (Connection connection = openConnection()){
			
			String sql = "SELECT * FROM \"Avaliacao\" WHERE \"IDUsuario\" = ?";
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, id);
			
			ResultSet result = stmt.executeQuery();
			
			return rows(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Avaliacao> getAvaliacoesPublicacao(Integer id){
		try (Connection connection = openConnection()){
			
			String sql = "SELECT * FROM \"Avaliacao\" WHERE \"IDPublicacao\" = ?";
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setInt(1, id);
			
			ResultSet result = stmt.executeQuery();
			
			return rows(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Avaliacao row(ResultSet result) throws SQLException {
		Avaliacao a = new Avaliacao(
			result.getInt("ID"),
			result.getInt("Pontuacao"),
			result.getString("Comentario"),
			result.getInt("IDPublicacao"),
			result.getInt("IDUsuario")
		);
		a.setDateTimeCreation(result.getTimestamp("DateTimeCreation").toLocalDateTime());
		a.setDateTimeLastUpdate(result.getTimestamp("DateTimeLastUpdate").toLocalDateTime());
		return a;
	}

	private List<Avaliacao> rows(ResultSet result) throws SQLException {
		List<Avaliacao> avaliacoes = new ArrayList<>();
		while (result.next()) {
			avaliacoes.add(row(result));
		}
		return avaliacoes;
	}
}
