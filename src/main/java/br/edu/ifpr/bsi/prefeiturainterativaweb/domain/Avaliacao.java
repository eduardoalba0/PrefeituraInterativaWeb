package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.util.Date;

import com.google.cloud.firestore.annotation.ServerTimestamp;

@SuppressWarnings("serial")
public class Avaliacao implements Serializable {
    private float nota;
    private String comentario;
    private boolean solucionada;
	private boolean staged;

    @ServerTimestamp
    private Date data;

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public boolean isSolucionada() {
        return solucionada;
    }

    public void setSolucionada(boolean solucionada) {
        this.solucionada = solucionada;
    }
    public boolean isStaged() {
		return staged;
	}

	public void setStaged(boolean staged) {
		this.staged = staged;
	}
    @ServerTimestamp
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
