package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("serial")
public class Aviso implements Serializable {
	
    public static final String CATEGORIA_PADRAO = "Avisos Gerais",
            CATEGORIA_AVALIACAO = "Avaliação do Atendimento",
            CATEGORIA_TRAMITACAO = "Tramitação de Solicitações";

    private String titulo;
    private String corpo;
    private String solicitacao_ID;
    private String categoria;
    private String token;
    private Date data;

//---------------------- Encapsulamento ----------------------


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getSolicitacao_ID() {
        return solicitacao_ID;
    }

    public void setSolicitacao_ID(String solicitacao_ID) {
        this.solicitacao_ID = solicitacao_ID;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aviso aviso = (Aviso) o;
        return solicitacao_ID.equals(aviso.solicitacao_ID) &&
                categoria.equals(aviso.categoria) &&
                data.equals(aviso.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solicitacao_ID, categoria, data);
    }
}
