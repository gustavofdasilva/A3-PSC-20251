package operacoes;

import java.sql.Time;
import java.sql.Timestamp;

public class OperacaoDTO {
    protected int id;
    protected Timestamp dtOperacao;
    protected int idUsuario;
    protected String tipo;

    public OperacaoDTO(int id, int idUsuario, Timestamp dtOperacao, String tipo) {
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

    public Timestamp getDtOperacao() {
        return dtOperacao;
    }

    public void setDtOperacao(Timestamp dtOperacao) {
        this.dtOperacao = dtOperacao;
    }

}
