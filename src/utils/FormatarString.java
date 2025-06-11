package utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatarString {
    public static String numeroParaReais(double valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formato.format(valor);
    }
}
