package operacoes;

import java.sql.Timestamp;



public class OperacaoDTO {
    
    public enum Contexto {
        ENTRADA,
        SAIDA
    }

    protected int id;
    protected Timestamp dtOperacao;
    protected int idUsuario;
    protected String tipo;
    private Contexto contexto;

    public OperacaoDTO(int id, int idUsuario, Timestamp dtOperacao, String tipo) {
        this.id = id;
        this.dtOperacao = dtOperacao;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
    }

    public OperacaoDTO(int id, int idUsuario, Timestamp dtOperacao, String tipo, Contexto contexto) {
        this.id = id;
        this.dtOperacao = dtOperacao;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.contexto = contexto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDtOperacao() {
        return dtOperacao;
    }

    public void setDtOperacao(Timestamp dtOperacao) {
        this.dtOperacao = dtOperacao;
    }

    public Contexto getContexto() {
        return contexto;
    }

    public void setContexto(Contexto contexto) {
        this.contexto = contexto;
    }

}
