package operacoes;

import java.sql.Time;

public class OperacaoDTO {
    protected int id;
    protected Time dtOperacao;
    protected int idUsuario;
    protected String tipo;

    public OperacaoDTO(int id, int idUsuario, Time dtOperacao, String tipo) {
        this.id = id;
        this.dtOperacao = dtOperacao;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
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

    public Time getDtOperacao() {
        return dtOperacao;
    }

    public void setDtOperacao(Time dtOperacao) {
        this.dtOperacao = dtOperacao;
    }

}
