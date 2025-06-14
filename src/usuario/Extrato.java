package usuario;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import operacoes.OperacaoDTO.Contexto;
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
                System.out.printf("%s -------------------\n",FormatarString.retornaTimestampDia(ultimoDia));
            } else {
                if (!ultimoDia.equals(operacao.getDtOperacao().toString().split(" ")[0])){
                    ultimoDia = operacao.getDtOperacao().toString().split(" ")[0];
                    System.out.println(" ");
                    System.out.printf("%s -------------------\n",FormatarString.retornaTimestampDia(ultimoDia));
                }
            }


            if (operacao instanceof TransferenciaDTO) {
                TransferenciaDTO transferencia = (TransferenciaDTO) operacao;

                String infoAdicional = "";
                if(transferencia.getContexto() == Contexto.ENTRADA) {
                    infoAdicional = "Quantia recebida: "+FormatarString.numeroParaReais(transferencia.getQuantia());
                } else if (transferencia.getContexto() == Contexto.SAIDA) {
                    infoAdicional = "Conta destinataria: "+Integer.toString(transferencia.getIdUsuarioDestinatario())+" / Quantia enviada: "+FormatarString.numeroParaReais(transferencia.getQuantia());
                }
                mostrarOperacao(operacao.getContexto(), operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);

            } else if (operacao instanceof SaqueDTO) {
                SaqueDTO saque = (SaqueDTO) operacao;
                
                
                String infoAdicional = "Valor sacado: "+FormatarString.numeroParaReais(saque.getValorSacado())+" / Novo saldo registrado: "+FormatarString.numeroParaReais(saque.getNovoSaldo());
                mostrarOperacao(operacao.getContexto(), operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);

            } else if (operacao instanceof DepositoDTO) {
                DepositoDTO deposito = (DepositoDTO) operacao;
                
                String infoAdicional = "Valor depositado: "+FormatarString.numeroParaReais(deposito.getValorDepositado())+" / Novo saldo registrado: "+FormatarString.numeroParaReais(deposito.getNovoSaldo());
                mostrarOperacao(operacao.getContexto(), operacao.getTipo(), operacao.getDtOperacao(), infoAdicional);
            }
        }

        System.out.println("--------------------------------------------");
    }

    public static void mostrarOperacao(String tipo, Timestamp dtOperacao, String infoAdicional) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String dataFormatada = sdf.format(dtOperacao);
        System.out.println(tipo.toUpperCase()+": "+infoAdicional+" / Feita em: "+ dataFormatada);
    }

    public static void mostrarOperacao(Contexto contexto, String tipo, Timestamp dtOperacao, String infoAdicional) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String dataFormatada = sdf.format(dtOperacao);
        String contextoString = "";
        if(contexto == Contexto.ENTRADA) {
            contextoString = "(+)";
        } else if (contexto == Contexto.SAIDA) {
            contextoString = "(-)";
        }

        System.out.println(contextoString+" "+tipo.toUpperCase()+": "+infoAdicional+" / Feita em: "+ dataFormatada);
    }
}
