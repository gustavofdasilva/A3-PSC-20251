package operacoes.transferencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bd.BaseDAO;
import operacoes.OperacaoDTO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import usuario.UsuarioDTO;

public class TransferenciaDAO extends BaseDAO {

    public void realizarTransferenciaPix(UsuarioDTO usuarioRementente, String chave, double quantia) {
        this.conn = conexaoDAO.conectar();
        int novoId = 0;
        try {

            if (usuarioRementente.getSaldo() < quantia) {
                System.err.println("Usuario não tem dinheiro suficiente para transferir!");
                return;
            }

            //Obtem id do destinatario
            String sql = "SELECT id_usuario FROM chave_pix WHERE chave = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, chave);
            ResultSet rs = stmt.executeQuery();
            int idUsuarioDestinatario=0;
            if(rs.next()) {
                idUsuarioDestinatario = rs.getInt("id_usuario");
            }

            //Inicia transação para, se caso falhe, não grave logs lixo no banco de dados
            conn.setAutoCommit(false);
            sql = "INSERT INTO operacao (tipo, id_usuario) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, "transferencia_pix");
            stmt.setInt(2, usuarioRementente.getId());
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

            sql = "INSERT INTO transferencia (id, id_usuario_destinatario, quantia) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, novoId);
            stmt.setInt(2, idUsuarioDestinatario);
            stmt.setDouble(3, quantia);
            stmt.execute();

            //Realiza a transferencia
            sql = "UPDATE usuario SET saldo = saldo + ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, idUsuarioDestinatario);
            stmt.execute();

            sql = "UPDATE usuario SET saldo = saldo - ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, usuarioRementente.getId());
            stmt.execute();
            
            conn.commit();
            System.out.println("Transferencia realizada com sucesso!");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (Exception rollbackE) {
                System.err.println("Não foi possível realizar o rollback");
            }
            System.out.println("Erro ao realizar transferencia: " + e.getMessage());
            return; 
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.err.println("Não foi possível mudar o auto commit para true");
            }
            conexaoDAO.fecharConexao();
        }
    }

    public void realizarTransferencia(UsuarioDTO usuarioRementente, String cpfUsuarioDestinatario, double quantia) {
        this.conn = conexaoDAO.conectar();
        int novoId = 0;
        try {

            if (usuarioRementente.getSaldo() < quantia) {
                System.err.println("Usuario não tem dinheiro suficiente para transferir!");
                return;
            }

            //Obtem id do destinatario
            String sql = "SELECT id FROM usuario WHERE cpf = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpfUsuarioDestinatario);
            ResultSet rs = stmt.executeQuery();
            int idUsuarioDestinatario = 0;
            if(rs.next()) {
                idUsuarioDestinatario = rs.getInt(1);
            }

            //Inicia transação para, se caso falhe, não grave logs lixo no banco de dados
            conn.setAutoCommit(false);
            sql = "INSERT INTO operacao (tipo, id_usuario) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, "transferencia");
            stmt.setInt(2, usuarioRementente.getId());
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

            sql = "INSERT INTO transferencia (id, id_usuario_destinatario, quantia) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, novoId);
            stmt.setInt(2, idUsuarioDestinatario);
            stmt.setDouble(3, quantia);
            stmt.execute();

            //Realiza a transferencia
            sql = "UPDATE usuario SET saldo = saldo + ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, idUsuarioDestinatario);
            stmt.execute();

            sql = "UPDATE usuario SET saldo = saldo - ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, usuarioRementente.getId());
            stmt.execute();
            
            conn.commit();
            System.out.println("Transferencia realizada com sucesso!");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (Exception rollbackE) {
                System.err.println("Não foi possível realizar o rollback");
            }
            System.out.println("Erro ao realizar transferencia: " + e.getMessage());
            return; 
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.err.println("Não foi possível mudar o auto commit para true");
            }
            conexaoDAO.fecharConexao();
        }
    }

    public ArrayList<TransferenciaDTO> buscarTransferenciasPix(int idUsuario) {
        this.conn = conexaoDAO.conectar();
        ArrayList<TransferenciaDTO> operacoes = new ArrayList<>();
        try {
            String sql = "SELECT op.tipo, op.id, op.dt_operacao, op.id_usuario, tr.id_usuario_destinatario, tr.quantia FROM operacao op INNER JOIN transferencia tr ON op.id = tr.id AND op.id_usuario = ? AND op.tipo = ? ORDER BY dt_operacao DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            stmt.setString(2, "transferencia_pix");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TransferenciaDTO transferencia = new TransferenciaDTO(
                    rs.getString("tipo"),
                    rs.getInt("id"), 
                    rs.getInt("id_usuario"),
                    rs.getTimestamp("dt_operacao"), 
                    rs.getInt("id_usuario_destinatario"),
                    rs.getDouble("quantia"));
            
                operacoes.add(transferencia);
            }
            
            return operacoes;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar transferencias pix: " + e.getMessage());
            return null; 
        } finally {
            conexaoDAO.fecharConexao();
        }
    }

}
