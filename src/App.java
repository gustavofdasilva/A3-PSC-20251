
import java.util.Scanner;

import comandos.ComandosUsuario;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String comando;
        System.out.println("Bem-vindo ao sistema bancário!");
        
        do {
            mostrarComandosPrincipais();
            System.out.print("Digite o comando desejado: ");
            comando = scanner.nextLine().toLowerCase();
            switch (comando) {
                case "1": //criar usuário
                    ComandosUsuario.acaoCriarUsuario(scanner);
                    break;
                case "2": //logar usuário
                    ComandosUsuario.acaoLogarUsuario(scanner);
                    break;
                
                case "x":
                    System.out.println("Saindo do sistema...");
                    return;

                default:
                    System.out.println("Comando inválido. Tente novamente.");
            }
        } while(!comando.equals("x"));
        scanner.close();
    }

    public static void mostrarComandosPrincipais() {
        System.out.println("Comandos disponíveis:");
        System.out.println("1. Criar usuário");
        System.out.println("2. Logar usuário");
        System.out.println("X. Sair");
    }
}
