package comandos;

import java.util.Scanner;

import pix.estorno.EstornoDAO;
import usuario.notificacao.NotificacaoDTO;
import utils.Log;

public class ComandosEstorno extends Comandos {

    NotificacaoDTO notificacaoAnalisada;
    protected ComandosEstorno(Scanner scanner, NotificacaoDTO notificacao) {
        super(scanner);
        this.notificacaoAnalisada = notificacao;
    }

    public void mostrarAcoes() {
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
}
