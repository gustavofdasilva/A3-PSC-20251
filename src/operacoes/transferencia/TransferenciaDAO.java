package operacoes.transferencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import bd.BaseDAO;
import operacoes.OperacaoDTO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import pix.estorno.EstornoDAO;
import usuario.UsuarioDAO;
import usuario.UsuarioDTO;
import utils.FormatarString;

public class TransferenciaDAO extends BaseDAO {

    public boolean detectarUsuarioSuspeito(int idUsuario) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        int denuncias = usuarioDAO.buscarNumeroDeDenuncias(idUsuario);
        System.out.println("USUÁRIO: "+idUsuario);
        System.out.println("DENUNCIAS DO USUÁRIO: "+denuncias);
        return denuncias >= 5;
    }

    //Retorna true se detectar como golpe, false se não
    public boolean detectarGolpeEstornoPix(int idUsuarioRemtente, int idUsuarioDestinatario, double quantiaParaEnviar) {
        try {   
            //Detectar se foi pra uma conta que o usuário nunca fez
            String sql = "SELECT COUNT(*) FROM transferencia t INNER JOIN operacao o ON t.id = o.id AND o.id_usuario = ? AND t.id_usuario_destinatario = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuarioRemtente);
            stmt.setInt(2, idUsuarioDestinatario);
            ResultSet rs = stmt.executeQuery();
            int transferenciasJaRegistradas = 0;
            if(rs.next()) {
                transferenciasJaRegistradas = rs.getInt(1);
            }

            if (transferenciasJaRegistradas > 0) {
                return false;
            }

            //Quantia que recebi em algum momento, foi enviada pela mesma pessoa que desejo enviar novamente OU a pessoa que vou enviar já teve algum histórico do tipo
            
            //O valor que está enviando é o mesmo ou parecido com algum que recebeu recentemente do mesmo usuario que desejo enviar
            sql = "SELECT quantia FROM transferencia t INNER JOIN operacao o ON t.id = o.id AND t.id_usuario_destinatario = ? AND o.id_usuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuarioRemtente);
            stmt.setInt(2, idUsuarioDestinatario);
            rs = stmt.executeQuery();
            while(rs.next()) {
                double quantiaJaRecebida = rs.getDouble("quantia");

                if(quantiaJaRecebida > quantiaParaEnviar-2 && quantiaJaRecebida < quantiaParaEnviar+2) { //coloca uma tolerancia para evitar ser exatamente igual as quantias. 
                    return true;
                }
            }

            //Analisa quais transferencias onde a quantia recebida é quase igual a quantia que vou enviar
            String sqlDetectarContato = "SELECT COUNT(*) FROM transferencia t INNER JOIN operacao o ON o.id = t.id AND ((o.id_usuario = ? AND t.id_usuario_destinatario = ?) OR (o.id_usuario = ? AND t.id_usuario_destinatario = ?))";
            sql = "SELECT o.id, o.id_usuario, t.id_usuario_destinatario, t.quantia, o.dt_operacao FROM transferencia t INNER JOIN operacao o ON t.id = o.id AND t.id_usuario_destinatario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuarioRemtente);
            rs = stmt.executeQuery();
            while(rs.next()) {
                if(rs.getDouble("quantia") > quantiaParaEnviar-2 && rs.getDouble("quantia") < quantiaParaEnviar+2) { //coloca uma tolerancia para evitar ser exatamente igual as quantias. 
                    TransferenciaDTO transferenciaSuspeita = new TransferenciaDTO(
                        "transferencia",
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getTimestamp("dt_operacao"),
                        rs.getInt("id_usuario_destinatario"),
                        rs.getDouble("quantia")
                    );
                    PreparedStatement stmtDetectarContato = conn.prepareStatement(sqlDetectarContato);
                    stmtDetectarContato.setInt(1, transferenciaSuspeita.getIdUsuario());
                    stmtDetectarContato.setInt(2, idUsuarioDestinatario);
                    stmtDetectarContato.setInt(3, transferenciaSuspeita.getIdUsuario());
                    stmtDetectarContato.setInt(4, idUsuarioDestinatario);
                    ResultSet rsDetectarContato = stmtDetectarContato.executeQuery();
                    if(rsDetectarContato.next()) {
                        if(rsDetectarContato.getInt(1) > 0) {
                            return true;
                        }
                    }
                }
            }

            stmt.close();

            return false;
        } catch (SQLException e) {
            System.out.println("Erro ao detectar golpe do estorno: " + e.getMessage());
            return false;
        }
    }

    public void realizarTransferenciaPix(UsuarioDTO usuarioRementente, String chave, double quantia) {
        this.conn = conexaoDAO.conectar();
        int novoId = 0;
        try {

            if (usuarioRementente.getSaldo() < quantia) {
                System.err.println("Usuario não tem dinheiro suficiente para transferir!");
                return;
            }

            //Checa os estornos pendentes para responder do usuário
            EstornoDAO estornoDAO = new EstornoDAO();
            double quantiaTotalEmAnalise = estornoDAO.checarQuantiaSolicitadaDeEstornos(usuarioRementente.getId());

            if(usuarioRementente.getSaldo()-quantia < quantiaTotalEmAnalise) {
                System.err.println("Usuario não tem dinheiro disponível para sacar!");
                System.err.println("Quantia aguardando análise: "+FormatarString.numeroParaReais(quantiaTotalEmAnalise));
                System.err.println("Responda todas suas solicitações de análise antes de sacar");
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

            if(idUsuarioDestinatario == 0) {
                System.out.println("Usuário não encontrado!");
                return;
            }

            boolean detectouGolpe = detectarGolpeEstornoPix(usuarioRementente.getId(), idUsuarioDestinatario, quantia);
            if (detectouGolpe) {
                System.out.println(" ");
                System.out.println("***GOLPE DETECTADO***");
                System.out.println("Nossos sistemas impediram a transação por detectar uma suspeita de golpe");
                System.out.println("Golpe: Estorno do pix");
                System.out.println("Explicação: O golpista faz um pix para a vítima e diz ser por acidente, tenta negociar uma transferência para voltar o valor e depois faz o estorno do mesmo, recebendo o valor duas vezes");
                System.out.println("LEMBRETE: O sistema bancário possui a função estorno do pix, caso o pix tenha sido feito por acidente, a pessoa poderá te contatar por essa ferramenta, evitando golpes e fraudes");
                System.out.println("SEMRE UTILIZE A FUNÇÃO ESTORNO PARA CASOS COMO ESSE");
                System.out.println(" ");
                System.out.println("*Caso tenha sido uma detecção errada, desconsidere essa mensagem");
                System.out.println(" ");
                System.out.println("Deseja continuar com a transação?");
                System.out.println("(S) Continuar");
                System.out.println("(N) Cancelar");
                Scanner scanner = new Scanner(System.in);
                String comando = scanner.nextLine();
                if (!comando.equalsIgnoreCase("s")) {
                    System.out.println("Transferência cancelada");
                    return;
                }
            }

            boolean detectouUsuarioSuspeito = detectarUsuarioSuspeito(idUsuarioDestinatario);
            if (detectouUsuarioSuspeito) {
                System.out.println(" ");
                System.out.println("***USUÁRIO SUSPEITO DETECTADO***");
                System.out.println("Nossos sistemas impediram a transação por detectar um usuário suspeito");
                System.out.println("O usuário que você está enviando o pix foi denunciado várias vezes no nosso sistema");
                System.out.println("Prossiga apenas se confiar no usuário");
                System.out.println(" ");
                System.out.println("Deseja continuar com a transação?");
                System.out.println("(S) Continuar");
                System.out.println("(N) Cancelar");
                Scanner scanner = new Scanner(System.in);
                String comando = scanner.nextLine();
                if (!comando.equalsIgnoreCase("s")) {
                    System.out.println("Transferência cancelada");
                    return;
                }
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
            stmt.executeUpdate();

            sql = "UPDATE usuario SET saldo = saldo - ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, usuarioRementente.getId());
            stmt.executeUpdate();

            stmt.close();
            
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

            //Checa os estornos pendentes para responder do usuário
            EstornoDAO estornoDAO = new EstornoDAO();
            double quantiaTotalEmAnalise = estornoDAO.checarQuantiaSolicitadaDeEstornos(usuarioRementente.getId());


            if(usuarioRementente.getSaldo()-quantia < quantiaTotalEmAnalise) {
                System.err.println("Usuario não tem dinheiro disponível para sacar!");
                System.err.println("Quantia aguardando análise: "+FormatarString.numeroParaReais(quantiaTotalEmAnalise));
                System.err.println("Responda todas suas solicitações de análise antes de sacar");
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

            if(idUsuarioDestinatario == 0) {
                System.out.println("Usuário não encontrado!");
                return;
            }

            boolean detectouGolpe = detectarGolpeEstornoPix(usuarioRementente.getId(), idUsuarioDestinatario, quantia);
            if (detectouGolpe) {
                System.out.println(" ");
                System.out.println("***GOLPE DETECTADO***");
                System.out.println("Nossos sistemas impediram a transação por detectar uma suspeita de golpe");
                System.out.println("Golpe: Estorno do pix");
                System.out.println("Explicação: O golpista faz um pix para a vítima e diz ser por acidente, tenta negociar uma transferência para voltar o valor e depois faz o estorno do mesmo, recebendo o valor duas vezes");
                System.out.println("LEMBRETE: O sistema bancário possui a função estorno do pix, caso o pix tenha sido feito por acidente, a pessoa poderá te contatar por essa ferramenta, evitando golpes e fraudes");
                System.out.println("SEMRE UTILIZE A FUNÇÃO ESTORNO PARA CASOS COMO ESSE");
                System.out.println(" ");
                System.out.println("*Caso tenha sido uma detecção errada, desconsidere essa mensagem");
                System.out.println(" ");
                System.out.println("Deseja continuar com a transação?");
                System.out.println("(S) Continuar");
                System.out.println("(N) Cancelar");
                Scanner scanner = new Scanner(System.in);
                String comando = scanner.nextLine();
                if (!comando.equalsIgnoreCase("s")) {
                    System.out.println("Transferência cancelada");
                    return;
                }
            }

            boolean detectouUsuarioSuspeito = detectarUsuarioSuspeito(idUsuarioDestinatario);
            if (detectouUsuarioSuspeito) {
                System.out.println(" ");
                System.out.println("***USUÁRIO SUSPEITO DETECTADO***");
                System.out.println("Nossos sistemas impediram a transação por detectar um usuário suspeito");
                System.out.println("O usuário que você está enviando o pix foi denunciado várias vezes no nosso sistema");
                System.out.println("Prossiga apenas se confiar no usuário");
                System.out.println(" ");
                System.out.println("Deseja continuar com a transação?");
                System.out.println("(S) Continuar");
                System.out.println("(N) Cancelar");
                Scanner scanner = new Scanner(System.in);
                String comando = scanner.nextLine();
                if (!comando.equalsIgnoreCase("s")) {
                    System.out.println("Transferência cancelada");
                    return;
                }
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
            stmt.executeUpdate();

            sql = "UPDATE usuario SET saldo = saldo - ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, quantia);
            stmt.setInt(2, usuarioRementente.getId());
            stmt.executeUpdate();

            stmt.close();
            
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

            stmt.close();
            
            return operacoes;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar transferencias pix: " + e.getMessage());
            return null; 
        }
    }
}
