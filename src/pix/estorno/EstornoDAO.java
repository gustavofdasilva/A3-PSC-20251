package pix.estorno;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bd.BaseDAO;
import operacoes.OperacaoDTO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import usuario.UsuarioDTO;

public class EstornoDAO extends BaseDAO {

    public void solicitarEstornoPix(int idTransacao, int idUsuarioSolicitante, int idUsuarioSolicitado) {
        this.conn = conexaoDAO.conectar();
        try {
            int solicitacaoId = 0;
            String sql = "INSERT INTO solicitacao_estorno_pix (id_transacao, id_usuario_solicitante, id_usuario_solicitado) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, idTransacao);
            stmt.setInt(2, idUsuarioSolicitante);
            stmt.setInt(3, idUsuarioSolicitado);
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                solicitacaoId = rs.getInt(1);
            }

            String mensagemNotificao = "O usuário de n° conta "+Integer.toString(idUsuarioSolicitante)+" te enviou um pix por acidente e deseja realizar o estorno";
            sql = "INSERT INTO notificacao (id_usuario, conteudo, referencia, tipo) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuarioSolicitado);
            stmt.setString(2, mensagemNotificao);
            stmt.setInt(3, solicitacaoId);
            stmt.setString(4, "ESTORNO_PIX");
            stmt.execute();
            
            System.err.println("Estorno solicitado! O usuário que recebeu o pix será notificado");
        } catch (SQLException e) {
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

    public ArrayList<EstornoDTO> buscarSolicitacoesDoIdUsuario(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        try {
            String sql = "SELECT sep.status, sep.dt_solicitacao, o.dt_operacao, t.quantia, t.id_usuario_destinatario FROM solicitacao_estorno_pix sep INNER JOIN transferencia t ON sep.id_transacao = t.id INNER JOIN operacao o ON sep.id_transacao = o.id AND sep.id_usuario_solicitante = ?;";
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

            return estornos;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar solicitacoes: " + e.getMessage());
            return null;
        } finally {
            conexaoDAO.fecharConexao();
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
        
            //Diminuir o saldo do usuario solicitado
            sql = "UPDATE usuario u SET u.saldo = saldo - ? WHERE u.id = (SELECT sep.id_usuario_solicitado FROM solicitacao_estorno_pix sep WHERE u.id = sep.id_usuario_solicitado AND sep.id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, id);
            stmt.execute();

            //Aumentar saldo do usuario solcitante
            sql = "UPDATE usuario u SET u.saldo = saldo + ? WHERE u.id = (SELECT sep.id_usuario_solicitante FROM solicitacao_estorno_pix sep WHERE u.id = sep.id_usuario_solicitante AND sep.id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, id);
            stmt.execute();

            //Mudar status da solicitacao para APROVADO
            sql = "UPDATE solicitacao_estorno_pix sep SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "APROVADO");
            stmt.setInt(2, id);
            stmt.execute();

            //Excluir notificacao para o usuario
            sql = "DELETE FROM notificacao WHERE referencia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();
            
            System.err.println("Estorno realizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
        } finally {
            conexaoDAO.fecharConexao();
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
            stmt.execute();
            
            System.err.println("Estorno recusado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao solicitar estorno pix: " + e.getMessage());
        } finally {
            conexaoDAO.fecharConexao();
        }
        
    }
}
