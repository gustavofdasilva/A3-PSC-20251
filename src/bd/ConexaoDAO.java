package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utils.Log;

public class ConexaoDAO {
    private String url = "jdbc:mysql://localhost:3306/db_a3";
    private String user = "root";
    private String pass = "root";
    
    private Connection conn;

    public Connection conectar() {
        try {
            conn = DriverManager.getConnection(url, user, pass);
            Log.info("Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            Log.error("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return conn;
    }

    public void fecharConexao() {
        if (conn != null) {
            try {
                conn.close();
                Log.info("Conexão fechada com sucesso!");
            } catch (SQLException e) {
                Log.error("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}
