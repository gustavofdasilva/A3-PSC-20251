package operacoes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bd.BaseDAO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import operacoes.transferencia.TransferenciaDTO;
import usuario.UsuarioDTO;

public class OperacaoDAO extends BaseDAO {

    public ArrayList<OperacaoDTO> buscarOperacoesPorId(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        ArrayList<OperacaoDTO> operacoes = new ArrayList<>();
        try {
            String sql = "SELECT id, dt_operacao, id_usuario, tipo FROM operacao WHERE id_usuario = ? ORDER BY dt_operacao DESC";
            String sqlTransferencia = "SELECT id_usuario_destinatario, quantia FROM transferencia WHERE id = ?";
            String sqlSaque = "SELECT valor_sacado, novo_saldo FROM saque WHERE id = ?";
            String sqlDeposito = "SELECT valor_depositado, novo_saldo FROM deposito WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OperacaoDTO op = new OperacaoDTO(
                    rs.getInt("id"), 
                    rs.getInt("id_usuario"),
                    rs.getTime("dt_operacao"), 
                    rs.getString("tipo"));
                
                switch (op.getTipo()) {
                    case "transferencia":
                            PreparedStatement subStmt = conn.prepareStatement(sqlTransferencia);
                            subStmt.setInt(1,op.getId());
                            ResultSet subRs = subStmt.executeQuery();
                            while (subRs.next()){
                                TransferenciaDTO transferencia = new TransferenciaDTO(
                                    op.getTipo(),
                                    op.getId(),
                                    op.getIdUsuario(),
                                    op.getDtOperacao(),
                                    subRs.getInt("id_usuario_destinatario"),
                                    subRs.getInt("quantia")
                                );

                                operacoes.add(transferencia);
                            }

                        break;

                    case "saque":
                            subStmt = conn.prepareStatement(sqlSaque);
                            subStmt.setInt(1,op.getId());
                            subRs = subStmt.executeQuery();
                            while (subRs.next()){
                                SaqueDTO saque = new SaqueDTO(
                                    op.getTipo(),
                                    op.getId(),
                                    op.getIdUsuario(),
                                    op.getDtOperacao(),
                                    subRs.getInt("novo_saldo"),
                                    subRs.getInt("valor_sacado")
                                );

                                operacoes.add(saque);
                            }

                        break;

                    case "deposito":
                            subStmt = conn.prepareStatement(sqlDeposito);
                            subStmt.setInt(1,op.getId());
                            subRs = subStmt.executeQuery();
                            while (subRs.next()){
                                DepositoDTO deposito = new DepositoDTO(
                                    op.getTipo(),
                                    op.getId(),
                                    op.getIdUsuario(),
                                    op.getDtOperacao(),
                                    subRs.getInt("novo_saldo"),
                                    subRs.getInt("valor_depositado")
                                );

                                operacoes.add(deposito);
                            }

                        break;
                
                    default:
                        break;
                }
            }
            
            return operacoes;
        } catch (SQLException e) {
            System.out.println("Erro ao realizar transferencia: " + e.getMessage());
            return null; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

}
