package usuario.extrato;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import operacoes.OperacaoDAO;
import operacoes.OperacaoDTO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import operacoes.transferencia.TransferenciaDTO;

public class Extrato {
    
    public static void exibirExtrato(int id_usuario) {
        OperacaoDAO operacaoDAO = new OperacaoDAO();
        List<OperacaoDTO> operacoes = operacaoDAO.buscarOperacoesPorId(id_usuario);

        System.out.println("----- Extrato da Conta ID: " + id_usuario + " -----");

        for (OperacaoDTO operacao : operacoes) {
            if (operacao instanceof TransferenciaDTO) {
                TransferenciaDTO transferencia = (TransferenciaDTO) operacao;
                
                String infoAdicional = "Conta destinataria: "+Integer.toString(transferencia.getIdUsuarioDestinatario())+" / Quantia: "+Integer.toString(transferencia.getQuantia());
                mostrarOperacao(operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);

            } else if (operacao instanceof SaqueDTO) {
                SaqueDTO saque = (SaqueDTO) operacao;
                
                String infoAdicional = "Valor sacado: "+Double.toString(saque.getValorSacado())+" / Novo saldo registrado: "+Double.toString(saque.getNovoSaldo());
                mostrarOperacao(operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);

            } else if (operacao instanceof DepositoDTO) {
                DepositoDTO deposito = (DepositoDTO) operacao;
                
                String infoAdicional = "Valor depositado: "+Double.toString(deposito.getValorDepositado())+" / Novo saldo registrado: "+Double.toString(deposito.getNovoSaldo());
                mostrarOperacao(operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);
            }
        }

        System.out.println("--------------------------------------------");
    }

    public static void mostrarOperacao(String tipo, Time dtOperacao, String infoAdicional) {
        System.out.println(tipo.toUpperCase()+": "+infoAdicional+" / Feita em: "+ dtOperacao.toString());
    }
}
