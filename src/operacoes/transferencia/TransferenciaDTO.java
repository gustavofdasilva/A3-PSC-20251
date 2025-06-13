package operacoes.transferencia;
import java.sql.Time;
import java.sql.Timestamp;

import operacoes.OperacaoDTO;

public class TransferenciaDTO extends OperacaoDTO {

    private int idUsuarioRemetente;
    private int idUsuarioDestinatario;
    private double quantia;

    public TransferenciaDTO(String tipo, int id, int idUsuario, Timestamp dtOperacao, int idUsuarioDestinatario, double quantia) {
        super(id, idUsuario, dtOperacao, tipo);
        this.idUsuarioDestinatario = idUsuarioDestinatario;
        this.quantia = quantia;
    }

    public double getQuantia() {
        return quantia;
    }

    public void setQuantia(double quantia) {
        this.quantia = quantia;
    }

    public int getIdUsuarioRemetente() {
        return idUsuarioRemetente;
    }

    public void setIdUsuarioRemetente(int idUsuarioRemetente) {
        this.idUsuarioRemetente = idUsuarioRemetente;
    }

    public int getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(int idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }
}
