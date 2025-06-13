package operacoes.saque;
import java.sql.Time;
import java.sql.Timestamp;

import operacoes.OperacaoDTO;

public class SaqueDTO extends OperacaoDTO {

    private double novoSaldo;
    private double valorSacado;
    
    public SaqueDTO(String tipo, int id, int idUsuario, Timestamp dtOperacao, double novoSaldo, double valorSacado) {
        super(id, idUsuario, dtOperacao, tipo);
        this.novoSaldo = novoSaldo;
        this.valorSacado = valorSacado;
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
