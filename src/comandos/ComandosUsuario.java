package comandos;

import java.util.ArrayList;
import java.util.Scanner;

import operacoes.deposito.DepositoDAO;
import operacoes.saque.SaqueDAO;
import operacoes.transferencia.TransferenciaDAO;
import operacoes.transferencia.TransferenciaDTO;
import pix.estorno.EstornoDAO;
import usuario.Extrato;
import usuario.UsuarioDAO;
import usuario.UsuarioDTO;
import usuario.notificacao.NotificacaoDAO;
import usuario.notificacao.NotificacaoDTO;
import utils.FormatarString;
import utils.Log;

public class ComandosUsuario extends Comandos {

    Scanner mainScanner;
    UsuarioDTO usuarioLogado;
    NotificacaoDTO notificacaoAnalisada;
    ComandosPix comandosPix;

    public ComandosUsuario(Scanner scanner) {
        this.mainScanner = scanner;
        comandosPix = new ComandosPix(scanner, usuarioLogado);
    }

    public void mostrarInfoUsuarioAtualizado() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioDTO = usuarioDAO.autenticarUsuario(usuarioLogado.getCpf(), usuarioLogado.getSenha());

        this.usuarioLogado = usuarioDTO;
        comandosPix = new ComandosPix(mainScanner, usuarioLogado);

        String saldoFormatado = FormatarString.numeroParaReais(this.usuarioLogado.getSaldo());

        NotificacaoDAO notificacaoDAO = new NotificacaoDAO();
        ArrayList<NotificacaoDTO> notificacoes = notificacaoDAO.buscarNotificacoes(usuarioDTO.getId());
        int notificacoesNaoLidas = 0;
        for (int i = 0; i < notificacoes.size(); i ++) {
            if (notificacoes.get(i).getStatus().equals("NAO_LIDA")) {
                notificacoesNaoLidas++;
            }
        }

        System.out.println("---------------------------------");
        System.out.println("INFORMAÇÕES DO USUÁRIO");
        if(notificacoesNaoLidas > 0) {
            System.out.printf("VOCÊ POSSUI (%d) NOTIFICAÇÕES NÃO LIDAS - DIGITE 6 PARA VER\n",notificacoesNaoLidas);
        }
        System.out.println("Nome: " + usuarioDTO.getNome());
        System.out.println("Email: " + usuarioDTO.getEmail());
        System.out.println("Telefone: " + usuarioDTO.getTelefone());
        System.out.println("CPF: " + usuarioDTO.getCpf());
        System.out.println("Banco: " + usuarioDTO.getBanco());
        System.out.println("Saldo: " + saldoFormatado);
        System.out.println("---------------------------------");
    }
    
    public void mostrarAcoes() {
        mostrarInfoUsuarioAtualizado();
        System.out.println("---------------------------------");
        System.out.println("Ações disponíveis para o usuário:");
        System.out.println("1. Transferência");
        System.out.println("2. Saque");
        System.out.println("3. Depósito");
        System.out.println("4. Extrato");
        System.out.println("5. Pix");
        System.out.println("6. Notificações");
        System.out.println("X. Sair");
        System.out.println("---------------------------------");
    }

    public void mostrarAcoesConfirmarEstorno() {
        System.out.println("---------------------------------");
        System.out.println("Deseja confirmar o estorno do pix enviado para você?");
        System.out.println("Ações disponíveis:");
        System.out.println("1. Confimar");
        System.out.println("2. Recusar");
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
                    acaoTransferencia(mainScanner);
                    break;
                case "2":
                    acaoSaque(mainScanner);
                    break;
                case "3":
                    acaoDeposito(mainScanner);
                    break;
                case "4":
                    Extrato.exibirExtrato(usuarioLogado.getId());
                    break;
                case "5":
                    comandosPix.loop();
                    break;
                case "6":
                    acaoVerNotificacoes(mainScanner);
                    break;
                
                case "x":
                    System.out.println("Deslogando do sistema...");
                    return;
                default:
                    Log.error("Comando inválido.");
            }
        } while (!comando.equals("x"));
    }

    public void loopConfirmarEstorno() {
        String comando;

        do {
            mostrarAcoesConfirmarEstorno();
            System.out.print("Digite o comando desejado: ");
            comando = this.mainScanner.nextLine().toLowerCase();

            switch (comando) {
                case "1":
                    System.out.println(" ");
                    System.out.println("Tem certeza que deseja estornar o pix?");
                    System.out.print("Digite (S) para confirmar e (N) para cancelar: ");
                    comando = this.mainScanner.nextLine().toLowerCase();
                        if(comando.equals("s")) {
                            acaoEstornarPix();
                            return;
                        } 
                    break;

                case "2":
                    System.out.println(" ");
                    System.out.println("Tem certeza que deseja recusar a solicitação de estorno do pix?");
                    System.out.println("Caso o usuário que te enviou o pix conteste sua resposta, nossos administradores serão acionados para investigar a situação e, caso seja comprovado que o pix foi acidental, o valor será retirado da sua conta sem aviso prévio");
                    System.out.print("Digite (S) para confirmar e (N) para cancelar: ");
                    comando = this.mainScanner.nextLine().toLowerCase();
                        if(comando.equals("s")) {
                            acaoRecusarEstornoPix();
                            return;
                        } 
                    break;
                
                case "x":
                    System.out.println("Deslogando do sistema...");
                    return;
                default:
                    Log.error("Comando inválido.");
            }
        } while (!comando.equals("x"));
    }

    public void acaoRecusarEstornoPix() {
        System.out.println("Você escolheu recusar o estorno do pix.");
        EstornoDAO estornoDAO = new EstornoDAO();
        estornoDAO.cancelarEstornoPix(notificacaoAnalisada.getReferencia());
    }


    public void acaoEstornarPix() {
        System.out.println("Você escolheu estornar o pix.");
        EstornoDAO estornoDAO = new EstornoDAO();
        estornoDAO.confirmarEstornoPix(notificacaoAnalisada.getReferencia());
    }

    public void acaoVerNotificacoes(Scanner scanner) {
        System.out.println("Você escolheu ver suas notificacoes.");

        NotificacaoDAO notificacaoDAO = new NotificacaoDAO();
        ArrayList<NotificacaoDTO> notificacoes = notificacaoDAO.buscarNotificacoes(usuarioLogado.getId());

        if (notificacoes.size() == 0) {
            System.out.println("Nenhuma notificação encontrada!");
            return;
        }

        System.out.println(" ");
        for(int i = 0; i < notificacoes.size(); i++) {
            NotificacaoDTO notificacao = notificacoes.get(i);
            if (notificacao.getStatus().equalsIgnoreCase("NAO_LIDA")) {
                System.out.println(Integer.toString(i+1)+". NOVA NOTIFICACAO: "+ notificacao.getConteudo());
            } else {
                System.out.println(Integer.toString(i+1)+". "+ notificacao.getConteudo());
            }
        }
        System.out.println(" ");

        System.out.println("Digite qual notificacao deseja analisar (número ao começo da linha):");
        int idxNotificacao = scanner.nextInt();
        scanner.nextLine();
        NotificacaoDTO notificacaoEscolhida = null;
        try {
            notificacaoEscolhida = notificacoes.get(idxNotificacao-1);
        } catch (IndexOutOfBoundsException e) {
            notificacaoAnalisada = null;
            System.out.println("Notificação não encontrada!");
            return;
        }
        notificacaoAnalisada = notificacaoEscolhida;

        notificacaoDAO.mudarStatus(notificacaoEscolhida.getId(), "LIDA");
        System.out.println("---------------------------------");
        System.err.println("Mensagem: "+notificacaoEscolhida.getConteudo());
        System.err.println("Criada em: "+FormatarString.retornaTimestampCompleto(notificacaoEscolhida.getDtCriada()));
        System.out.println("---------------------------------");
        loopConfirmarEstorno();
        notificacaoAnalisada = null;
    }

    public void acaoSaque(Scanner scanner) {
        System.out.println("Você escolheu fazer um saque.");
        System.out.print("Digite a quantia desejada: ");
        double quantia = scanner.nextDouble();
        scanner.nextLine();
        
        SaqueDAO saqueDAO = new SaqueDAO();
        saqueDAO.realizarSaque(usuarioLogado, quantia);
    }

    public void acaoDeposito(Scanner scanner) {
        System.out.println("Você escolheu fazer um deposito.");
        System.out.print("Digite a quantia desejada: ");
        double quantia = scanner.nextDouble();
        scanner.nextLine();
        
        DepositoDAO depositoDAO = new DepositoDAO();
        depositoDAO.realizarDeposito(usuarioLogado, quantia);
    }

    public void acaoTransferencia(Scanner scanner) {
        System.out.println("Você escolheu fazer uma transferência.");
        System.out.print("Digite o cpf do usuário destinatário: ");
        String cpf = scanner.nextLine();
        try {
            Long.parseLong(cpf);
        } catch (Exception e) {
            System.out.println("Digite apenas números para o cpf!");
            return;
        }
        System.out.print("Digite a quantia desejada: ");
        double quantia = scanner.nextDouble();
        scanner.nextLine();
        
        TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
        transferenciaDAO.realizarTransferencia(usuarioLogado, cpf, quantia);

    }

    public void acaoCriarUsuario(Scanner scanner) {
        System.out.println("Você escolheu criar um usuário.");
        System.out.print("Digite o nome do usuário: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a senha do usuário: ");
        String senha = scanner.nextLine();
        System.out.print("Digite o email do usuário: ");
        String email = scanner.nextLine();
        System.out.print("Digite o telefone (Apenas digitos e com DDD) do usuário: ");
        String telefone = scanner.nextLine();
        try {
            Long.parseLong(telefone);
        } catch (Exception e) {
            System.out.println("Digite apenas números para o telefone!");
            return;
        }
        System.out.print("Digite o CPF do usuário (Apenas digitos): ");
        String cpf = scanner.nextLine();
        try {
            Long.parseLong(cpf);
        } catch (Exception e) {
            System.out.println("Digite apenas números para o cpf!");
            return;
        }
        System.out.print("Digite o banco do usuário: ");
        String banco = scanner.nextLine();
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuario = new UsuarioDTO(nome, senha, email, telefone, cpf, banco, 0);
        usuarioDAO.criarUsuario(usuario);
    }
    
    public void acaoLogarUsuario(Scanner scanner) {
        System.out.println("Você escolheu logar um usuário.");
        System.out.print("Digite o cpf do usuário: ");
        String cpf = scanner.nextLine();
        System.out.print("Digite a senha do usuário: ");
        String senha = scanner.nextLine();
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioLogado = usuarioDAO.autenticarUsuario(cpf, senha);

        if (usuarioLogado != null) {
            this.usuarioLogado = usuarioLogado;
            loop();
        } else {
            System.out.println("Usuário não encontrado ou senha incorreta.");
        }
    }
}
