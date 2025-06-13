package comandos;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import operacoes.deposito.DepositoDAO;
import operacoes.saque.SaqueDAO;
import operacoes.transferencia.TransferenciaDAO;
import operacoes.transferencia.TransferenciaDTO;
import usuario.UsuarioDAO;
import usuario.UsuarioDTO;
import usuario.extrato.Extrato;
import usuario.pix.PixDAO;
import usuario.pix.PixDTO;
import utils.FormatarString;
import utils.Log;

public class ComandosPix extends Comandos {

    Scanner mainScanner;
    UsuarioDTO usuarioLogado;

    public ComandosPix(Scanner scanner, UsuarioDTO usuarioDTO) {
        this.mainScanner = scanner;
        this.usuarioLogado = usuarioDTO;
    }

    public void mostrarAcoes() {
        System.out.println("---------------------------------");
        System.out.println("Ações disponíveis para o pix:");
        System.out.println("1. Transferência por pix");
        System.out.println("2. Criar chave pix");
        System.out.println("3. Gerenciar chave pix");
        System.out.println("4. Gerenciar transações pix");
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
                case "4":
                    acaoGerenciarTransacoes(mainScanner);
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
                    //Estornar pix
                case "x":
                    System.out.println("Voltando para pix...");
                    return;
                default:
                    Log.error("Comando inválido.");
            }
        } while (!comando.equals("x"));
    }

    public void acaoTransferenciaPix(Scanner scanner) {
        System.out.println("Você escolheu fazer uma transferência.");
        System.out.print("Digite a chave: ");
        String chave = scanner.nextLine();
        System.out.print("Digite a quantia: ");
        double quantia = scanner.nextDouble();
        scanner.nextLine();

        TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
        transferenciaDAO.realizarTransferenciaPix(usuarioLogado, chave, quantia);
    }

    public void acaoCriarChave(Scanner scanner) {
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
    }

    public void acaoBuscarChaves(Scanner scanner) {
        System.out.println("Você escolheu fazer acessar suas chaves pix.");
        
        PixDAO pixDAO = new PixDAO();
        ArrayList<PixDTO> chavesPix = pixDAO.buscarChavesPix(this.usuarioLogado.getId());

        for(int i = 0; i < chavesPix.size(); i++) {
            PixDTO pix = chavesPix.get(i);
            System.out.println(pix.getTipo().toUpperCase()+" / chave: "+pix.getChave());
        }
    }

    public void acaoGerenciarTransacoes(Scanner scanner) {
        System.out.println("Você escolheu gerenciar transacoes.");
        
        TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
        ArrayList<TransferenciaDTO> transferencias = transferenciaDAO.buscarTransferenciasPix(this.usuarioLogado.getId());

        if (transferencias.size() == 0) {
            System.out.println("Nenhuma tranferência encontrada!");
            return;
        }

        for(int i = 0; i < transferencias.size(); i++) {
            TransferenciaDTO transferencia = transferencias.get(i);
            String infoAdicional = "Conta enviada: "+Integer.toString(transferencia.getIdUsuarioDestinatario())+ " / Quantia: "+Double.toString(transferencia.getQuantia()) +" / Feita em: "+transferencia.getDtOperacao().toString();
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
            return;
        }
        System.out.println("---------------------------------");
        System.err.println("Enviada para conta n°: "+transferenciaEscolhida.getIdUsuarioDestinatario());
        System.err.println("Quantia: "+transferenciaEscolhida.getQuantia());
        System.err.println("Feita em: "+transferenciaEscolhida.getDtOperacao().toString());
        System.out.println("---------------------------------");
        loopTransacao();
        
    }

    
    private String gerarChaveAleatoria() {
        return UUID.randomUUID().toString().substring(0, 32);
    }
}
