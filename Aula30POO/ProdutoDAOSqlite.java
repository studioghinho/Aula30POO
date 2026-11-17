package br.com.starlog.dao;

import br.com.starlog.model.Produto;
import br.com.starlog.util.ConexaoFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação concreta do ProdutoDAO usando SQLite via JDBC.
 */
public class ProdutoDAOSqlite implements ProdutoDAO {

    // DDL automático: cria a tabela caso ainda não exista
    public ProdutoDAOSqlite() {
        String ddl = "CREATE TABLE IF NOT EXISTS produtos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL, "
                + "preco REAL NOT NULL, "
                + "estoque INTEGER NOT NULL);";
        try (Connection conn = ConexaoFactory.obterConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela produtos: " + e.getMessage(), e);
        }
    }

    // CREATE
    @Override
    public void salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?);";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.executeUpdate();

            // Recupera o id autoincrement gerado e devolve para o objeto
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    produto.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    // READ (pontual)
    @Override
    public Optional<Produto> buscarPorId(Long id) {
        String sql = "SELECT id, nome, preco, estoque FROM produtos WHERE id = ?;";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinha(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // READ (em lote)
    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT id, nome, preco, estoque FROM produtos ORDER BY nome ASC;";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar catálogo: " + e.getMessage(), e);
        }
        return lista;
    }

    // UPDATE
    @Override
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, preco = ?, estoque = ? WHERE id = ?;";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.setLong(4, produto.getId());

            int afetadas = stmt.executeUpdate();
            return afetadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    // DELETE
    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?;";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int afetadas = stmt.executeUpdate();
            return afetadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    // Hidrata um Produto a partir da linha atual do ResultSet
    private Produto mapearLinha(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getDouble("preco"),
                rs.getInt("estoque"));
    }
}
