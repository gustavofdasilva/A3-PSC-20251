package usuario;

public class UsuarioDTO {
    private int id;
    private String nome;
    private String senha;
    private String email;
    private String telefone;
    private String cpf;
    private double saldo;
    private String banco;

    public UsuarioDTO(String nome, String senha, String email, String telefone, String cpf, String banco, double saldo) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.banco = banco;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getBanco() {
        return banco;
    }

}
