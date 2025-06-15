package comandos;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.UUID;

import operacoes.deposito.DepositoDAO;
import operacoes.saque.SaqueDAO;
import operacoes.transferencia.TransferenciaDAO;
import operacoes.transferencia.TransferenciaDTO;
import pix.PixDAO;
import pix.PixDTO;
import pix.estorno.EstornoDAO;
import pix.estorno.EstornoDTO;
import usuario.Extrato;
import usuario.UsuarioDAO;
import usuario.UsuarioDTO;
import utils.FormatarString;
import utils.Log;

public class ComandosPix extends Comandos {

    UsuarioDTO usuarioLogado;
    TransferenciaDTO transferenciaSelecionada;

    public ComandosPix(Scanner scanner, UsuarioDTO usuarioDTO) {
        super(scanner);
        this.usuarioLogado = usuarioDTO;
    }

    public void mostrarAcoes() {
        System.out.println("---------------------------------");
        System.out.println("Ações disponíveis para o pix:");
        System.out.println("1. Transferência por pix");
        System.out.println("2. Criar chave pix");
        System.out.println("3. Gerenciar chave pix");
        System.out.println("4. Gerenciar transações pix");
        System.out.println("5. Minhas solicitações de estorno");
        System.out.println("X. Sair");
        System.out.println("---------------------------------");
    }

    public void mostrarAcoesGerenciarTransacao() {
        System.out.println("---------------------------------");
        System.out.println("Ações disponíveis para a transacao:");
        System.out.println("1. Estornar pix");
        System.out.println("X. Sair");
        System.out.println("---------------------------------");
    }

    public void loop() {
        String comando;

        do {
            mostrarAcoes();
            System.out.print("Digite o comando desejado: ");
            comando = this.mainScanner.nextLine().toLowerCase();

            switch (comando) {
                case "1":
                    acaoTransferenciaPix(mainScanner);
                    break;
                case "2":
                    acaoCriarChave(mainScanner);
                    break;
                case "3":
                    acaoBuscarChaves(mainScanner);
                    break;
                case "4":
                    acaoGerenciarTransacoes(mainScanner);
                    break;
                case "5":
                    acaoMinhasSolicitacoesDeEstorno(mainScanner);
                    break;
                case "x":
                    System.out.println("Voltando para usuário...");
                    return;
                default:
                    Log.error("Comando inválido.");
            }
        } while (!comando.equals("x"));
    }

    public void loopTransacao() {
        String comando;

        do {
            mostrarAcoesGerenciarTransacao();
            System.out.print("Digite o comando desejado: ");
            comando = this.mainScanner.nextLine().toLowerCase();

            switch (comando) {
                case "1":
                    System.out.println(" ");
                    System.out.println("Tem certeza que deseja solicitar o estorno do pix? Sua solicitação será enviada para o usuário que recebeu e a aprovação dele é necessária");
                    System.out.println("Sua solicitação será enviada para o usuário que recebeu o pix e a aprovação dele é necessária para realizar o estorno");
                    System.out.println("Contate nosso suporte para mais informações");
                    System.out.print("Digite (S) para confirmar e (N) para cancelar: ");
                    comando = this.mainScanner.nextLine().toLowerCase();
                        if(comando.equals("s")) {
                            acaoSolicitarEstorno();
                            return;
                        } 
                    break;
                case "x":
                    System.out.println("Voltando para pix...");
                    return;
                default:
                    Log.error("Comando inválido.");
            }
        } while (!comando.equals("x"));
    }

    public void acaoSolicitarEstorno() {
        System.out.println("Você escolheu solicitar o estorno do pix.");
        
        EstornoDAO estornoDAO = new EstornoDAO();
        estornoDAO.solicitarEstornoPix(transferenciaSelecionada.getId(), transferenciaSelecionada.getIdUsuario(), transferenciaSelecionada.getIdUsuarioDestinatario());
    }

    public void acaoTransferenciaPix(Scanner scanner) {
        try {
            
            System.out.println("Você escolheu fazer uma transferência.");
            System.out.print("Digite a chave: ");
            String chave = scanner.nextLine();
            System.out.print("Digite a quantia: ");
            double quantia = scanner.nextDouble();
            scanner.nextLine();
            
            TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
            transferenciaDAO.realizarTransferenciaPix(usuarioLogado, chave, quantia);
        } catch (InputMismatchException e) {
            System.out.println("Valor digitado inválido!");
        }
    }

    public void acaoCriarChave(Scanner scanner) {
        try {
            
            System.out.println("Você escolheu fazer criar uma chave pix.");
            System.out.print("Digite o tipo da chave: 'CPF', 'EMAIL', 'TELEFONE', 'ALEATORIO': ");
            String tipo = scanner.nextLine().toUpperCase();
            String chave = "";
            System.out.println(tipo);
            
            switch (tipo) {
                case "CPF":
                    chave = usuarioLogado.getCpf();
                    break;
                case "TELEFONE":
                    chave = usuarioLogado.getTelefone();
                    break;
                case "EMAIL":
                    chave = usuarioLogado.getEmail();
                    break;
                case "ALEATORIO":
                    chave = gerarChaveAleatoria();
                    break;
                default:
                    System.out.println("Tipo de chave inválido.");
                    return;
            }
            
            PixDAO pixDAO = new PixDAO();
            PixDTO pixDTO = new PixDTO(0, usuarioLogado.getId(), tipo, chave); //Pode ser 0 pois ele vai desconsiderar para criar
            
            pixDAO.criarNovaChave(pixDTO);
        } catch (InputMismatchException e) {
            System.out.println("Valor digitado inválido!");
        }
    }

    public void acaoMinhasSolicitacoesDeEstorno(Scanner scanner) {
        try {
            System.out.println("Você escolheu acessar suas solicitacoes de estorno.");
            
            EstornoDAO estornoDAO = new EstornoDAO();
            ArrayList<EstornoDTO> estornos = estornoDAO.buscarSolicitacoesDoIdUsuario(usuarioLogado.getId());
            
            if(estornos.size() == 0) {
                System.err.println("Você não possui nenhuma solicitação de estorno!");    
                return;
            }
            
            System.err.println(" ");
            for(int i = 0; i < estornos.size(); i++) {
                EstornoDTO estorno = estornos.get(i);
                System.err.println( Integer.toString(i+1)+". Solicitada em: "+FormatarString.retornaTimestampCompleto(estorno.getDtSolicitacao()) +" / Quantia: "+FormatarString.numeroParaReais(estorno.getQuantia())+" / Transferencia realizada em: "+FormatarString.retornaTimestampCompleto(estorno.getDtOperacao())+" / STATUS: "+estorno.getStatus());
            }
        } catch (InputMismatchException e) {
            System.out.println("Valor digitado inválido!");
        }
    }

    public void acaoBuscarChaves(Scanner scanner) {
        try {    
            System.out.println("Você escolheu fazer acessar suas chaves pix.");
            
            PixDAO pixDAO = new PixDAO();
            ArrayList<PixDTO> chavesPix = pixDAO.buscarChavesPix(this.usuarioLogado.getId());
            
            for(int i = 0; i < chavesPix.size(); i++) {
                PixDTO pix = chavesPix.get(i);
                System.out.println(pix.getTipo().toUpperCase()+" / chave: "+pix.getChave());
            }
        } catch (InputMismatchException e) {
            System.out.println("Valor digitado inválido!");
        }
    }

    public void acaoGerenciarTransacoes(Scanner scanner) {
        try {
            
            System.out.println("Você escolheu gerenciar transacoes.");
            
            TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
            ArrayList<TransferenciaDTO> transferencias = transferenciaDAO.buscarTransferenciasPix(this.usuarioLogado.getId());
            
            if (transferencias.size() == 0) {
                System.out.println("Nenhuma tranferência encontrada!");
                return;
            }
            
            for(int i = 0; i < transferencias.size(); i++) {
                TransferenciaDTO transferencia = transferencias.get(i);
                String infoAdicional = "Conta enviada: "+Integer.toString(transferencia.getIdUsuarioDestinatario())+ " / Quantia: "+FormatarString.numeroParaReais(transferencia.getQuantia()) +" / Feita em: "+FormatarString.retornaTimestampCompleto(transferencia.getDtOperacao());
                System.out.println(Integer.toString(i+1)+". "+infoAdicional);
            }
            
            System.out.println("Digite qual transação deseja analisar (número ao começo da linha):");
            int idxTransferencia = scanner.nextInt();
            scanner.nextLine();
            TransferenciaDTO transferenciaEscolhida = null;
            try {
                transferenciaEscolhida = transferencias.get(idxTransferencia-1);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Transação não encontrada!");
                transferenciaSelecionada = null;
                return;
            }
            transferenciaSelecionada = transferenciaEscolhida;
            System.out.println("---------------------------------");
            System.err.println("Enviada para conta n°: "+transferenciaEscolhida.getIdUsuarioDestinatario());
            System.err.println("Quantia: "+FormatarString.numeroParaReais(transferenciaEscolhida.getQuantia()));
            System.err.println("Feita em: "+FormatarString.retornaTimestampCompleto(transferenciaEscolhida.getDtOperacao()));
            System.out.println("---------------------------------");
            loopTransacao();
            transferenciaSelecionada = null;
        } catch (InputMismatchException e) {
            System.out.println("Valor digitado inválido!");
        }
    }

    
    private String gerarChaveAleatoria() {
        return UUID.randomUUID().toString().substring(0, 32);
    }
}
