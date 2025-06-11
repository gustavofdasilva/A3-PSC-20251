package operacoes.saque;
import java.sql.Time;

import operacoes.Operacao;

public class SaqueDTO extends Operacao {


    private int idUsuario;
    private double novoSaldo;
    private double valorSacado;
    
    public SaqueDTO(int id, Time dtOperacao, double novoSaldo, double valorSacado) {
        super(id, dtOperacao);
        this.novoSaldo = novoSaldo;
        this.valorSacado = valorSacado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getNovoSaldo() {
        return novoSaldo;
    }

    public void setNovoSaldo(double novoSaldo) {
        this.novoSaldo = novoSaldo;
    }

    public double getValorSacado() {
        return valorSacado;
    }

    public void setValorSacado(double valorSacado) {
        this.valorSacado = valorSacado;
    }

    
}
