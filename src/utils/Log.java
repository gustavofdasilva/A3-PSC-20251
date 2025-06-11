package utils;

public class Log {
    final static boolean ativarLogInterno = false;

    public static void log(String mensagem) {
        if (ativarLogInterno) {
            System.out.println(mensagem);
        }
    }

    public static void info(String mensagem) {
        if (ativarLogInterno) {
            System.out.println("INFO: " + mensagem);
        }
    }

    public static void error(String mensagem) {
        if (ativarLogInterno) {
            System.err.println("ERROR: " + mensagem);
        }
    }
}
