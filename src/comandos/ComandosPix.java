package comandos;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import operacoes.deposito.DepositoDAO;
import operacoes.saque.SaqueDAO;
import operacoes.transferencia.TransferenciaDAO;
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
                case "x":
                    System.out.println("Voltando para usuário...");
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

    private String gerarChaveAleatoria() {
        return UUID.randomUUID().toString().substring(0, 32);
    }
}
