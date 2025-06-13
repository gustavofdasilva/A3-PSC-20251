package operacoes.deposito;

import java.sql.Time;
import java.sql.Timestamp;

import operacoes.OperacaoDTO;

public class DepositoDTO extends OperacaoDTO {

    private double novoSaldo;
    private double valorDepositado;
    
    public DepositoDTO(String tipo, int id, int idUsuario, Timestamp dtOperacao, double novoSaldo, double valorDepositado) {
        super(id, idUsuario, dtOperacao, tipo);
        this.novoSaldo = novoSaldo;
        this.valorDepositado = valorDepositado;
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
