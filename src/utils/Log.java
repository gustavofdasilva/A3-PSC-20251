package utils;

public class Log {

    public static void log(String mensagem) {
        System.out.println(mensagem);
    }

    public static void info(String mensagem) {
        System.out.println("INFO: " + mensagem);
    }

    public static void error(String mensagem) {
        System.err.println("ERROR: " + mensagem);
    }
}
