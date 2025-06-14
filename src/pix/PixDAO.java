package pix;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bd.BaseDAO;
public class PixDAO extends BaseDAO {

    public void criarNovaChave(PixDTO pixDTO) {
        this.conn = conexaoDAO.conectar();
        try {
            //Checa se o usuário já tem uma chave desse tipo
            String sql = "SELECT tipo FROM chave_pix WHERE id_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pixDTO.getIdUsuario());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tipo = rs.getString("tipo");
                if (tipo.equalsIgnoreCase(pixDTO.getTipo())) {
                    System.err.printf("Você já tem uma chave pix do tipo %s\n",tipo);
                    return;
                }
            }

            sql = "INSERT INTO chave_pix (id_usuario, tipo, chave) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pixDTO.getIdUsuario());
            stmt.setString(2, pixDTO.getTipo());
            stmt.setString(3, pixDTO.getChave());
            stmt.execute();

            System.out.println("Chave criada com sucesso!");
        } catch (SQLException e) {
            if (e.getSQLState() != null && (e.getSQLState().equals("23505") || e.getMessage().toLowerCase().contains("duplicate"))) {
                System.out.println("Erro: Já existe uma chave PIX com esse valor.");
            } else {
                System.out.println("Erro ao criar chave: " + e.getMessage());
            }
            return; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

    public ArrayList<PixDTO> buscarChavesPix(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        ArrayList<PixDTO> chaves = new ArrayList<>();
        try {
            String sql = "SELECT id, tipo, chave FROM chave_pix WHERE id_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PixDTO pixDTO = new PixDTO(
                    rs.getInt("id"), 
                    idUsuario, 
                    rs.getString("tipo"), 
                    rs.getString("chave"));

                chaves.add(pixDTO);
            }

            return chaves;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar chaves: " + e.getMessage());
            return null; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }


}
