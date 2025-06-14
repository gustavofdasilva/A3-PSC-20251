package pix.estorno;

import java.sql.Timestamp;

public class EstornoDTO {
    
    private String status;
    private Timestamp dtSolicitacao;
    private Timestamp dtOperacao;
    private double quantia;
    private int idUsuarioDestinatario;
    
    public EstornoDTO(String status, Timestamp dtSolicitacao, Timestamp dtOperacao, double quantia, int idUsuarioDestinatario) {
        this.status = status;
        this.dtSolicitacao = dtSolicitacao;
        this.dtOperacao = dtOperacao;
        this.quantia = quantia;
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public Timestamp getDtSolicitacao() {
        return dtSolicitacao;
    }

    public void setDtSolicitacao(Timestamp dtSolicitacao) {
        this.dtSolicitacao = dtSolicitacao;
    }

    public Timestamp getDtOperacao() {
        return dtOperacao;
    }

    public void setDtOperacao(Timestamp dtOperacao) {
        this.dtOperacao = dtOperacao;
    }

    public double getQuantia() {
        return quantia;
    }

    public void setQuantia(double quantia) {
        this.quantia = quantia;
    }

    public int getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(int idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
