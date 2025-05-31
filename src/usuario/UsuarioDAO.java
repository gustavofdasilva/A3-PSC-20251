package usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import bd.ConexaoDAO;

public class UsuarioDAO {
    private Connection conn;
    private ConexaoDAO conexaoDAO;

    public void criarUsuario(String nome, String email) {
        conn = conexaoDAO.conectar();
        try {
            String sql = "INSERT INTO usuarios (nome, email) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.executeUpdate();
            System.out.println("Usuário criado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
        } finally {
            conexaoDAO.fecharConexao();
        }
    }
}