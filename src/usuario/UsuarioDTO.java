package usuario;

public class UsuarioDTO {
    private String nome;
    private String email;

    public UsuarioDTO(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
