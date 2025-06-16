package utils;

import java.util.regex.Pattern;

public class ValidarCampos {
    public static boolean cpfValido(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;

        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;
                
            return digito1 == (cpf.charAt(9) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private static final Pattern EMAIL_REGEX = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static boolean emailValido(String email) {
        if (email == null || email.isEmpty()) return false;
        return EMAIL_REGEX.matcher(email).matches();
    }
}
