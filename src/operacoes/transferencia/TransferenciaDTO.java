package operacoes.transferencia;
import java.sql.Time;

import operacoes.OperacaoDTO;

public class TransferenciaDTO extends OperacaoDTO {

    private int idUsuarioRemetente;
    private int idUsuarioDestinatario;
    private int quantia;

    public TransferenciaDTO(String tipo, int id, int idUsuario, Time dtOperacao, int idUsuarioDestinatario, int quantia) {
        super(id, idUsuario, dtOperacao, tipo);
        this.idUsuarioDestinatario = idUsuarioDestinatario;
        this.quantia = quantia;
    }

    public int getQuantia() {
        return quantia;
    }

    public void setQuantia(int quantia) {
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
