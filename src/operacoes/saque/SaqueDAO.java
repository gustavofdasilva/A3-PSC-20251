package operacoes.saque;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.spi.DirStateFactory.Result;

import bd.BaseDAO;
import pix.estorno.EstornoDAO;
import usuario.UsuarioDTO;
import utils.FormatarString;

public class SaqueDAO extends BaseDAO {

    public void realizarSaque(UsuarioDTO usuario, double quantia) {
        this.conn = conexaoDAO.conectar();

        try {
            int novoId = 0;
            if (usuario.getSaldo() < quantia) {
                System.err.println("Usuario não tem dinheiro suficiente para sacar!");
                return;
            }

            //Checa os estornos pendentes para responder do usuário
            EstornoDAO estornoDAO = new EstornoDAO();
            double quantiaTotalEmAnalise = estornoDAO.checarQuantiaSolicitadaDeEstornos(usuario.getId());

            if(usuario.getSaldo()-quantia < quantiaTotalEmAnalise) {
                System.err.println("Usuario não tem dinheiro disponível para sacar!");
                System.err.println("Quantia aguardando análise: "+FormatarString.numeroParaReais(quantiaTotalEmAnalise));
                System.err.println("Responda todas suas solicitações de análise antes de sacar");
                return;
            }

            //Inicia transação para, se caso falhe, não grave logs lixo no banco de dados
            conn.setAutoCommit(false);
            String sql = "INSERT INTO operacao (tipo, id_usuario) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, "saque");
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
            sql = "UPDATE usuario SET saldo = saldo - ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, usuario.getId());
            stmt.executeUpdate();


            //Verificar novo saldo
            double novoSaldo = 0;
            sql = "SELECT saldo FROM usuario WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuario.getId());
            rs = stmt.executeQuery();
            if(rs.next()) {
                novoSaldo = rs.getDouble("saldo");
            }

            sql = "INSERT INTO saque (id, novo_saldo, valor_sacado) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, novoId);
            stmt.setDouble(2, novoSaldo);
            stmt.setDouble(3, quantia);
            stmt.execute();

            stmt.close();

            conn.commit();
            System.out.println("Saque realizado com sucesso!");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (Exception rollbackE) {
                System.err.println("Não foi possível realizar o rollback");
            }
            System.out.println("Erro ao realizar saque: " + e.getMessage());
            return; 
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.err.println("Não foi possível mudar o auto commit para true");
            }
        }
    }

}
