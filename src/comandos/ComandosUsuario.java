package comandos;

import java.util.Scanner;

import usuario.UsuarioDAO;
import usuario.UsuarioDTO;
import utils.Log;

public class ComandosUsuario {
    
    public static void mostrarAcoesUsuario() {
        System.out.println("Ações disponíveis para o usuário:");
        System.out.println("1. Transferência");
        System.out.println("2. Saque");
        System.out.println("3. Depósito");
        System.out.println("4. Ver saldo");
        System.out.println("5. Extrato");
        System.out.println("6. Fazer Pix");
        System.out.println("7. Pagar boleto");
        System.out.println("X. Sair");
    }

    public static void loopUsuario(UsuarioDTO usuario) {
        Scanner scanner = new Scanner(System.in);
        String comando;

        do {
            mostrarAcoesUsuario();
            System.out.print("Digite o comando desejado: ");
            comando = scanner.nextLine().toLowerCase();

            switch (comando) {
                case "1":
                    // Lógica para transferência
                    break;
                case "2":
                    // Lógica para saque
                    break;
                case "3":
                    // Lógica para depósito
                    break;
                case "4":
                    // Lógica para ver saldo
                    break;
                case "5":
                    // Lógica para extrato
                    break;
                case "6":
                    // Lógica para fazer Pix
                    break;
                case "7":
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

    public static void acaoCriarUsuario(Scanner scanner) {
        System.out.println("Você escolheu criar um usuário.");
        System.out.print("Digite o nome do usuário: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a senha do usuário: ");
        String senha = scanner.nextLine();
        System.out.print("Digite o email do usuário: ");
        String email = scanner.nextLine();
        System.out.print("Digite o telefone do usuário: ");
        int telefone = scanner.nextInt();
        System.out.print("Digite o CPF do usuário: ");
        long cpf = scanner.nextLong();
        scanner.nextLine(); // Consumir a quebra de linha pendente
        System.out.print("Digite o banco do usuário: ");
        String banco = scanner.nextLine();
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuario = new UsuarioDTO(nome, senha, email, telefone, cpf, banco);
        usuarioDAO.criarUsuario(usuario);
    }
    
    public static void acaoLogarUsuario(Scanner scanner) {
        //!Transferencia
        //Saque
        //Depósito
        //Ver saldo
        //Extrato
        //!Meus lançamentos - entradas e saídas
        //!Fazer pix
        //Pagar boleto

        System.out.println("Você escolheu logar um usuário.");
        System.out.println("Você escolheu criar um usuário.");
        System.out.print("Digite o cpf do usuário: ");
        long cpf = scanner.nextLong();
        scanner.nextLine(); // Consumir a quebra de linha pendente
        System.out.print("Digite a senha do usuário: ");
        String senha = scanner.nextLine();
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioLogado = usuarioDAO.autenticarUsuario(cpf, senha);

        if (usuarioLogado != null) {
            System.out.println("Usuário logado com sucesso!");
            System.out.println("Nome: " + usuarioLogado.getNome());
            System.out.println("Email: " + usuarioLogado.getEmail());
            System.out.println("Telefone: " + usuarioLogado.getTelefone());
            System.out.println("Banco: " + usuarioLogado.getBanco());

            loopUsuario(usuarioLogado);
        } else {
            System.out.println("Usuário não encontrado ou senha incorreta.");
        }
    }
}
