package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utils.Log;

public class ConexaoDAO {
    private String user = "root";
    private String pass = "root";
    private String host = "localhost";
    private String port = "3306";
    private String dbName = "db_a3";
    
    private Connection conn;

    public Connection conectar() {
        try {
            String url = "jdbc:mysql://"+host+":"+port+"/"+dbName;
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
