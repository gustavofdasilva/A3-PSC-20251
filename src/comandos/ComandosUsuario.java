package comandos;

import java.util.Scanner;

import operacoes.deposito.DepositoDAO;
import operacoes.saque.SaqueDAO;
import operacoes.transferencia.TransferenciaDAO;
import usuario.UsuarioDAO;
import usuario.UsuarioDTO;
import usuario.extrato.Extrato;
import utils.FormatarString;
import utils.Log;

public class ComandosUsuario extends Comandos {

    Scanner mainScanner;
    UsuarioDTO usuarioLogado;

    public ComandosUsuario(Scanner scanner) {
        this.mainScanner = scanner;
    }

    public void mostrarInfoUsuarioAtualizado() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioDTO = usuarioDAO.autenticarUsuario(usuarioLogado.getCpf(), usuarioLogado.getSenha());

        this.usuarioLogado = usuarioDTO;

        String saldoFormatado = FormatarString.numeroParaReais(this.usuarioLogado.getSaldo());
        System.out.println("---------------------------------");
        System.out.println("INFORMAÇÕES DO USUÁRIO");
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
        System.out.println("5. Fazer Pix");
        System.out.println("6. Pagar boleto");
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
                    // Lógica para fazer Pix
                    break;
                case "6":
                    // Lógica para pagar boleto
                    break;
                
                case "x":
                    System.out.println("Deslogando do sistema...");
                    return;
                default:
                    Log.error("Comando inválido.");
            }
        } while (!comando.equals("x"));
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
