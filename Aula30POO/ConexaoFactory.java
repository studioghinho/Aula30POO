package br.com.starlog.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões com o SQLite (arquivo starlog.db na raiz do projeto).
 * Requer o driver sqlite-jdbc (org.xerial) no classpath.
 */
public class ConexaoFactory {

    private static final String URL = "jdbc:sqlite:starlog.db";

    private ConexaoFactory() { }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
