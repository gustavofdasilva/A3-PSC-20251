package operacoes.transferencia;
import java.sql.Time;

import operacoes.Operacao;

public class TransferenciaDTO extends Operacao {

    private int idUsuarioRemetente;
    private int idUsuarioDestinatario;
    
    public TransferenciaDTO(int id, Time dtOperacao, int idUsuarioRemetente, int idUsuarioDestinatario) {
        super(id, dtOperacao);
        this.idUsuarioRemetente = idUsuarioRemetente;
        this.idUsuarioDestinatario = idUsuarioDestinatario;
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
