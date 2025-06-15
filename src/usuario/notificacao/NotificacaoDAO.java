package usuario.notificacao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bd.BaseDAO;
import utils.Log;
public class NotificacaoDAO extends BaseDAO {

    public ArrayList<NotificacaoDTO> buscarNotificacoes(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        try {
            
            ArrayList<NotificacaoDTO> notificacoes = new ArrayList<>();
            String sql = "SELECT id, conteudo, referencia, tipo, status, dt_criada FROM notificacao WHERE id_usuario = ? ORDER BY status";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NotificacaoDTO notificacao = new NotificacaoDTO(
                    rs.getInt("id"), 
                    idUsuario, 
                    rs.getString("conteudo"), 
                    rs.getInt("referencia"), 
                    rs.getString("tipo"),
                    rs.getString("status"),
                    rs.getTimestamp("dt_criada"));

                notificacoes.add(notificacao);
            }

            stmt.close();

            return notificacoes;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar notificacoes: " + e.getMessage());
            return null; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

    public void criarNotificacao(int idUsuario, String mensagem, int referencia, String tipo) {
        this.conn = conexaoDAO.conectar();
        try {
            String sql = "INSERT INTO notificacao (id_usuario, conteudo, referencia, tipo) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            stmt.setString(2, mensagem);
            stmt.setInt(3, referencia);
            stmt.setString(4, tipo);
            stmt.execute();

            stmt.close();
            
            Log.info("Notificação criada!");
            Log.info(mensagem);
        } catch (SQLException e) {
            Log.error("Erro ao criar notificacao: " + e.getMessage());
        }
    }

    public void mudarStatus(int id, String novoStatus) { //NAO_LIDA / LIDA
        this.conn = conexaoDAO.conectar();
        try {
            
            ArrayList<NotificacaoDTO> notificacoes = new ArrayList<>();
            String sql = "UPDATE notificacao SET status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            stmt.close();
            
            Log.info("Status da notificação alterado!");
        } catch (SQLException e) {
            System.out.println("Erro ao mudar status da notificacao: " + e.getMessage());
        }
    }
}
