package comandos;

import java.util.Scanner;

public abstract class Comandos {

    protected Scanner mainScanner;

    protected Comandos(Scanner scanner) {
        this.mainScanner = scanner;
    }

    public abstract void mostrarAcoes();
    
    public abstract void loop();
}
