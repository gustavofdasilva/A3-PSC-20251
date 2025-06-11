package usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bd.BaseDAO;
import usuario.extrato.ExtratoDTO;
import utils.Log;

public class UsuarioDAO extends BaseDAO{

    public void criarUsuario(UsuarioDTO usuario) {
        conn = conexaoDAO.conectar();
        try {
            String sql = "INSERT INTO usuario (nome, senha, email, cpf, telefone, banco) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getCpf());
            stmt.setString(5, usuario.getTelefone());
            stmt.setString(6, usuario.getBanco());
            stmt.executeUpdate();
            System.out.println("Usuário criado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

    public UsuarioDTO autenticarUsuario(String cpf, String senha) {
        conn = conexaoDAO.conectar();
        try {
            String sql = "SELECT id, nome, email, telefone, banco, saldo FROM usuario WHERE cpf = ? AND senha = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpf);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String banco = rs.getString("banco");
                double saldo = rs.getDouble("saldo");
                UsuarioDTO novoUsuario = new UsuarioDTO(nome, senha, email, telefone, cpf, banco, saldo);
                novoUsuario.setId(id);
                return novoUsuario;
            } else {
                return null; 
            }
        } catch (SQLException e) {
            Log.error("Erro ao autenticar usuário: " + e.getMessage());
            return null; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

    public ExtratoDTO carregarExtrato(UsuarioDTO usuario) {

        return null;
    }

    
}