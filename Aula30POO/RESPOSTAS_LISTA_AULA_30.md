# Aula 30 – Prática DAO Completa & CRUD de Negócio

## Parte 1 – Questões de múltipla escolha

| Questão | Resposta | Por quê |
|---|---|---|
| 1 – Origem do CRUD | **A** | James Martin (1983); Create/INSERT, Read/SELECT, Update/UPDATE, Delete/DELETE. |
| 2 – Retorno de `executeUpdate()` | **C** | O `int` diz quantas linhas foram afetadas; `> 0` confirma que o registro existia. |
| 3 – DELETE sem WHERE | **D** | Sem filtro, todas as linhas da tabela `pecas` são excluídas. |
| 4 – Fluxo MVC | **B** | View → Controller (valida e cria a entidade) → DAO (JDBC) → View recarrega. |

## Parte 2 – Desafios de código (colar nas caixas da página)

### Desafio 1 – `atualizar(Produto produto)`

```java
@Override
public boolean atualizar(Produto produto) {
    String sql = "UPDATE produtos SET nome = ?, preco = ?, estoque = ? WHERE id = ?;";
    try (Connection conn = ConexaoFactory.obterConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, produto.getNome());
        stmt.setDouble(2, produto.getPreco());
        stmt.setInt(3, produto.getEstoque());
        stmt.setLong(4, produto.getId());

        int linhasAfetadas = stmt.executeUpdate();

        return linhasAfetadas > 0;

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
    }
}
```

### Desafio 2 – `excluir(Long id)`

```java
@Override
public boolean excluir(Long id) {
    String sql = "DELETE FROM produtos WHERE id = ?;";
    try (Connection conn = ConexaoFactory.obterConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setLong(1, id);

        int linhasAfetadas = stmt.executeUpdate();

        return linhasAfetadas > 0;

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
    }
}
```

### Desafio 3 – `listarTodos()`

```java
@Override
public List<Produto> listarTodos() {
    String sql = "SELECT id, nome, preco, estoque FROM produtos ORDER BY nome;";
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
```

## Parte 3 – Projeto oficial (Almoxarifado StarLog)

Código completo na pasta `starlog/src/br/com/starlog/`:

- `model/Produto.java` – entidade com validações (`IllegalArgumentException`)
- `dao/ProdutoDAO.java` – interface com as 5 assinaturas
- `dao/ProdutoDAOSqlite.java` – DDL no construtor, `afetadas > 0`, `ORDER BY nome ASC`
- `util/ConexaoFactory.java` – conexão `jdbc:sqlite:starlog.db`
- `Main.java` – ciclo Create → Read → Update → Delete (incluindo ID 888) → estado final

### Como executar

Precisa do driver `sqlite-jdbc` (org.xerial) no classpath:

```bash
javac -encoding UTF-8 -cp sqlite-jdbc.jar -d out $(find src -name "*.java")
java -cp "out:sqlite-jdbc.jar" br.com.starlog.Main      # Linux/Mac
java -cp "out;sqlite-jdbc.jar" br.com.starlog.Main      # Windows
```

Na IDE (IntelliJ/Eclipse/VS Code), basta adicionar o `.jar` às bibliotecas do projeto.
