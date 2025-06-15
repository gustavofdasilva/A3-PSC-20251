package operacoes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import bd.BaseDAO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import operacoes.transferencia.TransferenciaDTO;
public class OperacaoDAO extends BaseDAO {

    public ArrayList<OperacaoDTO> buscarOperacoesPorId(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        ArrayList<OperacaoDTO> operacoes = new ArrayList<>();
        try {
            //Seleciona transferencias
            String sqlTransferencia = "SELECT o.id, o.tipo, o.dt_operacao, o.id_usuario, t.id_usuario_destinatario, t.quantia FROM transferencia t INNER JOIN operacao o ON t.id = o.id AND (o.id_usuario = ? OR t.id_usuario_destinatario = ?) ";
            PreparedStatement stmt = conn.prepareStatement(sqlTransferencia);
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TransferenciaDTO transferencia = new TransferenciaDTO(
                    rs.getString("tipo"), 
                    rs.getInt("id"), 
                    rs.getInt("id_usuario"), 
                    rs.getTimestamp("dt_operacao"), 
                    rs.getInt("id_usuario_destinatario"), 
                    rs.getDouble("quantia")    
                );

                if(idUsuario == rs.getInt("id_usuario")) {//SAIDA: enviou a quantia
                    transferencia.setContexto(OperacaoDTO.Contexto.SAIDA);
                } else if (idUsuario == rs.getInt("id_usuario_destinatario")) {//ENTRADA: recebeu a quantia
                    transferencia.setContexto(OperacaoDTO.Contexto.ENTRADA);
                }

                operacoes.add(transferencia);
            }

            String sqlSaque = "SELECT o.id, o.tipo, o.dt_operacao, o.id_usuario, s.valor_sacado, s.novo_saldo FROM saque s INNER JOIN operacao o ON s.id = o.id AND o.id_usuario = ?";
            stmt = conn.prepareStatement(sqlSaque);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();
            while (rs.next()) {
                SaqueDTO saque = new SaqueDTO(
                    rs.getString("tipo"), 
                    rs.getInt("id"), 
                    rs.getInt("id_usuario"), 
                    rs.getTimestamp("dt_operacao"), 
                    rs.getDouble("novo_saldo"), 
                    rs.getDouble("valor_sacado")    
                );

                saque.setContexto(OperacaoDTO.Contexto.SAIDA);
                operacoes.add(saque);
            }

            String sqlDeposito = "SELECT o.id, o.tipo, o.dt_operacao, o.id_usuario, d.valor_depositado, d.novo_saldo FROM deposito d INNER JOIN operacao o ON d.id = o.id AND o.id_usuario = ?";
            stmt = conn.prepareStatement(sqlDeposito);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();
            while (rs.next()) {
                DepositoDTO deposito = new DepositoDTO(
                    rs.getString("tipo"), 
                    rs.getInt("id"), 
                    rs.getInt("id_usuario"), 
                    rs.getTimestamp("dt_operacao"), 
                    rs.getDouble("novo_saldo"), 
                    rs.getDouble("valor_depositado")    
                );

                deposito.setContexto(OperacaoDTO.Contexto.ENTRADA);
                operacoes.add(deposito);
            }
            stmt.close();
            
            operacoes.sort((o1, o2) -> o2.getDtOperacao().compareTo(o1.getDtOperacao()));

            return operacoes;
        } catch (SQLException e) {
            System.out.println("Erro ao realizar transferencia: " + e.getMessage());
            return null; 
        }
    }

}
