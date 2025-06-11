package operacoes;

import java.sql.Time;

public class Operacao {
    private int id;
    private Time dtOperacao;

    public Operacao(int id, Time dtOperacao) {
        this.id = id;
        this.dtOperacao = dtOperacao;
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
