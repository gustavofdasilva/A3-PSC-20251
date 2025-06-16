package utils;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatarString {
    // Private constructor to prevent instantiation
    private FormatarString() {}

    public static String numeroParaReais(double valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        return formato.format(valor);
    }
    
    public static String retornaTimestampCompleto(Timestamp data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return sdf.format(data);
    }

    public static String retornaTimestampDia(Timestamp data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }
    public static String retornaTimestampDia(String dataString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Timestamp data = Timestamp.valueOf(dataString+" 00:00:00");
        return sdf.format(data);
    }
}
