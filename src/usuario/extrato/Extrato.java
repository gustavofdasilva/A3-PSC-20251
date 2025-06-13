package usuario.extrato;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import operacoes.OperacaoDAO;
import operacoes.OperacaoDTO;
import operacoes.deposito.DepositoDTO;
import operacoes.saque.SaqueDTO;
import operacoes.transferencia.TransferenciaDTO;
import utils.FormatarString;

public class Extrato {
    
    public static void exibirExtrato(int id_usuario) {
        OperacaoDAO operacaoDAO = new OperacaoDAO();
        List<OperacaoDTO> operacoes = operacaoDAO.buscarOperacoesPorId(id_usuario);

        System.out.println("----- Extrato da Conta ID: " + id_usuario + " -----");

        String ultimoDia = ""; 
        for (OperacaoDTO operacao : operacoes) {

            if(ultimoDia.equals("")) {
                ultimoDia = operacao.getDtOperacao().toString().split(" ")[0];
                System.out.printf("%s -------------------\n",retornaTimestampDia(ultimoDia));
            } else {
                if (!ultimoDia.equals(operacao.getDtOperacao().toString().split(" ")[0])){
                    ultimoDia = operacao.getDtOperacao().toString().split(" ")[0];
                    System.out.println(" ");
                    System.out.printf("%s -------------------\n",retornaTimestampDia(ultimoDia));
                }
            }


            if (operacao instanceof TransferenciaDTO) {
                TransferenciaDTO transferencia = (TransferenciaDTO) operacao;
                
                String infoAdicional = "Conta destinataria: "+Integer.toString(transferencia.getIdUsuarioDestinatario())+" / Quantia: "+Double.toString(transferencia.getQuantia());
                mostrarOperacao(operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);

            } else if (operacao instanceof SaqueDTO) {
                SaqueDTO saque = (SaqueDTO) operacao;
                
                
                String infoAdicional = "Valor sacado: "+FormatarString.numeroParaReais(saque.getValorSacado())+" / Novo saldo registrado: "+FormatarString.numeroParaReais(saque.getNovoSaldo());
                mostrarOperacao(operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);

            } else if (operacao instanceof DepositoDTO) {
                DepositoDTO deposito = (DepositoDTO) operacao;
                
                String infoAdicional = "Valor depositado: "+FormatarString.numeroParaReais(deposito.getValorDepositado())+" / Novo saldo registrado: "+FormatarString.numeroParaReais(deposito.getNovoSaldo());
                mostrarOperacao(operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);
            }
        }

        System.out.println("--------------------------------------------");
    }

    public static void mostrarOperacao(String tipo, Timestamp dtOperacao, String infoAdicional) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String dataFormatada = sdf.format(dtOperacao);
        System.out.println(tipo.toUpperCase()+": "+infoAdicional+" / Feita em: "+ dataFormatada);
    }

    public static String retornaTimestampCompleto(Timestamp data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return sdf.format(data);
    }

    public static String retornaTimestampDia(Timestamp data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }
    public static String retornaTimestampDia(String dataString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Timestamp data = Timestamp.valueOf(dataString+" 00:00:00");
        return sdf.format(data);
    }
}
