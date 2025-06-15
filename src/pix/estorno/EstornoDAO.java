package pix.estorno;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bd.BaseDAO;
import usuario.notificacao.NotificacaoDAO;
import utils.FormatarString;

public class EstornoDAO extends BaseDAO {

    //Checa a quantia total de estornos que foi solicitado para o usuário, para evitar de gastar o saldo sem responder ao estorno
    public double checarQuantiaSolicitadaDeEstornos(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        try {
            double quantiaTotalEmAnalise = 0;
            String sql = "SELECT SUM(t.quantia) FROM transferencia t INNER JOIN solicitacao_estorno_pix sep ON t.id = sep.id_transacao AND sep.id_usuario_solicitado = ? AND sep.status = 'AGUARDANDO'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                quantiaTotalEmAnalise = rs.getDouble(1);
            }
            
            stmt.close();
            return quantiaTotalEmAnalise;
        } catch (SQLException e) {
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
            return 0;
        }
    }

    public void solicitarEstornoPix(int idTransacao, int idUsuarioSolicitante, int idUsuarioSolicitado) {
        this.conn = conexaoDAO.conectar();
        try {

            String sql = "SELECT COUNT(*) FROM solicitacao_estorno_pix WHERE id_transacao = ? AND id_usuario_solicitante = ? AND id_usuario_solicitado = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idTransacao);
            stmt.setInt(2, idUsuarioSolicitante);
            stmt.setInt(3, idUsuarioSolicitado);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt(1) > 0) {
                    System.out.println("Solicitacao já criada!");
                    return;
                }
            }
            
            int solicitacaoId = 0;
            sql = "INSERT INTO solicitacao_estorno_pix (id_transacao, id_usuario_solicitante, id_usuario_solicitado) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, idTransacao);
            stmt.setInt(2, idUsuarioSolicitante);
            stmt.setInt(3, idUsuarioSolicitado);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                solicitacaoId = rs.getInt(1);
            }

            double quantia = 0;
            sql = "SELECT quantia FROM transferencia WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idTransacao);
            rs = stmt.executeQuery();
            if (rs.next()) {
                quantia = rs.getDouble("quantia");
            }

            String mensagemNotificao = "O usuário de n° conta "+Integer.toString(idUsuarioSolicitante)+" te enviou um pix, com a quantia de: "+FormatarString.numeroParaReais(quantia)+" por acidente e deseja realizar o estorno.";
            NotificacaoDAO notificacaoDAO = new NotificacaoDAO();
            notificacaoDAO.criarNotificacao(idUsuarioSolicitado, mensagemNotificao, solicitacaoId, "ESTORNO_PIX");
            stmt.close();

            System.out.println("Estorno solicitado! O usuário que recebeu o pix será notificado");
        } catch (SQLException e) {
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
        }
    }

    public ArrayList<EstornoDTO> buscarSolicitacoesDoIdUsuario(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        try {
            String sql = "SELECT sep.status, sep.dt_solicitacao, o.dt_operacao, t.quantia, t.id_usuario_destinatario FROM solicitacao_estorno_pix sep INNER JOIN transferencia t ON sep.id_transacao = t.id INNER JOIN operacao o ON sep.id_transacao = o.id AND sep.id_usuario_solicitante = ? ORDER BY o.dt_operacao DESC;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            ArrayList<EstornoDTO> estornos = new ArrayList<>();
            while (rs.next()) {
                EstornoDTO estorno = new EstornoDTO(
                    rs.getString("status"),
                    rs.getTimestamp("dt_solicitacao"),
                    rs.getTimestamp("dt_operacao"),
                    rs.getDouble("quantia"),
                    rs.getInt("id_usuario_destinatario"));

                estornos.add(estorno);
            }

            stmt.close();
            return estornos;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar solicitacoes: " + e.getMessage());
            return null;
        }
    }

    public void confirmarEstornoPix(int id) {
        this.conn = conexaoDAO.conectar();
        try {
            //Pegar a quantia do pix
            double quantia = 0;
            String sql = "SELECT quantia FROM transferencia t INNER JOIN solicitacao_estorno_pix sep ON t.id = sep.id_transacao AND sep.id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                quantia = rs.getDouble("quantia");
            }

            conn.setAutoCommit(false);
            int novoId = 0;
            sql = "INSERT INTO operacao (tipo, id_usuario) VALUES (?, (SELECT sep.id_usuario_solicitado FROM solicitacao_estorno_pix sep where sep.id = ?))";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, "ESTORNO");
            stmt.setInt(2, id);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                novoId = rs.getInt(1);
            }

            if(novoId == 0) {
                System.err.println("Erro ao realizar transferencia: Operacao nao registrada");
                conn.rollback();
                return;
            }

            sql = "INSERT INTO transferencia (id, id_usuario_destinatario, quantia) VALUES (?, (select sep.id_usuario_solicitante from solicitacao_estorno_pix sep where sep.id = ? ), ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, novoId);
            stmt.setInt(2, id);
            stmt.setDouble(3, quantia);
            stmt.execute();
        
            //Diminuir o saldo do usuario solicitado
            sql = "UPDATE usuario u SET u.saldo = saldo - ? WHERE u.id = (SELECT sep.id_usuario_solicitado FROM solicitacao_estorno_pix sep WHERE u.id = sep.id_usuario_solicitado AND sep.id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            //Aumentar saldo do usuario solcitante
            sql = "UPDATE usuario u SET u.saldo = saldo + ? WHERE u.id = (SELECT sep.id_usuario_solicitante FROM solicitacao_estorno_pix sep WHERE u.id = sep.id_usuario_solicitante AND sep.id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            //Mudar status da solicitacao para APROVADO
            sql = "UPDATE solicitacao_estorno_pix sep SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "APROVADO");
            stmt.setInt(2, id);
            stmt.executeUpdate();

            //Excluir notificacao para o usuario
            sql = "DELETE FROM notificacao WHERE referencia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            stmt.close();
            conn.commit();
            
            System.err.println("Estorno realizado com sucesso!");
        } catch (SQLException e) {
             try {
                conn.rollback();
            } catch (Exception rollbackE) {
                System.err.println("Não foi possível realizar o rollback");
            }
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.err.println("Não foi possível mudar o auto commit para true");
            }
        }
        
    }

    public void cancelarEstornoPix(int id) {
        this.conn = conexaoDAO.conectar();
        try {
            //Mudar status da solicitacao para APROVADO
            String sql = "UPDATE solicitacao_estorno_pix sep SET status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "RECUSADO");
            stmt.setInt(2, id);
            stmt.execute();

            //Excluir notificacao para o usuario
            sql = "DELETE FROM notificacao WHERE referencia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            stmt.close();
            System.err.println("Estorno recusado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
        }
        
    }
}
