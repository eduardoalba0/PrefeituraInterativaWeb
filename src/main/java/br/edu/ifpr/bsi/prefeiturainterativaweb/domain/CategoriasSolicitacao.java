package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class CategoriasSolicitacao implements Serializable {

	public CategoriasSolicitacao() {

	}

	public CategoriasSolicitacao(String _ID) {
		this._ID = _ID;
	}

	private String _ID;
	private String categoria_ID;
	private String solicitacao_ID;

	@Exclude
	private Categoria localCategoria;

	@Exclude
	private Solicitacao localSolicitacao;

//---------------------- Encapsulamento ----------------------

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	
	
	public String getCategoria_ID() {
		return categoria_ID;
	}

	public void setCategoria_ID(String categoria_ID) {
		this.categoria_ID = categoria_ID;
	}

	public String getSolicitacao_ID() {
		return solicitacao_ID;
	}

	public void setSolicitacao_ID(String solicitacao_ID) {
		this.solicitacao_ID = solicitacao_ID;
	}

	public Categoria getLocalCategoria() {
		return localCategoria;
	}

	public void setLocalCategoria(Categoria localCategoria) {
		this.localCategoria = localCategoria;
	}

	public Solicitacao getLocalSolicitacao() {
		return localSolicitacao;
	}

	public void setLocalSolicitacao(Solicitacao localSolicitacao) {
		this.localSolicitacao = localSolicitacao;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CategoriasSolicitacao categoriasSolicitacao = (CategoriasSolicitacao) o;
		return _ID.equals(categoriasSolicitacao._ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_ID);
	}

	@Nonnull
	@Override
	public String toString() {
		return this._ID;
	}
}
