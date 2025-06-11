package operacoes.deposito;

import java.sql.Time;

import operacoes.Operacao;

public class DepositoDTO extends Operacao {


    private int idUsuario;
    private double novoSaldo;
    private double valorDepositado;
    
    public DepositoDTO(int id, Time dtOperacao, double novoSaldo, double valorDepositado) {
        super(id, dtOperacao);
        this.novoSaldo = novoSaldo;
        this.valorDepositado = valorDepositado;
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

    public double getValorDepositado() {
        return valorDepositado;
    }

    public void setValorDepositado(double valorDepositado) {
        this.valorDepositado = valorDepositado;
    }

    
}
