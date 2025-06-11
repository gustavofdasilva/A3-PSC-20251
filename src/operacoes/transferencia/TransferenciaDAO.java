package operacoes.transferencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bd.BaseDAO;
import usuario.UsuarioDTO;

public class TransferenciaDAO extends BaseDAO {

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

}
