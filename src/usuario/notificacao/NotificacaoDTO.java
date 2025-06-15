package usuario.notificacao;

import java.sql.Timestamp;

public class NotificacaoDTO {
    private int id;
    private int idUsuario;
    private String conteudo;
    private int referencia;
    private String tipo;
    private String status;
    private Timestamp dtCriada;

    public NotificacaoDTO(int id, int idUsuario, String conteudo, int referencia, String tipo, String status, Timestamp dtCriada) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.conteudo = conteudo;
        this.referencia = referencia;
        this.tipo = tipo;
        this.status = status;
        this.dtCriada = dtCriada;
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
    public String getConteudo() {
        return conteudo;
    }
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    public int getReferencia() {
        return referencia;
    }
    public void setReferencia(int referencia) {
        this.referencia = referencia;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDtCriada() {
        return dtCriada;
    }

    public void setDtCriada(Timestamp dtCriada) {
        this.dtCriada = dtCriada;
    }

    
}
