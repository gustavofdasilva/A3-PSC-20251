package usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bd.BaseDAO;
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
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
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
                stmt.close();
                return novoUsuario;
            } else {
                return null; 
            }
        } catch (SQLException e) {
            Log.error("Erro ao autenticar usuário: " + e.getMessage());
            return null; 
        }
    }    

    public int buscarNumeroDeDenuncias(int idUsuario) {
        conn = conexaoDAO.conectar();
        try {
            String sql = "SELECT COUNT(*) FROM denuncia WHERE id_usuario_denunciado = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            int denuncias = 0;
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                denuncias = rs.getInt(1);
            }

            stmt.close();

            return denuncias;
        } catch (SQLException e) {
            Log.error("Erro ao buscar número de denuncias feitas ao usuário: " + e.getMessage());
            return 0;
        }
    }

    public void denunciarUsuario(int idUsuarioDenunciado, int idUsuarioDenunciando) {
        conn = conexaoDAO.conectar();
        try {
            //Impedir usuário denunciar si mesmo
            if(idUsuarioDenunciado == idUsuarioDenunciando) {
                System.out.println("Você não pode denunciar a si mesmo");
                return;
            }

            //Quantas vezes essa denuncia foi feita desse usuário
            String sql = "SELECT COUNT(*) FROM denuncia WHERE id_usuario_denunciando = ? AND id_usuario_denunciado = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuarioDenunciando);
            stmt.setInt(2, idUsuarioDenunciado);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if(rs.getInt(1) > 0) {
                    System.out.println("Você já denunciou esse usuário!");
                    return;
                }
            }

            sql = "INSERT INTO denuncia (id_usuario_denunciado, id_usuario_denunciando) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuarioDenunciado);
            stmt.setInt(2, idUsuarioDenunciando);
            stmt.execute();

            stmt.close();
            
            System.out.println("Usuário n° conta "+Integer.toString(idUsuarioDenunciado)+" denunciado!");
        } catch (SQLException e) {
            Log.error("Erro ao denunciar usuário: " + e.getMessage());
        }
    }    
}