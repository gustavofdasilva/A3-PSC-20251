package usuario.pix;

public class PixDTO {
    private int id;
    private int idUsuario;
    private String tipo;
    private String chave;

    
    public PixDTO(int id, int idUsuario, String tipo, String chave) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.chave = chave;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getChave() {
        return chave;
    }
    public void setChave(String chave) {
        this.chave = chave;
    }
}
