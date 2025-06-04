package usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bd.ConexaoDAO;

public class UsuarioDAO {
    private Connection conn;
    private ConexaoDAO conexaoDAO;

    public UsuarioDAO() {
        this.conexaoDAO = new ConexaoDAO();
    }

    public void criarUsuario(UsuarioDTO usuario) {
        conn = conexaoDAO.conectar();
        try {
            String sql = "INSERT INTO usuario (nome, senha, email, cpf, telefone, banco) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, Long.toString(usuario.getCpf()));
            stmt.setString(5, Integer.toString(usuario.getTelefone()));
            stmt.setString(6, usuario.getBanco());
            stmt.executeUpdate();
            System.out.println("Usuário criado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

    public UsuarioDTO autenticarUsuario(long cpf, String senha) {
        conn = conexaoDAO.conectar();
        try {
            String sql = "SELECT * FROM usuario WHERE cpf = ? AND senha = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, Long.toString(cpf));
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                int telefone = rs.getInt("telefone");
                String banco = rs.getString("banco");
                return new UsuarioDTO(nome, senha, email, telefone, cpf, banco);
            } else {
                return null; 
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
            return null; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }
}