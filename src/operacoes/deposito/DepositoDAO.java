package operacoes.deposito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.spi.DirStateFactory.Result;

import bd.BaseDAO;
import usuario.UsuarioDTO;

public class DepositoDAO extends BaseDAO {

    public void realizarDeposito(UsuarioDTO usuario, double quantia) {
        this.conn = conexaoDAO.conectar();

        try {
            int novoId = 0;

            //Inicia transação para, se caso falhe, não grave logs lixo no banco de dados
            conn.setAutoCommit(false);
            String sql = "INSERT INTO operacao (tipo, id_usuario) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, "deposito");
            stmt.setInt(2, usuario.getId());
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                novoId = rs.getInt(1);
            }

            if(novoId == 0) {
                System.err.println("Erro ao realizar transferencia: Operacao nao registrada");
                conn.rollback();
                return;
            }

            //Mudar saldo
            sql = "UPDATE usuario SET saldo = saldo + ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, usuario.getId());
            stmt.execute();


            //Verificar novo saldo
            double novoSaldo = 0;
            sql = "SELECT saldo FROM usuario WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuario.getId());
            rs = stmt.executeQuery();
            if(rs.next()) {
                novoSaldo = rs.getDouble("saldo");
            }

            sql = "INSERT INTO deposito (id, novo_saldo, valor_depositado) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, novoId);
            stmt.setDouble(2, novoSaldo);
            stmt.setDouble(3, quantia);
            stmt.execute();

            conn.commit();
            System.out.println("Deposito realizado com sucesso!");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (Exception rollbackE) {
                System.err.println("Não foi possível realizar o rollback");
            }
            System.out.println("Erro ao realizar deposito: " + e.getMessage());
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
